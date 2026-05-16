package com.okanetransfer.config;

import org.springframework.security.access.SecurityConfig;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{AppConfig.class, JpaConfig.class};  // removed SecurityConfig
    }
    @Override
    protected Class<?>[] getServletConfigClasses() { return null; }
    @Override
    protected String[] getServletMappings() { return new String[]{"/"}; }
}
