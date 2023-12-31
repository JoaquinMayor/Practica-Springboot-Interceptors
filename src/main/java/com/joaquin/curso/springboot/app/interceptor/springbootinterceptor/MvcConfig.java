package com.joaquin.curso.springboot.app.interceptor.springbootinterceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer{ 

    @Qualifier("loadingTimeInterceptor")
    @Autowired
    private HandlerInterceptor timeInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) { //Esto es para poder registrar y usar el interceptor
        registry.addInterceptor(timeInterceptor).addPathPatterns("/app/foo","/app/bar"); //con el metodo addPAthPatterns se ejecuta en las direcciones que ledigamos, si no ponemos el metodo se ejecuta en todos
    }
    
    
}
