package tech1.framework.iam.handlers.exceptions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import tech1.framework.iam.tests.contexts.TestsApplicationHandlersContext;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import static tech1.framework.foundation.domain.exceptions.ExceptionEntityType.ERROR;
import static tech1.framework.foundation.utilities.random.RandomUtility.randomString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JwtAccessDeniedExceptionHandlerTest {

    @Configuration
    @Import({
            TestsApplicationHandlersContext.class
    })
    static class ContextConfiguration {

    }

    private final ObjectMapper objectMapper;

    private final JwtAccessDeniedExceptionHandler componentUnderTest;

    @SuppressWarnings("unchecked")
    @Test
    void handleTest() throws IOException {
        // Arrange
        var response = mock(HttpServletResponse.class);
        var printWriter = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(printWriter);
        var request = mock(HttpServletRequest.class);
        var exception = mock(AccessDeniedException.class);
        var exceptionMessage = randomString();
        when(exception.getMessage()).thenReturn(exceptionMessage);
        var jsonAC = ArgumentCaptor.forClass(String.class);

        // Act
        this.componentUnderTest.handle(request, response, exception);

        // Assert
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(response).getWriter();
        verify(exception, times(2)).getMessage();
        verify(printWriter).write(jsonAC.capture());
        TypeReference<HashMap<String, Object>> typeRef = new TypeReference<>() {};
        HashMap<String, Object> json = objectMapper.readValue(jsonAC.getValue(), typeRef);
        assertThat(json)
                .hasSize(3)
                .containsKeys("exceptionEntityType", "attributes", "timestamp")
                .containsEntry("exceptionEntityType", ERROR.toString());
        var attributes = (Map<String, Object>) json.get("attributes");
        assertThat(attributes)
                .containsEntry("shortMessage", exceptionMessage)
                .containsEntry("fullMessage", exceptionMessage);
        verifyNoMoreInteractions(
                request,
                response,
                exception
        );
    }
}
