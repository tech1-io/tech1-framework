package io.tech1.framework.b2b.mongodb.security.jwt.services.impl;

import io.tech1.framework.b2b.mongodb.security.jwt.domain.db.DbUser;
import io.tech1.framework.b2b.mongodb.security.jwt.domain.dto.requests.RequestUserRegistration1;
import io.tech1.framework.b2b.mongodb.security.jwt.repositories.InvitationCodeRepository;
import io.tech1.framework.b2b.mongodb.security.jwt.repositories.UserRepository;
import io.tech1.framework.b2b.mongodb.security.jwt.services.RegistrationService;
import io.tech1.framework.domain.base.Password;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RegistrationServiceImpl implements RegistrationService {

    // Repository
    private final InvitationCodeRepository invitationCodeRepository;
    private final UserRepository userRepository;
    // Password
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public DbUser register1(RequestUserRegistration1 requestUserRegistration1) {
        var invitationCode = invitationCodeRepository.findByValue(requestUserRegistration1.invitationCode());

        var hashPassword = this.bCryptPasswordEncoder.encode(requestUserRegistration1.password().value());

        var user = new DbUser(
                requestUserRegistration1.username(),
                Password.of(hashPassword),
                requestUserRegistration1.zoneId(),
                invitationCode.getAuthorities()
        );

        user = this.userRepository.save(user);

        invitationCode.setInvited(user.getUsername());
        this.invitationCodeRepository.save(invitationCode);

        return user;
    }
}
