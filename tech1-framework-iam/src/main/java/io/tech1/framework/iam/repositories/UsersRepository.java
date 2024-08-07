package io.tech1.framework.iam.repositories;

import io.tech1.framework.iam.domain.db.InvitationCode;
import io.tech1.framework.iam.domain.dto.requests.RequestUserRegistration1;
import io.tech1.framework.iam.domain.identifiers.UserId;
import io.tech1.framework.iam.domain.jwt.JwtUser;
import io.tech1.framework.foundation.domain.base.Email;
import io.tech1.framework.foundation.domain.base.Password;
import io.tech1.framework.foundation.domain.base.Username;
import io.tech1.framework.foundation.domain.tuples.TuplePresence;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UsersRepository {
    TuplePresence<JwtUser> isPresent(UserId userId);
    JwtUser loadUserByUsername(Username username) throws UsernameNotFoundException;
    JwtUser findByUsernameAsJwtUserOrNull(Username username);
    JwtUser findByEmailAsJwtUserOrNull(Email email);
    long count();
    UserId saveAs(JwtUser user);
    UserId saveAs(RequestUserRegistration1 requestUserRegistration1, Password password, InvitationCode invitationCode);
}
