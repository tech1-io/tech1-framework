package io.tech1.framework.b2b.base.security.jwt.filters.logging;

import io.tech1.framework.b2b.base.security.jwt.utils.HttpRequestUtils;
import io.tech1.framework.b2b.base.security.jwt.utils.SecurityPrincipalUtils;
import io.tech1.framework.domain.http.cache.CachedBodyHttpServletRequest;
import io.tech1.framework.properties.ApplicationFrameworkProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static io.tech1.framework.b2b.base.security.jwt.utilities.HttpServletRequestUtility.isMultipartRequest;

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
        if (isMultipartRequest(request)) {
            filterChain.doFilter(request, response);
        } else {
            var cachedRequest = new CachedBodyHttpServletRequest(request);
            this.httpRequestUtils.cachePayload(cachedRequest);

            if (this.applicationFrameworkProperties.getSecurityJwtConfigs().getLoggingConfigs().isAdvancedRequestLoggingEnabled()) {
                LOGGER.info("============================================================================================");
                LOGGER.info("Method: (@" + cachedRequest.getMethod() + ", " + cachedRequest.getServletPath() + ")");
                LOGGER.info("Current User: " + this.securityPrincipalUtils.getAuthenticatedUsernameOrUnexpected());
                var payload = cachedRequest.getCachedPayload();
                if (!payload.value().isBlank()) {
                    LOGGER.info("Payload: \n" + payload);
                } else {
                    LOGGER.info("No Payload");
                }
                LOGGER.info("============================================================================================");
            }

            filterChain.doFilter(cachedRequest, response);
        }
    }
}
