package tech1.framework.iam.resources.base;

import io.swagger.v3.oas.annotations.tags.Tag;
import tech1.framework.foundation.domain.exceptions.tokens.AccessTokenNotFoundException;
import tech1.framework.foundation.domain.system.reset_server.ResetServerStatus;
import tech1.framework.iam.annotations.AbstractFrameworkBaseSecurityResource;
import tech1.framework.iam.assistants.current.CurrentSessionAssistant;
import tech1.framework.iam.domain.dto.responses.ResponseInvitationCode;
import tech1.framework.iam.domain.dto.responses.ResponseSuperadminSessionsTable;
import tech1.framework.iam.domain.identifiers.UserSessionId;
import tech1.framework.iam.services.BaseSuperadminService;
import tech1.framework.iam.services.BaseUsersSessionsService;
import tech1.framework.iam.tokens.facade.TokensProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Swagger
@Tag(name = "[tech1-framework] Superadmin API")
// Spring
@Slf4j
@AbstractFrameworkBaseSecurityResource
@RestController
@RequestMapping("/superadmin")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BaseSuperadminResource {

    // Assistants
    private final CurrentSessionAssistant currentSessionAssistant;
    // Services
    private final BaseSuperadminService baseSuperadminService;
    private final BaseUsersSessionsService baseUsersSessionsService;
    // Tokens
    private final TokensProvider tokensProvider;

    // =================================================================================================================
    // Server
    // =================================================================================================================

    @GetMapping("/server/reset/status")
    public ResetServerStatus getResetServerStatus() {
        return this.baseSuperadminService.getResetServerStatus();
    }

    @PostMapping("/server/reset")
    public void resetServer() {
        var user = this.currentSessionAssistant.getCurrentJwtUser();
        this.baseSuperadminService.resetServerBy(user);
    }

    // =================================================================================================================
    // Invitation Codes
    // =================================================================================================================

    @GetMapping("/invitationCodes/unused")
    public List<ResponseInvitationCode> getUnusedInvitationCodes() {
        return this.baseSuperadminService.findUnused();
    }

    // =================================================================================================================
    // Users Sessions
    // =================================================================================================================

    @GetMapping("/sessions")
    public ResponseSuperadminSessionsTable getSessions(HttpServletRequest httpRequest) throws AccessTokenNotFoundException {
        var cookie = this.tokensProvider.readRequestAccessToken(httpRequest);
        return this.baseSuperadminService.getSessions(cookie);
    }

    @PostMapping("/sessions/{sessionId}/renew/manually")
    public void renewManually(@PathVariable UserSessionId sessionId) {
        this.baseUsersSessionsService.enableUserRequestMetadataRenewManually(sessionId);
    }

    @DeleteMapping("/sessions/{sessionId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteById(@PathVariable UserSessionId sessionId) {
        this.baseUsersSessionsService.deleteById(sessionId);
    }

    @DeleteMapping("/sessions")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAllExceptCurrent(HttpServletRequest httpRequest) throws AccessTokenNotFoundException {
        var cookie = this.tokensProvider.readRequestAccessToken(httpRequest);
        this.baseUsersSessionsService.deleteAllExceptCurrentAsSuperuser(cookie);
    }
}

