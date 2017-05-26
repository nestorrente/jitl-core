package com.nestorrente.jitl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;
import com.nestorrente.jitl.annotation.BaseClasspath;
import com.nestorrente.jitl.annotation.ClasspathTemplate;
import com.nestorrente.jitl.annotation.Encoding;
import com.nestorrente.jitl.annotation.InlineTemplate;
import com.nestorrente.jitl.annotation.Param;
import com.nestorrente.jitl.annotation.Params;
import com.nestorrente.jitl.annotation.UseModule;
import com.nestorrente.jitl.exception.MissingParameterNameException;
import com.nestorrente.jitl.exception.RuntimeIOException;
import com.nestorrente.jitl.exception.UnregisteredModuleException;
import com.nestorrente.jitl.exception.WrongAnnotationUseException;
import com.nestorrente.jitl.module.Module;
import com.nestorrente.jitl.module.NoOpModule;
import com.nestorrente.jitl.util.ReflectionUtils;
import com.nestorrente.jitl.util.ResourceUtils;
import com.nestorrente.jitl.util.StringUtils;

// TODO refactor this class for doing unit-tests (i.e., a method that allows get the resource path of a class method)
class JitlMethodInvocationHandler implements InvocationHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(JitlMethodInvocationHandler.class);

	@SuppressWarnings("unused")
	private final Class<?> interfaze; // TODO cache the post-processor for every method?

	private final Jitl jitl;

	public JitlMethodInvocationHandler(Class<?> interfaze, Jitl jitl) {
		this.interfaze = interfaze;
		this.jitl = jitl;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		Map<String, Object> parameters = this.getTemplateParameters(method, args);

		Module module = this.getModule(method);

		String renderedTemplate = this.renderTemplate(method, parameters, module);

		return module.postProcess(this.jitl, method, renderedTemplate, parameters);

	}

	private String renderTemplate(Method method, Map<String, Object> parameters, Module module) {

		// TODO don't allow @InlineTemplate and @Classpath template at the same time

		Optional<InlineTemplate> inlineAnnotation = ReflectionUtils.getAnnotation(method, InlineTemplate.class);
		Optional<ClasspathTemplate> classpathAnnotation = ReflectionUtils.getAnnotation(method, ClasspathTemplate.class);

		if(inlineAnnotation.isPresent()) {

			if(classpathAnnotation.isPresent()) {
				throw new WrongAnnotationUseException(String.format("Cannot use @%s and @%s annotations at the same time", InlineTemplate.class.getSimpleName(), ClasspathTemplate.class.getSimpleName()));
			}

			return this.jitl.getTemplateEngine().renderString(inlineAnnotation.get().value(), parameters);

		}

		Charset templateCharset = this.getTemplateCharset(method);
		String templateUri = this.getTemplateUri(method, classpathAnnotation);

		String templateContents = this.getTemplateContents(templateUri, templateCharset, module.getFileExtensions());

		return this.jitl.getTemplateEngine().renderString(templateContents, parameters);

	}

	private Module getModule(Method method) {

		Class<?> declaringClass = method.getDeclaringClass();

		Optional<Class<? extends Module>> moduleClassOptional = ReflectionUtils.getAnnotationValue(declaringClass, UseModule.class, UseModule::value);

		if(!moduleClassOptional.isPresent()) {

			LOGGER.warn("@UseModule not present; using No-Operation module by default");

			// We use the no-operation module by default
			return NoOpModule.INSTANCE;

		}

		Class<? extends Module> moduleClass = moduleClassOptional.get();

		Module module = this.jitl.getModule(moduleClass);

		if(module == null) {
			throw new UnregisteredModuleException("Module " + moduleClass.getName() + " is not registered in this " + Jitl.class.getSimpleName() + " instance");
		}

		return module;

	}

	private String getTemplateContents(String templateUri, Charset templateCharset, Collection<String> moduleFileExtensions) {

		Optional<String> contents = ResourceUtils.getResourceContentsIfExists(templateUri, templateCharset);

		if(contents.isPresent()) {
			return contents.get();
		}

		for(String extension : Iterables.concat(moduleFileExtensions, this.jitl.getFileExtensions())) {

			String templateUriWithExtension = templateUri + "." + extension;

			contents = ResourceUtils.getResourceContentsIfExists(templateUriWithExtension, templateCharset);

			if(contents.isPresent()) {
				return contents.get();
			}

		}

		throw new RuntimeIOException("Resource not found: " + templateUri);

	}

	private Charset getTemplateCharset(Method method) {

		Optional<String> encoding = ReflectionUtils.getAnnotationValue(method, Encoding.class, Encoding::value);

		if(!encoding.isPresent()) {
			encoding = ReflectionUtils.getAnnotationValue(method.getDeclaringClass(), Encoding.class, Encoding::value);
		}

		return encoding.map(Charset::forName).orElseGet(Charset::defaultCharset);

	}

	private String getTemplateUri(Method method, Optional<ClasspathTemplate> classpathAnnotation) {

		Class<?> declaringClass = method.getDeclaringClass();

		String baseClasspathUri = ReflectionUtils.getAnnotationValue(declaringClass, BaseClasspath.class, a -> ResourceUtils.ensureAbsoluteUri(a.value()))
			.orElseGet(() -> ResourceUtils.packageOrClassNameToUri(declaringClass.getName()));

		String templateUri = classpathAnnotation.map(ClasspathTemplate::value)
			.filter(s -> !s.isEmpty())
			.orElseGet(() -> StringUtils.camelToLowerUnderscore(method.getName()));

		if(templateUri.charAt(0) != '/') {
			templateUri = baseClasspathUri + templateUri;
		}

		return templateUri;

	}

	private Map<String, Object> getTemplateParameters(Method method, Object[] args) {

		// TODO don't allow using @Params and @Param at the same time

		Optional<String[]> paramsAnnotationValues = ReflectionUtils.getAnnotationValue(method, Params.class, Params::value);

		if(paramsAnnotationValues.isPresent() && method.getParameterCount() != paramsAnnotationValues.get().length) {
			throw new WrongAnnotationUseException(String.format("@%s annotation must specify the name of all parameters", Params.class.getSimpleName()));
		}

		Map<String, Object> templateParameters = new HashMap<>();

		int paramIndex = 0;

		for(Parameter param : method.getParameters()) {

			Optional<String> paramAnnotationValue = ReflectionUtils.getAnnotationValue(param, Param.class, Param::value);

			String name;

			if(paramAnnotationValue.isPresent()) {

				name = paramAnnotationValue.get();

			} else if(paramsAnnotationValues.isPresent()) {

				name = paramsAnnotationValues.get()[paramIndex];

			} else if(param.isNamePresent()) {

				name = param.getName();

			} else {
				throw new MissingParameterNameException("Cannot infer parameter name: " + param.toString());
			}

			templateParameters.put(name, args[paramIndex]);

			++paramIndex;

		}

		return templateParameters;

	}

}
