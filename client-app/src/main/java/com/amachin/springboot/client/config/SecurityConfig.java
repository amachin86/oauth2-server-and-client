package com.amachin.springboot.client.config;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.JwtBearerOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((autHttp) -> autHttp
                .requestMatchers(HttpMethod.GET,"/authorized").permitAll()
                .requestMatchers(HttpMethod.GET,"/message").hasAnyAuthority("SCOPE_read", "SCOPE_write")
                .anyRequest().authenticated())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2Login(login -> login.loginPage("/oauth2/authorization/client-app"))//login page
                .oauth2Client(Customizer.withDefaults())
                .oauth2ResourceServer(resourceServer -> resourceServer.jwt(withDefaults()));
        return http.build();
    }

    @Bean
    public OAuth2AuthorizedClientProvider jwtBearer() {
        return new JwtBearerOAuth2AuthorizedClientProvider();
    }



    @Bean
    public JwtDecoder jwtDecoder() {
        // Replace with your JWT issuer URI
        String jwkSetUri = "http://127.0.0.1:9001";
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }

   /* @Bean
    public InMemoryClientRegistrationRepository clientRegistrationRepository() {
        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("my-provider")
                .clientId("your-client-id")
                .clientSecret("your-client-secret")
                .authorizationUri("https://provider.com/oauth/authorize")
                .tokenUri("https://provider.com/oauth/token")
                .userInfoUri("https://provider.com/userinfo")
                .scope("read", "write")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
               // .redirectUriTemplate("{baseUrl}/login/oauth2/code/{registrationId}")
                .build();

        return new InMemoryClientRegistrationRepository(clientRegistration);
    }*/
}
