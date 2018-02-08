package com.nestorrente.jitl;

import com.google.common.collect.Iterables;
import com.nestorrente.jitl.annotation.Cache;
import com.nestorrente.jitl.annotation.UseProcessor;
import com.nestorrente.jitl.annotation.param.Param;
import com.nestorrente.jitl.annotation.param.ParamProviders;
import com.nestorrente.jitl.annotation.param.Params;
import com.nestorrente.jitl.cache.CacheManager;
import com.nestorrente.jitl.cache.CacheStrategy;
import com.nestorrente.jitl.exception.MissingParameterNameException;
import com.nestorrente.jitl.exception.UnregisteredProcessorException;
import com.nestorrente.jitl.exception.WrongAnnotationUseException;
import com.nestorrente.jitl.param.ParamProvider;
import com.nestorrente.jitl.param.ParamProviderRegister;
import com.nestorrente.jitl.processor.NoOpProcessor;
import com.nestorrente.jitl.processor.Processor;
import com.nestorrente.jitl.template.TemplateEngine;
import com.nestorrente.jitl.util.AnnotationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

// TODO refactor this class for doing unit-tests (i.e., a method that allows get the resource path of a class method)
class JitlProxyInvocationHandler implements InvocationHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(JitlProxyInvocationHandler.class);

	private final TemplateEngine templateEngine;
	private final Map<Class<? extends Processor>, Processor> processors;
	private final Collection<String> fileExtensions;
	private final Charset encoding;
	private final CacheStrategy cacheStrategy;
	private final CacheManager cacheManager;
	private final ParamProviderRegister paramProviderRegister;
	private final boolean autoRegisterParamProviders;

	JitlProxyInvocationHandler(
			TemplateEngine templateEngine,
			Map<Class<? extends Processor>, Processor> processors,
			Collection<String> fileExtensions,
			Charset encoding,
			CacheStrategy cacheStrategy,
			CacheManager cacheManager,
			ParamProviderRegister paramProviderRegister,
			boolean autoRegisterParamProviders
	) {
		this.templateEngine = templateEngine;
		this.processors = processors;
		this.fileExtensions = fileExtensions;
		this.encoding = encoding;
		this.cacheStrategy = cacheStrategy;
		this.cacheManager = cacheManager;
		this.paramProviderRegister = paramProviderRegister;
		this.autoRegisterParamProviders = autoRegisterParamProviders;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		Map<String, Object> parameters = this.getTemplateParameters(method, args);

		Processor processor = this.getProcessor(method);
		CacheStrategy cacheStrategy = this.getTemplateCacheType(method);

		String templateContents = new TemplateContentsReader(
				method,
				this.encoding,
				cacheStrategy,
				this.cacheManager,
				() -> Iterables.concat(processor.getFileExtensions(), this.fileExtensions)
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

		Processor processor = this.processors.get(processorClass);

		if(processor == null) {
			throw new UnregisteredProcessorException(String.format("Processor %s is not registered in this %s instance", processorClass.getName(), Jitl.class.getSimpleName()));
		}

		return processor;

	}

	private CacheStrategy getTemplateCacheType(Method method) {
		return AnnotationUtils.getFromMethodOrClass(method, Cache.class)
				.map(Cache::value)
				.orElse(this.cacheStrategy);
	}

	private String renderTemplate(String templateContents, Map<String, Object> parameters) {
		return this.templateEngine.render(templateContents, parameters);
	}

	private Map<String, Object> getTemplateParameters(Method method, Object[] args) {

		// FIXME don't allow using @Params and @Param at the same time

		Optional<String[]> paramsAnnotationValues = AnnotationUtils.get(method, Params.class)
				.map(Params::value);

		if(paramsAnnotationValues.isPresent() && method.getParameterCount() != paramsAnnotationValues.get().length) {
			throw new WrongAnnotationUseException(String.format("@%s annotation must specify the name of all parameters", Params.class.getSimpleName()));
		}

		Map<String, Object> templateParameters = this.getParamsFromProviders(method);

		int paramIndex = 0;

		for(Parameter param : method.getParameters()) {

			Optional<String> paramAnnotationValue = AnnotationUtils.get(param, Param.class)
					.map(Param::value);

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
				.map(c -> this.paramProviderRegister.getParamProvider(c, this.autoRegisterParamProviders));

	}

}
