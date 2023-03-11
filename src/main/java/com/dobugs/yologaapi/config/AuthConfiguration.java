package com.dobugs.yologaapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.dobugs.yologaapi.auth.AuthInterceptor;
import com.dobugs.yologaapi.auth.support.Connector;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class AuthConfiguration implements WebMvcConfigurer {

    private final Connector dobugsConnector;

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor(dobugsConnector))
            .addPathPatterns("/api/**");
    }
}
