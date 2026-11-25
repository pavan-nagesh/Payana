package com.payana.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {
  @Override public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/api/**").allowedOrigins("http://localhost:5500","http://127.0.0.1:5500","http://localhost:5501","http://127.0.0.1:5501","http://localhost:5502","http://127.0.0.1:5502","http://localhost:8080","http://127.0.0.1:8080").allowedMethods("*");
  }
}
