package app.fichier.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // Désactive CSRF pour les tests (à réactiver en production)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/demande/**").permitAll()  // Autorise l'endpoint /demande sans authentification
                .anyRequest().authenticated()  // Tous les autres endpoints nécessitent une authentification
            );
        
        return http.build();
    }
}

