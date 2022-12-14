package io.tech1.framework.configurations.async;

import io.tech1.framework.properties.ApplicationFrameworkProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executor;

import static io.tech1.framework.domain.properties.utilities.PropertiesAsserter.assertProperties;
import static io.tech1.framework.domain.utilities.processors.ProcessorsUtility.getHalfOfCores;

@Configuration
@EnableAsync
@EnableScheduling
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ApplicationAsync implements AsyncConfigurer {

    // Properties
    private final ApplicationFrameworkProperties applicationFrameworkProperties;

    @PostConstruct
    public void init() {
        assertProperties(this.applicationFrameworkProperties.getAsyncConfigs(), "asyncConfigs");
    }

    @Override
    public Executor getAsyncExecutor() {
        var threadNamePrefix = this.applicationFrameworkProperties.getAsyncConfigs().getThreadNamePrefix();
        var taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(getHalfOfCores());
        taskExecutor.setThreadNamePrefix(threadNamePrefix);
        taskExecutor.initialize();
        return taskExecutor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }
}
