package tech1.framework.foundation.domain.properties.configs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AssignableTypeFilter;
import tech1.framework.foundation.domain.asserts.Asserts;
import tech1.framework.foundation.domain.base.AbstractAuthority;
import tech1.framework.foundation.domain.base.PropertyId;
import tech1.framework.foundation.domain.properties.annotations.MandatoryProperty;
import tech1.framework.foundation.domain.properties.base.Checkbox;
import tech1.framework.foundation.domain.properties.configs.security.jwt.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class SecurityJwtConfigs extends AbstractPropertiesConfigs {
    @MandatoryProperty
    private final AuthoritiesConfigs authoritiesConfigs;
    @MandatoryProperty
    private final CookiesConfigs cookiesConfigs;
    @MandatoryProperty
    private final EssenceConfigs essenceConfigs;
    @MandatoryProperty
    private final IncidentsConfigs incidentsConfigs;
    @MandatoryProperty
    private final JwtTokensConfigs jwtTokensConfigs;
    @MandatoryProperty
    private final LoggingConfigs loggingConfigs;
    @MandatoryProperty
    private final SessionConfigs sessionConfigs;
    @MandatoryProperty
    private final UsersEmailsConfigs usersEmailsConfigs;

    public static SecurityJwtConfigs testsHardcoded() {
        return new SecurityJwtConfigs(
                AuthoritiesConfigs.testsHardcoded(),
                CookiesConfigs.testsHardcoded(),
                EssenceConfigs.testsHardcoded(),
                IncidentsConfigs.testsHardcoded(),
                JwtTokensConfigs.testsHardcoded(),
                LoggingConfigs.testsHardcoded(),
                SessionConfigs.testsHardcoded(),
                UsersEmailsConfigs.testsHardcoded()
        );
    }

    public static SecurityJwtConfigs of(LoggingConfigs loggingConfigs) {
        return new SecurityJwtConfigs(
                null,
                null,
                null,
                null,
                null,
                loggingConfigs,
                null,
                null
        );
    }

    public static SecurityJwtConfigs of(SessionConfigs sessionConfigs) {
        return new SecurityJwtConfigs(
                null,
                null,
                null,
                null,
                null,
                null,
                sessionConfigs,
                null
        );
    }

    public static SecurityJwtConfigs disabledUsersEmailsConfigs() {
        return new SecurityJwtConfigs(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                new UsersEmailsConfigs(
                        "[Tech1]",
                        Checkbox.disabled(),
                        Checkbox.disabled()
                )
        );
    }

    @Override
    public boolean isParentPropertiesNode() {
        return true;
    }

    @Override
    public void assertProperties(PropertyId propertyId) {
        super.assertProperties(propertyId);

        // Requirements: availableAuthorities vs. defaultUsersAuthorities
        var expectedAuthorities = this.authoritiesConfigs.getAllAuthoritiesValues();
        var defaultUsersAuthorities = this.essenceConfigs.getDefaultUsers().getDefaultUsersAuthorities();
        boolean containsAll = expectedAuthorities.containsAll(defaultUsersAuthorities);
        Asserts.assertTrueOrThrow(containsAll, "Please verify `defaultUsers.users.authorities`. Configuration provide unauthorized authority");

        // Requirements: availableAuthorities vs. required enum values
        var authorityClasses = this.getAbstractAuthorityClasses(this.authoritiesConfigs.getPackageName());
        int size = authorityClasses.size();
        Asserts.assertTrueOrThrow(size == 1, "Please verify AbstractAuthority.class has only one sub enum. Found: `" + size + "`");
        var authorityClass = authorityClasses.iterator().next();
        Set<String> actualAuthorities = new HashSet<>();
        var abstractAuthorityClass = AbstractAuthority.class;
        var frameworkAuthorities = Stream.of(abstractAuthorityClass.getDeclaredFields())
                .map(field -> {
                    try {
                        return field.get(abstractAuthorityClass).toString();
                    } catch (IllegalAccessException ex) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        var serverAuthorities = Stream.of(authorityClass.getEnumConstants())
                .map(AbstractAuthority::getValue)
                .collect(Collectors.toSet());
        actualAuthorities.addAll(frameworkAuthorities);
        actualAuthorities.addAll(serverAuthorities);
        Asserts.assertTrueOrThrow(
                expectedAuthorities.equals(actualAuthorities),
                "Please verify AbstractAuthority sub enum configuration. Expected: `" + expectedAuthorities + "`. Actual: `" + actualAuthorities + "`"
        );
    }

    // =================================================================================================================
    // Private Method: Reflection on AbstractAuthority
    // =================================================================================================================
    @SuppressWarnings("unchecked")
    private Set<Class<? extends AbstractAuthority>> getAbstractAuthorityClasses(String packageName) {
        var beanDefinitionRegistry = new SimpleBeanDefinitionRegistry();
        var classPathBeanDefinitionScanner = new ClassPathBeanDefinitionScanner(beanDefinitionRegistry, false);
        var tf = new AssignableTypeFilter(AbstractAuthority.class);
        classPathBeanDefinitionScanner.addIncludeFilter(tf);
        classPathBeanDefinitionScanner.scan(packageName);
        return Stream.of(beanDefinitionRegistry.getBeanDefinitionNames())
                .map(beanDefinitionRegistry::getBeanDefinition)
                .map(BeanDefinition::getBeanClassName)
                .map(className -> {
                    try {
                        return (Class<? extends AbstractAuthority>) Class.forName(className);
                    } catch (ClassNotFoundException ex) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .filter(clazz -> nonNull(clazz.getEnumConstants()))
                .collect(Collectors.toSet());
    }
}
