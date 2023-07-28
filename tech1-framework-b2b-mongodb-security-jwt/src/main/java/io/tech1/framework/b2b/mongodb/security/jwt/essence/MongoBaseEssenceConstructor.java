package io.tech1.framework.b2b.mongodb.security.jwt.essence;

import io.tech1.framework.b2b.base.security.jwt.essense.EssenceConstructor;
import io.tech1.framework.b2b.mongodb.security.jwt.domain.db.MongoDbInvitationCode;
import io.tech1.framework.b2b.mongodb.security.jwt.domain.db.MongoDbUser;
import io.tech1.framework.b2b.mongodb.security.jwt.repositories.MongoInvitationCodesRepository;
import io.tech1.framework.b2b.mongodb.security.jwt.repositories.MongoUsersRepository;
import io.tech1.framework.properties.ApplicationFrameworkProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.tech1.framework.domain.asserts.Asserts.assertTrueOrThrow;
import static io.tech1.framework.domain.constants.FrameworkLogsConstants.FRAMEWORK_B2B_MONGODB_SECURITY_JWT_PREFIX;
import static io.tech1.framework.domain.utilities.exceptions.ExceptionsMessagesUtility.invalidAttribute;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MongoBaseEssenceConstructor implements EssenceConstructor {

    // Repositories
    protected final MongoInvitationCodesRepository mongoInvitationCodesRepository;
    protected final MongoUsersRepository mongoUsersRepository;
    // Properties
    protected final ApplicationFrameworkProperties applicationFrameworkProperties;

    @Override
    public boolean isDefaultUsersEnabled() {
        return this.applicationFrameworkProperties.getSecurityJwtConfigs().getEssenceConfigs().getDefaultUsers().isEnabled();
    }

    @Override
    public boolean isInvitationCodesEnabled() {
        return this.applicationFrameworkProperties.getSecurityJwtConfigs().getEssenceConfigs().getInvitationCodes().isEnabled();
    }

    @Override
    public void addDefaultUsers() {
        assertTrueOrThrow(this.isDefaultUsersEnabled(), invalidAttribute("essenceConfigs.defaultUsers.enabled == true"));
        if (this.mongoUsersRepository.count() == 0L) {
            LOGGER.warn(FRAMEWORK_B2B_MONGODB_SECURITY_JWT_PREFIX + " Essence `defaultUsers`. No users in database. Establish database structure");
            var defaultUsers = this.applicationFrameworkProperties.getSecurityJwtConfigs().getEssenceConfigs().getDefaultUsers().getUsers();
            var users = defaultUsers.stream().map(defaultUser -> {
                var username = defaultUser.getUsername();
                var user = new MongoDbUser(
                        username,
                        defaultUser.getPassword(),
                        defaultUser.getZoneId().getId(),
                        defaultUser.getAuthorities().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
                );
                user.setEmail(defaultUser.getEmail());
                LOGGER.debug(FRAMEWORK_B2B_MONGODB_SECURITY_JWT_PREFIX + " Essence `defaultUsers`. Convert default user. Username: {}", username);
                return user;
            }).collect(Collectors.toList());
            this.mongoUsersRepository.saveAll(users);
            LOGGER.warn(FRAMEWORK_B2B_MONGODB_SECURITY_JWT_PREFIX + " Essence `defaultUsers` is completed. Saved dbRecords: `{}`", users.size());
        } else {
            LOGGER.warn(FRAMEWORK_B2B_MONGODB_SECURITY_JWT_PREFIX + " Essence `defaultUsers`. Users are already saved in database. Please double check");
        }
    }

    @Override
    public void addDefaultUsersInvitationCodes() {
        assertTrueOrThrow(this.isInvitationCodesEnabled(), invalidAttribute("essenceConfigs.invitationCodes.enabled == true"));
        var authorities = this.applicationFrameworkProperties.getSecurityJwtConfigs().getAuthoritiesConfigs().getAvailableAuthorities();
        var simpleGrantedAuthorities = authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        var defaultUsers = this.applicationFrameworkProperties.getSecurityJwtConfigs().getEssenceConfigs().getDefaultUsers();
        defaultUsers.getUsers().forEach(defaultUser -> {
            var username = defaultUser.getUsername();
            var invitationCodes = this.mongoInvitationCodesRepository.findByOwner(username);
            if (invitationCodes.size() == 0L) {
                LOGGER.warn(FRAMEWORK_B2B_MONGODB_SECURITY_JWT_PREFIX + " Essence `defaultUsers`. No invitation codes in database. Username: `{}`", username);
                var dbInvitationCodes = IntStream.range(0, 10)
                        .mapToObj(i ->
                                new MongoDbInvitationCode(
                                        username,
                                        simpleGrantedAuthorities
                                )
                        ).collect(Collectors.toList());
                this.mongoInvitationCodesRepository.saveAll(dbInvitationCodes);
            } else {
                LOGGER.warn(FRAMEWORK_B2B_MONGODB_SECURITY_JWT_PREFIX + " Essence `defaultUsers`. Invitation codes are already saved in database. Username: `{}`", username);
            }
        });
    }
}
