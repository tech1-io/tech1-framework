package io.tech1.framework.hardware.tests.runners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.tech1.framework.hardware.tests.contexts.ApplicationResourcesContext;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@WebAppConfiguration
@ExtendWith({ SpringExtension.class, MockitoExtension.class })
@ContextConfiguration(classes = {
        ApplicationResourcesContext.class
})
public abstract class ApplicationResourceRunner {

    protected final ObjectMapper objectMapper = new ObjectMapper();

    protected MockMvc mvc;

    public void beforeByResource(Object object) {
        mvc = MockMvcBuilders
                .standaloneSetup(object)
                .build();
    }

    protected String asJsonString(Object value) throws JsonProcessingException {
        return this.objectMapper.writeValueAsString(value);
    }
}
