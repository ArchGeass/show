package org.geass.starter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @Description: 启动时引入自定义解析bean
 * @Author: ArchGeass
 * @Date: 2020/7/4,下午3:36
 * @see RequestResponseBodyMethodProcessorDecorator
 */
@Configuration
public class MyConfiguration implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(myProcessor());
    }

    @Bean
    public RequestResponseBodyMethodProcessorDecorator myProcessor() {
        return new RequestResponseBodyMethodProcessorDecorator();
    }

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> handlers) {
        handlers.add(myProcessor());
    }
}
