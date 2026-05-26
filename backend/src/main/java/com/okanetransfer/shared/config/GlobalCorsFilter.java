package com.okanetransfer.shared.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class GlobalCorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (!(request instanceof HttpServletRequest httpRequest)
                || !(response instanceof HttpServletResponse httpResponse)) {
            chain.doFilter(request, response);
            return;
        }

        String origin = httpRequest.getHeader("Origin");
        if (isAllowedOrigin(origin)) {
            httpResponse.setHeader("Access-Control-Allow-Origin", origin);
            httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
            httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
            httpResponse.setHeader("Access-Control-Allow-Headers", requestedHeaders(httpRequest));
            httpResponse.setHeader("Access-Control-Expose-Headers", "Authorization");
            httpResponse.setHeader("Access-Control-Max-Age", "3600");
            httpResponse.setHeader("Vary", "Origin");
        }

        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isAllowedOrigin(String origin) {
        return origin != null
                && (origin.startsWith("http://localhost:")
                || origin.startsWith("http://127.0.0.1:"));
    }

    private String requestedHeaders(HttpServletRequest request) {
        String requestedHeaders = request.getHeader("Access-Control-Request-Headers");
        return requestedHeaders == null || requestedHeaders.isBlank()
                ? "Authorization, Content-Type, Accept, Origin, X-Requested-With"
                : requestedHeaders;
    }
}
