package io.tech1.framework.b2b.mongodb.security.jwt.services;

import io.tech1.framework.b2b.base.security.jwt.assistants.userdetails.JwtUserDetailsService;
import io.tech1.framework.b2b.base.security.jwt.services.abstracts.AbstractTokensContextThrowerService;
import io.tech1.framework.b2b.base.security.jwt.utils.SecurityJwtTokenUtils;
import io.tech1.framework.b2b.mongodb.security.jwt.repositories.MongoUsersSessionsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Slf4j
@Service
public class MongoTokensContextThrowerService extends AbstractTokensContextThrowerService {

    @Autowired
    public MongoTokensContextThrowerService(
            JwtUserDetailsService jwtUserDetailsService,
            MongoUsersSessionsRepository usersSessionsRepository,
            SecurityJwtTokenUtils securityJwtTokenUtils
    ) {
        super(
                jwtUserDetailsService,
                usersSessionsRepository,
                securityJwtTokenUtils
        );
    }
}