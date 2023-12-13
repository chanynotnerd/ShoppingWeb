package com.example.shoppingweb.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ShoppingWebMvcConfiguration implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        registry.addInterceptor(
                new AuthenticateInterceptor()).addPathPatterns("/item/{id}");
                // new AuthenticateInterceptor()).addPathPatterns("/");
    }
}
