package com.dobugs.yologaapi.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.dobugs.yologaapi.auth.AuthInterceptor;
import com.dobugs.yologaapi.auth.TokenExtractor;
import com.dobugs.yologaapi.auth.TokenResolver;
import com.dobugs.yologaapi.auth.support.Connector;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class AuthConfiguration implements WebMvcConfigurer {

    private final Connector dobugsConnector;
    private final TokenExtractor tokenExtractor;

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor(dobugsConnector))
            .addPathPatterns("/api/**");
    }

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new TokenResolver(tokenExtractor));
    }
}
