package io.tech1.framework.b2b.mongodb.security.jwt.configurations;

import io.tech1.framework.properties.ApplicationFrameworkProperties;
import io.tech1.framework.properties.tests.contexts.ApplicationFrameworkPropertiesContext;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.lang.reflect.Method;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ApplicationMongodbTest {

    @Configuration
    @Import({
            ApplicationFrameworkPropertiesContext.class
    })
    @RequiredArgsConstructor(onConstructor = @__(@Autowired))
    static class ContextConfiguration {
        private final ApplicationFrameworkProperties applicationFrameworkProperties;

        @Bean
        ApplicationMongodb applicationMongodb() {
            return new ApplicationMongodb(
                    this.applicationFrameworkProperties
            );
        }
    }

    private final ApplicationMongodb componentUnderTest;

    @Test
    public void beansTests() {
        // Act
        var methods = Stream.of(this.componentUnderTest.getClass().getMethods())
                .map(Method::getName)
                .collect(Collectors.toList());

        // Assert
        assertThat(methods).contains("tech1MongoClient");
        assertThat(methods).contains("tech1MongoDatabaseFactory");
        assertThat(methods).contains("tech1MongoTemplate");
        assertThat(methods).hasSize(12);
        assertThat(this.componentUnderTest.tech1MongoClient()).isNotNull();
        assertThat(this.componentUnderTest.tech1MongoDatabaseFactory()).isNotNull();
        assertThat(this.componentUnderTest.tech1MongoTemplate()).isNotNull();
    }
}
