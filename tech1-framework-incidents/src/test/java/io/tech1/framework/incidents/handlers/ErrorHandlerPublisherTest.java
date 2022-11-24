package io.tech1.framework.incidents.handlers;

import io.tech1.framework.incidents.events.publishers.IncidentPublisher;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ErrorHandlerPublisherTest {

    @Configuration
    static class ContextConfiguration {
        @Bean
        IncidentPublisher incidentPublisher() {
            return mock(IncidentPublisher.class);
        }

        @Bean
        ErrorHandlerPublisher errorHandlerPublisher() {
            return new ErrorHandlerPublisher(
                    this.incidentPublisher()
            );
        }
    }

    // Publisher
    private final IncidentPublisher incidentPublisher;

    private final ErrorHandlerPublisher componentUnderTest;

    @BeforeEach
    public void beforeEach() {
        reset(
                this.incidentPublisher
        );
    }

    @AfterEach
    public void afterEach() {
        verifyNoMoreInteractions(
                this.incidentPublisher
        );
    }

    @Test
    public void handleErrorTest() {
        // Arrange
        var throwable = mock(Throwable.class);

        // Act
        this.componentUnderTest.handleError(throwable);

        // Assert
        verify(this.incidentPublisher).publishThrowable(throwable);
    }
}
