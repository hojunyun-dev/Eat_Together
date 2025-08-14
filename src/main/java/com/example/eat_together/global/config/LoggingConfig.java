package com.example.eat_together.global.config;

import com.example.eat_together.global.logging.LoggingFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingConfig {
    @Bean
    public FilterRegistrationBean<LoggingFilter> httpLoggingFilter() {
        FilterRegistrationBean<LoggingFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new LoggingFilter());
        bean.setOrder(Integer.MIN_VALUE + 10); // 앞쪽에서 실행
        return bean;
    }
}