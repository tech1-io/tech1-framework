package tech1.framework.iam.tests.runners;

import tech1.framework.iam.tests.contexts.TestsApplicationPropertiesMocked;
import tech1.framework.iam.tests.contexts.TestsApplicationResourcesContext;
import tech1.framework.iam.configurations.ApplicationBaseSecurityJwtMvc;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

@WebAppConfiguration
@ExtendWith({ SpringExtension.class })
@ContextConfiguration(classes = {
        ApplicationBaseSecurityJwtMvc.class,
        TestsApplicationPropertiesMocked.class,
        TestsApplicationResourcesContext.class
})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public abstract class AbstractResourcesRunner2 extends AbstractResourcesRunner {

}
