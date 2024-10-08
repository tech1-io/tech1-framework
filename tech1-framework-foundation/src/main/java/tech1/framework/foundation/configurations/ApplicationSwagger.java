package tech1.framework.foundation.configurations;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

// Swagger
@OpenAPIDefinition(
        info = @Info(
                title = "${tech1.server-configs.name}",
                version = "${tech1.maven-configs.version}"
        )
)
// Spring
@Configuration
public class ApplicationSwagger {
}
