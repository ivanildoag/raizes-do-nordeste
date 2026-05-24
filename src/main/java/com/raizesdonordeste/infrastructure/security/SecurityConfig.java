package com.raizesdonordeste.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuração de segurança da aplicação.
 * 
 * Decisões de segurança:
 * - JWT stateless: Não mantém sessão no servidor, cada requisição carrega o token.
 * - CSRF desabilitado: APIs REST stateless não precisam de proteção CSRF.
 * - CORS permissivo: Para desenvolvimento. Em produção, restringir origens.
 * - BCrypt: Algoritmo de hash adaptativo para senhas (custo computacional crescente).
 * 
 * Endpoints públicos:
 * - POST /api/v1/auth/** (login e registro)
 * - GET /api/v1/produtos/** (consulta de cardápio)
 * - GET /api/v1/unidades/** (consulta de unidades)
 * - /swagger-ui/**, /v3/api-docs/** (documentação)
 * - /actuator/health (health check)
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Desabilita CSRF - API REST stateless não necessita
            .csrf(AbstractHttpConfigurer::disable)

            // Configura CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // Configura gerenciamento de sessão como STATELESS (JWT)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Configura tratamento de exceções de segurança
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler))

            // Configura regras de autorização por endpoint
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos de autenticação
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/**").permitAll()
                // Consulta pública de produtos e unidades
                .requestMatchers(HttpMethod.GET, "/api/v1/produtos/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/unidades/**").permitAll()
                // Swagger/OpenAPI
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html",
                        "/v3/api-docs/**", "/swagger-resources/**").permitAll()
                // Actuator health check
                .requestMatchers("/actuator/health").permitAll()
                // Todos os demais endpoints requerem autenticação
                .anyRequest().authenticated())

            // Registra o filtro JWT antes do filtro padrão de autenticação
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /** Bean do PasswordEncoder usando BCrypt (segurança de senhas) */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** Bean do AuthenticationManager para uso no AuthService */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /** Configuração de CORS - permissivo para desenvolvimento */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
