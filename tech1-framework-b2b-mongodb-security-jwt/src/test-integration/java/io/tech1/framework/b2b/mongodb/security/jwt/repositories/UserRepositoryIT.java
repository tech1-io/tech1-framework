package io.tech1.framework.b2b.mongodb.security.jwt.repositories;

import io.tech1.framework.b2b.mongodb.security.jwt.domain.db.DbUser;
import io.tech1.framework.b2b.mongodb.security.jwt.tests.TestsApplicationRepositoriesRunner;
import io.tech1.framework.domain.base.Username;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static io.tech1.framework.b2b.mongodb.security.jwt.repositories.UserRepository.SUPERADMIN;
import static io.tech1.framework.b2b.mongodb.security.jwt.tests.converters.UserConverter.toUsernamesAsStrings;
import static io.tech1.framework.b2b.mongodb.security.jwt.tests.random.SecurityJwtDbRandomUtility.dummyUsersData1;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({ SpringExtension.class })
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {
                UserRepository.class
        }
)
@EnableAutoConfiguration
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class UserRepositoryIT extends TestsApplicationRepositoriesRunner {

    private final UserRepository userRepository;

    @Override
    public MongoRepository<DbUser, String> getMongoRepository() {
        return this.userRepository;
    }

    @Test
    void findByAuthoritiesTests() {
        // Arrange
        this.userRepository.saveAll(dummyUsersData1());

        // Act
        var superadmins = this.userRepository.findByAuthoritySuperadmin();
        var notSuperadmins = this.userRepository.findByAuthorityNotSuperadmin();
        var superadminsProjection = this.userRepository.findByAuthorityProjectionUsernames(SUPERADMIN);
        var notSuperadminsProjection = this.userRepository.findByAuthorityNotEqualProjectionUsernames(SUPERADMIN);
        var superadminsUsernames = this.userRepository.findSuperadminsUsernames();
        var notSuperadminsUsernames = this.userRepository.findNotSuperadminsUsernames();

        // Assert
        assertThat(toUsernamesAsStrings(superadmins))
                .hasSize(3)
                .containsExactly("sa1", "sa2", "sa3");

        assertThat(toUsernamesAsStrings(notSuperadmins))
                .hasSize(3)
                .containsExactly("admin", "user1", "user2");

        assertThat(toUsernamesAsStrings(superadminsProjection))
                .hasSize(3)
                .containsExactly("sa1", "sa2", "sa3");
        superadminsProjection.forEach(user -> {
            assertThat(user.getUsername()).isNotNull();
            assertThat(user.getId()).isNull();
            assertThat(user.getPassword()).isNull();
            assertThat(user.getAuthorities()).isNull();
            assertThat(user.getZoneId()).isNull();
        });

        assertThat(toUsernamesAsStrings(notSuperadminsProjection))
                .hasSize(3)
                .containsExactly("admin", "user1", "user2");
        notSuperadminsProjection.forEach(user -> {
            assertThat(user.getUsername()).isNotNull();
            assertThat(user.getId()).isNull();
            assertThat(user.getPassword()).isNull();
            assertThat(user.getAuthorities()).isNull();
            assertThat(user.getZoneId()).isNull();
        });

        assertThat(superadminsUsernames)
                .hasSize(3)
                .isEqualTo(
                    List.of(
                            Username.of("sa1"),
                            Username.of("sa2"),
                            Username.of("sa3")
                    )
                );

        assertThat(notSuperadminsUsernames)
                .hasSize(3)
                .isEqualTo(
                    List.of(
                            Username.of("admin"),
                            Username.of("user1"),
                            Username.of("user2")
                    )
                );
    }

    @Test
    void deleteByAuthoritiesTests() {
        // Arrange
        this.userRepository.saveAll(dummyUsersData1());

        // Act-Assert-0
        assertThat(this.userRepository.count()).isEqualTo(6);

        // Act-Assert-1
        this.userRepository.deleteByAuthorityNotSuperadmin();
        var users1 = this.userRepository.findAll();
        assertThat(toUsernamesAsStrings(users1))
                .hasSize(3)
                .containsExactly("sa1", "sa2", "sa3");

        // Act-Assert-2
        this.userRepository.deleteByAuthoritySuperadmin();
        var users2 = this.userRepository.findAll();
        assertThat(users2).isEmpty();
    }
}
