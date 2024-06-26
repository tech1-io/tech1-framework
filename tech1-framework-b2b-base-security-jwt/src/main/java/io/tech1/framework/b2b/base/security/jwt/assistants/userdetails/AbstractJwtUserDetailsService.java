package io.tech1.framework.b2b.base.security.jwt.assistants.userdetails;

import io.tech1.framework.b2b.base.security.jwt.domain.jwt.JwtUser;
import io.tech1.framework.b2b.base.security.jwt.repositories.UsersRepository;
import io.tech1.framework.foundation.domain.base.Username;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractJwtUserDetailsService implements JwtUserDetailsService {

    // Repository
    protected final UsersRepository usersRepository;

    @Override
    public JwtUser loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.usersRepository.loadUserByUsername(Username.of(username));
    }
}
