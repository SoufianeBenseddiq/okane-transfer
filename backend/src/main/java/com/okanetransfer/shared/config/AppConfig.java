package com.okanetransfer.shared.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.WebJarsResourceResolver;

import java.util.List;

@Configuration
@EnableWebMvc
@EnableScheduling
@PropertySource("classpath:application.properties")
@ComponentScan(
        basePackages = "com.okanetransfer",
        excludeFilters = {
            @ComponentScan.Filter(
                    type = FilterType.ASSIGNABLE_TYPE,
                    classes = JpaConfig.class
            ),
            // OpenApiConfig is excluded from the component scan so it is NOT loaded into
            // the root ApplicationContext. It is explicitly registered only in the
            // DispatcherServlet context (see WebAppInitializer.getServletConfigClasses).
            // This prevents springdoc's @ConditionalOnMissingBean beans from being
            // "found" in the root context, which would cause the servlet context to skip
            // creating them — leaving RequestMappingHandlerMapping with no /v3/api-docs
            // endpoint to register.
            @ComponentScan.Filter(
                    type = FilterType.ASSIGNABLE_TYPE,
                    classes = OpenApiConfig.class
            )
        }
)
public class AppConfig implements WebMvcConfigurer {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // ByteArrayHttpMessageConverter must come first so that byte[] responses
        // (e.g. springdoc's /v3/api-docs) are written as raw bytes, not base64
        // strings by Jackson.
        converters.add(new ByteArrayHttpMessageConverter());
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        converters.add(new MappingJackson2HttpMessageConverter(mapper));
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // springdoc bundles swagger-ui via org.webjars.npm:swagger-ui-dist whose
        // resources live at classpath:/META-INF/resources/webjars/swagger-ui-dist/{version}/.
        // WebJarsResourceResolver (backed by webjars-locator-core) resolves the version
        // automatically; the fallback location covers the resolved full webjar path.
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations(
                    "classpath:/META-INF/resources/webjars/swagger-ui/",
                    "classpath:/META-INF/resources/webjars/")
                .resourceChain(true)
                .addResolver(new WebJarsResourceResolver())
                .addResolver(new PathResourceResolver());
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/")
                .resourceChain(true)
                .addResolver(new WebJarsResourceResolver())
                .addResolver(new PathResourceResolver());
    }

    @Override
public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/api/**")
            .allowedOrigins(
                "http://localhost:4200",
                "http://13.60.74.176",
                "http://13.60.74.176:80",
                "http://13.60.74.176:4200"
            )
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true);
}
}