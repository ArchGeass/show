package org.geass.starter;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 自定义注解解析器
 * @Author: ArchGeass
 * @Date: 2020/7/4,下午3:18
 */
public class RequestResponseBodyMethodProcessorDecorator implements HandlerMethodArgumentResolver, ApplicationContextAware, HandlerMethodReturnValueHandler {

    private RequestResponseBodyMethodProcessor delegate;

    private ApplicationContext applicationContext;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(MyRequestBody.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        //延迟获取delegate
        if (delegate == null) {
            delegate = (RequestResponseBodyMethodProcessor) applicationContext.getBean(RequestMappingHandlerAdapter.class)
                    .getArgumentResolvers().stream()
                    .filter(p -> p instanceof RequestResponseBodyMethodProcessor)
                    .findFirst().get();
        }
        Object resolvedArgument = delegate.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
        if (resolvedArgument instanceof Map) {
            ((Map) resolvedArgument).put("timestamp", System.currentTimeMillis());
        }
        return resolvedArgument;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return (AnnotatedElementUtils.hasAnnotation(returnType.getContainingClass(), MyResponseBody.class) ||
                returnType.hasMethodAnnotation(MyResponseBody.class));
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        if (delegate == null) {
            delegate = (RequestResponseBodyMethodProcessor) applicationContext.getBean(RequestMappingHandlerAdapter.class)
                    .getArgumentResolvers().stream()
                    .filter(p -> p instanceof RequestResponseBodyMethodProcessor)
                    .findFirst().get();
        }
        delegate.handleReturnValue(formatMyReturnJson(returnValue), returnType, mavContainer, webRequest);
    }

    //{"status":"ok", "data": [{"id":1, "name": "ABC"}]}
    private Object formatMyReturnJson(Object returnValue) {
//        JSONObject json = new JSONObject();
//        json.put("status", "ok");
//        json.put("data", returnValue);
//        String proxyReturnValue = json.toString();
        Map<String, Object> proxyReturnValue = new HashMap<>();
        proxyReturnValue.put("status", "ok");
        proxyReturnValue.put("data", returnValue);
        return proxyReturnValue;
    }
}
