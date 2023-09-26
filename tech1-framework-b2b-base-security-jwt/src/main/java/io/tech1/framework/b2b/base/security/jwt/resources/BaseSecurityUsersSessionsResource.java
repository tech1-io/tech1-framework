package io.tech1.framework.b2b.base.security.jwt.resources;

import io.tech1.framework.b2b.base.security.jwt.annotations.AbstractFrameworkBaseSecurityResource;
import io.tech1.framework.b2b.base.security.jwt.assistants.current.CurrentSessionAssistant;
import io.tech1.framework.b2b.base.security.jwt.cookies.CookieProvider;
import io.tech1.framework.b2b.base.security.jwt.domain.dto.responses.ResponseUserSessionsTable;
import io.tech1.framework.b2b.base.security.jwt.domain.identifiers.UserSessionId;
import io.tech1.framework.b2b.base.security.jwt.domain.security.CurrentClientUser;
import io.tech1.framework.b2b.base.security.jwt.services.BaseUsersSessionsService;
import io.tech1.framework.b2b.base.security.jwt.validators.BaseUsersSessionsRequestsValidator;
import io.tech1.framework.domain.exceptions.cookie.CookieAccessTokenNotFoundException;
import io.tech1.framework.properties.ApplicationFrameworkProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Slf4j
@AbstractFrameworkBaseSecurityResource
@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BaseSecurityUsersSessionsResource {

    // Assistants
    private final CurrentSessionAssistant currentSessionAssistant;
    // Services
    private final BaseUsersSessionsService baseUsersSessionsService;
    // Cookie
    private final CookieProvider cookieProvider;
    // Validators
    private final BaseUsersSessionsRequestsValidator baseUsersSessionsRequestsValidator;
    // Properties
    private final ApplicationFrameworkProperties applicationFrameworkProperties;

    @GetMapping
    public ResponseUserSessionsTable getCurrentUserDbSessions(HttpServletRequest httpServletRequest) throws CookieAccessTokenNotFoundException {
        var cookie = this.cookieProvider.readJwtAccessToken(httpServletRequest);
        return this.currentSessionAssistant.getCurrentUserDbSessionsTable(cookie);
    }

    @GetMapping("/current")
    public CurrentClientUser getCurrentClientUser(HttpServletRequest httpServletRequest) throws CookieAccessTokenNotFoundException {
        var user = this.currentSessionAssistant.getCurrentClientUser();
        if (this.applicationFrameworkProperties.getSecurityJwtConfigs().getSessionConfigs().getEnableSessionsMetadataRenewCron().isEnabled()) {
            var session = this.currentSessionAssistant.getCurrentUserSession(httpServletRequest);
            this.baseUsersSessionsService.renewUserRequestMetadataCron(user.getUsername(), session, httpServletRequest);
        }
        return user;
    }

    @PostMapping("/renew/manually/{sessionId}")
    public void renewManually(@PathVariable UserSessionId sessionId, HttpServletRequest httpServletRequest) {
        var user = this.currentSessionAssistant.getCurrentJwtUser();
        this.baseUsersSessionsRequestsValidator.validateAccess(user.username(), sessionId);
        this.baseUsersSessionsService.renewUserRequestMetadataManually(user.username(), sessionId, httpServletRequest);
    }

    @DeleteMapping("/{sessionId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteById(@PathVariable UserSessionId sessionId) {
        var username = this.currentSessionAssistant.getCurrentUsername();
        this.baseUsersSessionsRequestsValidator.validateAccess(username, sessionId);
        this.baseUsersSessionsService.deleteById(sessionId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteAllExceptCurrent(HttpServletRequest httpServletRequest) throws CookieAccessTokenNotFoundException {
        var username = this.currentSessionAssistant.getCurrentUsername();
        var cookie = this.cookieProvider.readJwtAccessToken(httpServletRequest);
        this.baseUsersSessionsService.deleteAllExceptCurrent(username, cookie);
    }
}

