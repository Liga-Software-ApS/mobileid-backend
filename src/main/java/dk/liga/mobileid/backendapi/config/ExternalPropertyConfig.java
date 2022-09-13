package dk.liga.mobileid.backendapi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource(value = "file:${external.config.path}")
public class ExternalPropertyConfig {

    @Autowired
    Environment env;

}