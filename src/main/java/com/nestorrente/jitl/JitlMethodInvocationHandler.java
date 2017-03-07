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
import com.nestorrente.jitl.annotation.Module;
import com.nestorrente.jitl.annotation.Param;
import com.nestorrente.jitl.annotation.Params;
import com.nestorrente.jitl.module.JitlModule;
import com.nestorrente.jitl.util.ReflectionUtils;
import com.nestorrente.jitl.util.ResourceUtils;
import com.nestorrente.jitl.util.StringUtils;

class JitlMethodInvocationHandler implements InvocationHandler {

	private static final class FallbackModule extends JitlModule {

		public static final FallbackModule INSTANCE = new FallbackModule();

		private FallbackModule() {
			super(Collections.emptyList());
		}

	}

	@SuppressWarnings("unused")
	private final Class<?> interfaze; // TODO cachear los métodos (su executor en base a su return type, y su SQL si es @Classpath y procede)

	private final Jitl jitl;

	public JitlMethodInvocationHandler(Class<?> interfaze, Jitl jitl) {
		this.interfaze = interfaze;
		this.jitl = jitl;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		Map<String, Object> parameters = this.getTemplateParameters(method, args);

		JitlModule module = this.getModule(method);

		String renderedTemplate = this.renderTemplate(method, parameters, module);

		return module.postProcess(this.jitl, method, renderedTemplate, parameters);

	}

	private String renderTemplate(Method method, Map<String, Object> parameters, JitlModule module) {

		// TODO separar toda esta clase en funcionalidades que permitan hacer tests unitarios (por ejemplo, de un método sacar la URI de su resource)

		// TODO controlar que el método no esté anotado con más de un tipo de template o con el mismo tipo varias veces (esto puede que no sea posible, ya que las anotaciones no son @Repeatable)

		// TODO investigar diferencia entre method.getAnnotationsByType(...) y method.getDeclaredAnnotationsByType(...)
		// ¿tendrá que ver con la sobrescritura y las anotaciones que tenía el método en la clase padre?

		Optional<InlineTemplate> inlineAnnotation = ReflectionUtils.getAnnotation(method, InlineTemplate.class);

		if(inlineAnnotation.isPresent()) {
			return this.jitl.getTemplateProcessor().renderString(inlineAnnotation.get().value(), parameters);
		}

		String templateUri = this.getTemplateUri(method);

		if(ResourceUtils.resourceExists(templateUri)) {
			return this.jitl.getTemplateProcessor().renderResource(templateUri, parameters);
		}

		for(String extension : Iterables.concat(module.getFileExtensions(), this.jitl.getFileExtensions())) {

			String templateUriWithExtension = templateUri + "." + extension;

			if(ResourceUtils.resourceExists(templateUriWithExtension)) {
				return this.jitl.getTemplateProcessor().renderResource(templateUriWithExtension, parameters);
			}

		}

		// TODO cambiar por una excepción más apropiada
		throw new RuntimeException("Resource not found: " + templateUri);

	}

	private JitlModule getModule(Method method) {

		Class<?> declaringClass = method.getDeclaringClass();

		Optional<Class<? extends JitlModule>> moduleClassOptional = ReflectionUtils.getAnnotationValue(declaringClass, Module.class, a -> a.value());

		if(!moduleClassOptional.isPresent()) {
			return FallbackModule.INSTANCE;
		}

		Class<? extends JitlModule> moduleClass = moduleClassOptional.get();

		JitlModule module = this.jitl.getModules().get(moduleClass);

		if(module == null) {
			// TODO cambiar por una más adecuada
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

		// TODO impedir que se usen @Params y @Param a la vez? Ahora mismo, prevalece @Param

		Optional<String[]> paramsAnnotationValues = ReflectionUtils.getAnnotationValue(method, Params.class, a -> a.value());

		if(paramsAnnotationValues.isPresent() && method.getParameterCount() != paramsAnnotationValues.get().length) {
			// TODO cambiar por una excepción más apropiada
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
				// TODO cambiar por una más adecuada
				throw new RuntimeException("Cannot infer parameter name: " + param.toString());
			}

			templateParameters.put(name, args[paramIndex]);

			++paramIndex;

		}

		return templateParameters;

	}

}
