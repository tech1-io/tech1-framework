package tech1.framework.foundation.configurations;

import tech1.framework.foundation.domain.properties.ApplicationFrameworkPropertiesTestsHardcodedContext;
import tech1.framework.foundation.services.emails.services.EmailService;
import tech1.framework.foundation.services.emails.services.impl.EmailServiceImpl;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.lang.reflect.Method;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ApplicationEmails2Test {

    @Configuration
    @Import({
            ApplicationFrameworkPropertiesTestsHardcodedContext.class,
            ApplicationEmails.class
    })
    static class ContextConfiguration {

    }

    private final ApplicationEmails componentUnderTest;

    @Test
    void beansTests() {
        // Act
        var methods = Stream.of(this.componentUnderTest.getClass().getMethods())
                .map(Method::getName)
                .collect(Collectors.toList());

        // Assert
        assertThat(methods)
                .contains("javaMailSender")
                .contains("springTemplateEngine")
                .contains("htmlTemplateResolver")
                .contains("emailUtility")
                .contains("emailService")
                .contains("emailServiceSlf4j")
                .hasSize(20);
    }

    @Test
    void javaMailSenderTest() {
        // Act + Assert
        assertThatThrownBy(this.componentUnderTest::javaMailSender)
                .isInstanceOf(NoSuchBeanDefinitionException.class)
                .hasMessage("No bean named 'javaMailSender' available");
    }

    @Test
    void emailServiceTest() {
        // Act + Assert
        assertThatThrownBy(this.componentUnderTest::emailService)
                .isInstanceOf(NoSuchBeanDefinitionException.class)
                .hasMessage("No bean named 'emailService' available");
    }

    @Test
    void emailServiceSlf4jTest() {
        // Act
        var incidentClientDefinition = this.componentUnderTest.emailServiceSlf4j();

        // Assert
        assertThat(incidentClientDefinition.getClass()).isNotEqualTo(EmailService.class);
        assertThat(incidentClientDefinition.getClass()).isNotEqualTo(EmailServiceImpl.class);
    }
}
