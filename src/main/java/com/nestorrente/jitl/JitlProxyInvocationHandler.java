package com.nestorrente.jitl;

import com.google.common.collect.Iterables;
import com.nestorrente.jitl.annotation.UseProcessor;
import com.nestorrente.jitl.annotation.cache.CacheTemplate;
import com.nestorrente.jitl.annotation.param.ParamName;
import com.nestorrente.jitl.annotation.param.ParamNames;
import com.nestorrente.jitl.annotation.param.ParamProviders;
import com.nestorrente.jitl.cache.CacheManager;
import com.nestorrente.jitl.cache.MapCacheManager;
import com.nestorrente.jitl.exception.MissingParameterNameException;
import com.nestorrente.jitl.exception.UnregisteredProcessorException;
import com.nestorrente.jitl.exception.WrongAnnotationUseException;
import com.nestorrente.jitl.param.ParamProvider;
import com.nestorrente.jitl.processor.NoOpProcessor;
import com.nestorrente.jitl.processor.Processor;
import com.nestorrente.jitl.util.AnnotationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

// TODO refactor this class for doing unit-tests (i.e., a method that allows get the resource path of a class method)
class JitlProxyInvocationHandler implements InvocationHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(JitlProxyInvocationHandler.class);

	private final JitlConfig config;
	private final CacheManager<Method, String> contentsCacheManager;

	JitlProxyInvocationHandler(JitlConfig config) {
		this.config = config;
		this.contentsCacheManager = new MapCacheManager<>();
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		// TODO crear una clase con información del método, de modo que ésta pueda quedar cacheada y no leerse en cada invocación

		Map<String, Object> parameters = this.readMethodParameters(method, args);

		Processor processor = this.getProcessor(method);
		boolean cacheTemplate = this.isTemplateCacheEnabled(method);

		String templateContents = new TemplateContentsReader(
				method,
				this.config.getEncoding(),
				cacheTemplate,
				this.contentsCacheManager,
				() -> Iterables.concat(processor.getFileExtensions(), this.config.getFileExtensions())
		).getTemplateContents();

		String renderedTemplate = this.renderTemplate(templateContents, parameters);

		return processor.process(method, renderedTemplate, parameters);

	}

	private Processor getProcessor(Method method) {

		Class<?> declaringClass = method.getDeclaringClass();

		Optional<Class<? extends Processor>> processorClassOptional = AnnotationUtils.get(declaringClass, UseProcessor.class)
				.map(UseProcessor::value);

		if(!processorClassOptional.isPresent()) {

			LOGGER.warn("@{0} annotation not present; using No-Operation processor by default", UseProcessor.class.getSimpleName());

			// We use the no-operation processor by default
			return NoOpProcessor.INSTANCE;

		}

		Class<? extends Processor> processorClass = processorClassOptional.get();

		Processor processor = this.config.getProcessors().get(processorClass);

		if(processor == null) {
			throw new UnregisteredProcessorException(String.format("Processor %s is not registered in this %s instance", processorClass.getName(), Jitl.class.getSimpleName()));
		}

		return processor;

	}

	private boolean isTemplateCacheEnabled(Method method) {
		return AnnotationUtils.getFromMethodOrClass(method, CacheTemplate.class)
				.map(CacheTemplate::value)
				.orElse(this.config.isCacheTemplates());
	}

	private String renderTemplate(String templateContents, Map<String, Object> parameters) {
		return this.config.getTemplateEngine().render(templateContents, parameters);
	}

	private Map<String, Object> readMethodParameters(Method method, Object[] args) {

		// FIXME don't allow using @ParamNames and @ParamName at the same time

		Optional<String[]> paramNamesAnnotationValues = AnnotationUtils.get(method, ParamNames.class)
				.map(ParamNames::value);

		if(paramNamesAnnotationValues.isPresent() && method.getParameterCount() != paramNamesAnnotationValues.get().length) {
			throw new WrongAnnotationUseException(String.format("@%s annotation must specify the name of all parameters", ParamNames.class.getSimpleName()));
		}

		Map<String, Object> templateParameters = this.getParamsFromProviders(method);

		int paramIndex = 0;

		for(Parameter param : method.getParameters()) {

			String name = this.getParamName(param, paramIndex, paramNamesAnnotationValues);
			Object value = args[paramIndex];

			templateParameters.put(name, value);

			++paramIndex;

		}

		return templateParameters;

	}

	private String getParamName(Parameter param, int paramIndex, Optional<String[]> paramNamesAnnotationValues) {

		Optional<String> paramNameAnnotationValue = AnnotationUtils.get(param, ParamName.class)
				.map(ParamName::value);

		if(paramNameAnnotationValue.isPresent()) {
			return paramNameAnnotationValue.get();
		}

		if(paramNamesAnnotationValues.isPresent()) {
			return paramNamesAnnotationValues.get()[paramIndex];
		}

		if(param.isNamePresent()) {
			return param.getName();
		}

		throw new MissingParameterNameException("Cannot infer parameter name: " + param.toString());

	}

	private Map<String, Object> getParamsFromProviders(Method method) {

		Map<String, Object> params = new HashMap<>();

		this.getParamProviders(method)
				.map(ParamProvider::params)
				.forEach(params::putAll);

		return params;

	}

	private Stream<ParamProvider> getParamProviders(Method method) {

		Stream<Class<? extends ParamProvider>> methodLevelProviders = AnnotationUtils.get(method, ParamProviders.class)
				.map(ParamProviders::value)
				.map(Arrays::stream)
				.orElseGet(Stream::empty);

		Stream<Class<? extends ParamProvider>> classLevelProviders = AnnotationUtils.get(method.getDeclaringClass(), ParamProviders.class)
				.map(ParamProviders::value)
				.map(Arrays::stream)
				.orElseGet(Stream::empty);

		return Stream.concat(methodLevelProviders, classLevelProviders)
				.map(c -> this.config.getParamProviderRegister().getParamProvider(c, this.config.isParamProviderAutoRegisterEnabled()));

	}

}
