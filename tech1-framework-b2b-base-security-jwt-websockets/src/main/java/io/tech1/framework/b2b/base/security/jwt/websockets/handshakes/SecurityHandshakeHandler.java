package io.tech1.framework.b2b.base.security.jwt.websockets.handshakes;

import io.tech1.framework.b2b.base.security.jwt.services.TokensService;
import io.tech1.framework.b2b.base.security.jwt.tokens.facade.TokensProvider;
import io.tech1.framework.foundation.domain.exceptions.tokens.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SecurityHandshakeHandler extends DefaultHandshakeHandler {

    // Services
    private final TokensService tokensService;
    // Tokens
    private final TokensProvider tokensProvider;

    @Override
    protected Principal determineUser(
            @NotNull ServerHttpRequest serverHttpRequest,
            @NotNull WebSocketHandler wsHandler,
            @NotNull Map<String, Object> attributes
    ) {
        try {
            var request = ((ServletServerHttpRequest) serverHttpRequest).getServletRequest();
            var cookieAccessToken = this.tokensProvider.readRequestAccessTokenOnWebsocketHandshake(request);
            var cookieRefreshToken = this.tokensProvider.readRequestRefreshTokenOnWebsocketHandshake(request);
            var user = this.tokensService.getJwtUserByAccessTokenOrThrow(cookieAccessToken, cookieRefreshToken);
            return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        } catch (AccessTokenNotFoundException | RefreshTokenNotFoundException |
                 AccessTokenInvalidException |
                 RefreshTokenInvalidException | AccessTokenExpiredException |
                 AccessTokenDbNotFoundException ex) {
            throw new IllegalArgumentException("WebSocket user not determined. Exception: " + ex.getMessage());
        }
    }
}
