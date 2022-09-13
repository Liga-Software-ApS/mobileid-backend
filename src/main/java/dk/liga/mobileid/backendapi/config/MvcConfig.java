package dk.liga.mobileid.backendapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
 

@Configuration
public class MvcConfig implements WebMvcConfigurer {
 
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry
            .addViewController("/login")
            .setViewName("login");
         
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
            .addMapping("/**")
            .allowedOrigins("http://localhost:3000");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (!registry.hasMappingForPattern("/static/**")) {
            registry
                .addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
        }
    }
 
}