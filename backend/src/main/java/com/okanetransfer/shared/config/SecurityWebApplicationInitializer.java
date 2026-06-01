package com.okanetransfer.shared.config;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

public class SecurityWebApplicationInitializer extends AbstractSecurityWebApplicationInitializer {
    // Registers springSecurityFilterChain with the servlet container.
    // No-arg constructor delegates to the root ApplicationContext (WebAppInitializer).
}
