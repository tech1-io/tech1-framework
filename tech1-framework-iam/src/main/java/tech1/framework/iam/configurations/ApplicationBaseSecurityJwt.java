package tech1.framework.iam.configurations;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tech1.framework.foundation.configurations.*;
import tech1.framework.foundation.domain.base.PropertyId;
import tech1.framework.foundation.domain.constants.SwaggerConstants;
import tech1.framework.foundation.domain.properties.ApplicationFrameworkProperties;
import tech1.framework.iam.assistants.userdetails.JwtUserDetailsService;
import tech1.framework.iam.filters.jwt.JwtTokensFilter;
import tech1.framework.iam.handlers.exceptions.JwtAccessDeniedExceptionHandler;
import tech1.framework.iam.handlers.exceptions.JwtAuthenticationEntryPointExceptionHandler;

import static org.springframework.http.HttpMethod.*;
import static tech1.framework.foundation.domain.base.AbstractAuthority.*;

@Configuration
@ComponentScan({
        // -------------------------------------------------------------------------------------------------------------
        "tech1.framework.iam.crons",
        "tech1.framework.iam.events.publishers.base",
        "tech1.framework.iam.events.publishers.impl",
        "tech1.framework.iam.events.subscribers.base",
        "tech1.framework.iam.events.subscribers.impl",
        "tech1.framework.iam.handlers.exceptions",
        "tech1.framework.iam.resources.base",
        "tech1.framework.iam.services.base",
        "tech1.framework.iam.tokens",
        "tech1.framework.iam.utils",
        "tech1.framework.iam.validators.base"
        // -------------------------------------------------------------------------------------------------------------
})
@EnableWebSecurity
@Import({
        ApplicationHardwareMonitoring.class,
        ApplicationUserMetadata.class,
        ApplicationSpringBootServer.class,
        ApplicationJasypt.class,
        ApplicationBaseSecurityJwtMvc.class,
        ApplicationBaseSecurityJwtFilters.class,
        ApplicationBaseSecurityJwtPasswords.class,
        ApplicationIncidents.class,
        ApplicationEmails.class
})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ApplicationBaseSecurityJwt {

    // Assistants
    private final JwtUserDetailsService jwtUserDetailsService;
    // Passwords
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    // Filters
    private final JwtTokensFilter jwtTokensFilter;
    // Handlers
    private final JwtAuthenticationEntryPointExceptionHandler jwtAuthenticationEntryPointExceptionHandler;
    private final JwtAccessDeniedExceptionHandler jwtAccessDeniedExceptionHandler;
    // Configurer
    private final AbstractApplicationSecurityJwtConfigurer abstractApplicationSecurityJwtConfigurer;
    // Properties
    private final ApplicationFrameworkProperties applicationFrameworkProperties;

    @PostConstruct
    public void init() {
        this.applicationFrameworkProperties.getSecurityJwtConfigs().assertProperties(new PropertyId("securityJwtConfigs"));
    }

    @Autowired
    void configureAuthenticationManager(AuthenticationManagerBuilder builder) throws Exception {
        builder
                .userDetailsService(this.jwtUserDetailsService)
                .passwordEncoder(this.bCryptPasswordEncoder);
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> {
            if (this.applicationFrameworkProperties.getServerConfigs().isSpringdocEnabled()) {
                web.ignoring().requestMatchers(SwaggerConstants.ENDPOINTS.toArray(new String[0]));
            }
            this.abstractApplicationSecurityJwtConfigurer.configure(web);
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        var basePathPrefix = this.applicationFrameworkProperties.getMvcConfigs().getFrameworkBasePathPrefix();

        http.cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .addFilterBefore(
                        this.jwtTokensFilter,
                        UsernamePasswordAuthenticationFilter.class
                )
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling
                                .authenticationEntryPoint(this.jwtAuthenticationEntryPointExceptionHandler)
                                .accessDeniedHandler(this.jwtAccessDeniedExceptionHandler)
                )
                .sessionManagement(sessionManagement ->
                        sessionManagement
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        // WARNING: order is important, configurer must have possibility to override matchers below
        this.abstractApplicationSecurityJwtConfigurer.configure(http);

        http.authorizeHttpRequests(authorizeHttpRequests -> {
            authorizeHttpRequests
                    .requestMatchers(POST, basePathPrefix + "/authentication/login").permitAll()
                    .requestMatchers(POST, basePathPrefix + "/authentication/logout").permitAll()
                    .requestMatchers(POST, basePathPrefix + "/authentication/refreshToken").permitAll()
                    .requestMatchers(GET, basePathPrefix + "/session/current").authenticated()
                    .requestMatchers(POST, basePathPrefix + "/registration/register1").anonymous()
                    .requestMatchers(POST, basePathPrefix + "/user/update1").authenticated()
                    .requestMatchers(POST, basePathPrefix + "/user/update2").authenticated()
                    .requestMatchers(POST, basePathPrefix + "/user/changePassword1").authenticated();

            if (this.applicationFrameworkProperties.getSecurityJwtConfigs().getEssenceConfigs().getInvitationCodes().isEnabled()) {
                authorizeHttpRequests
                        .requestMatchers(GET, basePathPrefix + "/invitationCode").hasAuthority(INVITATION_CODE_READ)
                        .requestMatchers(POST, basePathPrefix + "/invitationCode").hasAuthority(INVITATION_CODE_WRITE)
                        .requestMatchers(DELETE, basePathPrefix + "/invitationCode/{invitationCodeId}").hasAuthority(INVITATION_CODE_WRITE);
            } else {
                authorizeHttpRequests.requestMatchers(basePathPrefix + "/invitationCode/**").denyAll();
            }

            authorizeHttpRequests
                    .requestMatchers(basePathPrefix + "/test-data/**").authenticated()
                    .requestMatchers(basePathPrefix + "/hardware/**").authenticated()
                    .requestMatchers(basePathPrefix + "/superadmin/**").hasAuthority(SUPERADMIN)
                    .requestMatchers(basePathPrefix + "/**").authenticated();

            authorizeHttpRequests.requestMatchers("/actuator/**").permitAll();

            authorizeHttpRequests.anyRequest().authenticated();
        });

        return http.build();
    }
}
