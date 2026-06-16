package com.okanetransfer.shared.config;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import java.util.EnumSet;

public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{AppConfig.class, JpaConfig.class, SecurityConfig.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        // OpenApiConfig is explicitly listed here (NOT via component scan)
        // so that springdoc beans live only in the servlet context.
        return new Class[]{AppConfig.class, JpaConfig.class, OpenApiConfig.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        FilterRegistration.Dynamic cors = servletContext.addFilter("globalCorsFilter", new GlobalCorsFilter());
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "/*");
        super.onStartup(servletContext);
    }
}
