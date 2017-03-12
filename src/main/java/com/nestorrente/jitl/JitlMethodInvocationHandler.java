package com.nestorrente.jitl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.Iterables;
import com.nestorrente.jitl.annotation.BaseClasspath;
import com.nestorrente.jitl.annotation.ClasspathTemplate;
import com.nestorrente.jitl.annotation.InlineTemplate;
import com.nestorrente.jitl.annotation.Param;
import com.nestorrente.jitl.annotation.Params;
import com.nestorrente.jitl.annotation.UseModule;
import com.nestorrente.jitl.module.Module;
import com.nestorrente.jitl.util.ReflectionUtils;
import com.nestorrente.jitl.util.ResourceUtils;
import com.nestorrente.jitl.util.StringUtils;

// TODO refactor this class for doing unit-tests (i.e., a method that allows get the resource path of a class method)
class JitlMethodInvocationHandler implements InvocationHandler {

	private static final class FallbackModule extends Module {

		public static final FallbackModule INSTANCE = new FallbackModule();

		private FallbackModule() {
			super(Collections.emptyList());
		}

	}

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

		if(inlineAnnotation.isPresent()) {
			return this.jitl.getTemplateEngine().renderString(inlineAnnotation.get().value(), parameters);
		}

		String templateUri = this.getTemplateUri(method);

		if(ResourceUtils.resourceExists(templateUri)) {
			return this.jitl.getTemplateEngine().renderResource(templateUri, parameters);
		}

		for(String extension : Iterables.concat(module.getFileExtensions(), this.jitl.getFileExtensions())) {

			String templateUriWithExtension = templateUri + "." + extension;

			if(ResourceUtils.resourceExists(templateUriWithExtension)) {
				return this.jitl.getTemplateEngine().renderResource(templateUriWithExtension, parameters);
			}

		}

		// TODO replace with a custom exception
		throw new RuntimeException("Resource not found: " + templateUri);

	}

	private Module getModule(Method method) {

		Class<?> declaringClass = method.getDeclaringClass();

		Optional<Class<? extends Module>> moduleClassOptional = ReflectionUtils.getAnnotationValue(declaringClass, UseModule.class, a -> a.value());

		if(!moduleClassOptional.isPresent()) {
			return FallbackModule.INSTANCE;
		}

		Class<? extends Module> moduleClass = moduleClassOptional.get();

		Module module = this.jitl.getModule(moduleClass);

		if(module == null) {
			// TODO replace with a custom exception
			throw new RuntimeException("Module " + moduleClass.getName() + " is not registered in this " + Jitl.class.getSimpleName() + " instance");
		}

		return module;

	}

	private String getTemplateUri(Method method) {

		Class<?> declaringClass = method.getDeclaringClass();

		String baseClasspathUri = ReflectionUtils.getAnnotationValue(declaringClass, BaseClasspath.class, a -> ResourceUtils.ensureAbsoluteUri(a.value()))
			.orElseGet(() -> ResourceUtils.packageOrClassNameToUri(declaringClass.getName()));

		String templateUri = ReflectionUtils.getAnnotationValue(method, ClasspathTemplate.class, a -> a.value())
			.orElseGet(() -> StringUtils.camelToLowerUnderscore(method.getName()));

		if(templateUri.charAt(0) != '/') {
			templateUri = baseClasspathUri + templateUri;
		}

		return templateUri;

	}

	private Map<String, Object> getTemplateParameters(Method method, Object[] args) {

		// TODO don't allow using @Params and @Param at the same time

		Optional<String[]> paramsAnnotationValues = ReflectionUtils.getAnnotationValue(method, Params.class, a -> a.value());

		if(paramsAnnotationValues.isPresent() && method.getParameterCount() != paramsAnnotationValues.get().length) {
			// TODO replace with a custom exception
			throw new RuntimeException("@Params annotation must specify the name of all parameters");
		}

		Map<String, Object> templateParameters = new HashMap<>();

		int paramIndex = 0;

		for(Parameter param : method.getParameters()) {

			Optional<String> paramAnnotationValue = ReflectionUtils.getAnnotationValue(param, Param.class, a -> a.value());

			String name;

			if(paramAnnotationValue.isPresent()) {

				name = paramAnnotationValue.get();

			} else if(paramsAnnotationValues.isPresent()) {

				name = paramsAnnotationValues.get()[paramIndex];

			} else if(param.isNamePresent()) {

				name = param.getName();

			} else {
				// TODO replace with a custom exception
				throw new RuntimeException("Cannot infer parameter name: " + param.toString());
			}

			templateParameters.put(name, args[paramIndex]);

			++paramIndex;

		}

		return templateParameters;

	}

}
