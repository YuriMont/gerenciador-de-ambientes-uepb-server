package dev.uepb.gereciador.ambientes.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
        @Value("${cors.origins}")
        private String corsOrigins;

        @Override
        public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins(corsOrigins.split(","))
                                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS",
                                                "HEAD", "TRACE", "CONNECT")
                                .allowedHeaders("*");
        }
}
