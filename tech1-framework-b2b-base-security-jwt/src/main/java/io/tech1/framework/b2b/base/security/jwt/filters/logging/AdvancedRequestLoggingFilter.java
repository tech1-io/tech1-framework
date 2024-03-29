package io.tech1.framework.b2b.base.security.jwt.filters.logging;

import io.tech1.framework.b2b.base.security.jwt.utils.HttpRequestUtils;
import io.tech1.framework.b2b.base.security.jwt.utils.SecurityPrincipalUtils;
import io.tech1.framework.domain.http.cache.CachedBodyHttpServletRequest;
import io.tech1.framework.properties.ApplicationFrameworkProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.nio.charset.Charset.defaultCharset;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AdvancedRequestLoggingFilter extends OncePerRequestFilter {

    // Utilities
    private final HttpRequestUtils httpRequestUtils;
    private final SecurityPrincipalUtils securityPrincipalUtils;
    // Properties
    private final ApplicationFrameworkProperties applicationFrameworkProperties;

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) throws ServletException, IOException {
        var cachedRequest = new CachedBodyHttpServletRequest(request);
        var payload = new String(cachedRequest.getInputStream().readAllBytes(), defaultCharset());

        if (this.httpRequestUtils.isCachedEndpoint(cachedRequest)) {
            this.httpRequestUtils.cachePayload(cachedRequest, payload);
        }

        if (this.applicationFrameworkProperties.getSecurityJwtConfigs().getLoggingConfigs().isAdvancedRequestLoggingEnabled()) {
            LOGGER.info("============================================================================================");
            LOGGER.info("Method: (@" + cachedRequest.getMethod() + ", " + cachedRequest.getServletPath() + ")");
            LOGGER.info("Current User: " + this.securityPrincipalUtils.getAuthenticatedUsernameOrUnexpected());
            if (!payload.isBlank()) {
                LOGGER.info("Payload: \n" + payload);
            } else {
                LOGGER.info("No Payload");
            }
            LOGGER.info("============================================================================================");
        }

        filterChain.doFilter(cachedRequest, response);
    }
}
