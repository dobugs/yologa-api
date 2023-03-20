package com.dobugs.yologaapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.dobugs.yologaapi.support.FileGenerator;
import com.dobugs.yologaapi.support.logging.FileLogger;
import com.dobugs.yologaapi.support.logging.LoggingAspect;

@EnableAspectJAutoProxy
@Configuration
public class AspectConfiguration {

    @Value("${logging.file-path}")
    private String savedDirectory;

    @Bean
    public FileLogger fileLogger() {
        return new FileLogger(FileGenerator.getInstance(), savedDirectory);
    }

    @Bean
    public LoggingAspect loggingAspect() {
        return new LoggingAspect(fileLogger());
    }
}
