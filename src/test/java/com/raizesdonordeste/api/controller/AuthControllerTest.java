package com.raizesdonordeste.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raizesdonordeste.application.dto.request.LoginRequest;
import com.raizesdonordeste.application.dto.request.CriarUsuarioRequest;
import com.raizesdonordeste.application.dto.response.LoginResponse;
import com.raizesdonordeste.application.dto.response.UsuarioResponse;
import com.raizesdonordeste.application.service.AuthService;
import com.raizesdonordeste.infrastructure.security.*;
import com.raizesdonordeste.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração do controller de autenticação.
 * Usa @WebMvcTest para testar apenas a camada web (sem necessidade de DB).
 * Configura mocks para beans de segurança para evitar dependência de infraestrutura.
 */
@WebMvcTest(AuthController.class)
@Import(AuthControllerTest.TestConfig.class)
@DisplayName("AuthController - Testes de integração dos endpoints de autenticação")
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private AuthService authService;

    /**
     * Configuração de teste que desabilita a security real e mocka dependências.
     */
    @TestConfiguration
    static class TestConfig {

        @Bean
        public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }

        @Bean
        public AuthService authService() {
            return Mockito.mock(AuthService.class);
        }

        /** Mocks para beans de segurança que são component-scanned */
        @Bean
        public JwtTokenProvider jwtTokenProvider() {
            return Mockito.mock(JwtTokenProvider.class);
        }

        @Bean
        public CustomUserDetailsService customUserDetailsService() {
            return Mockito.mock(CustomUserDetailsService.class);
        }

        @Bean
        public UsuarioRepository usuarioRepository() {
            return Mockito.mock(UsuarioRepository.class);
        }
    }

    @Test
    @DisplayName("Deve retornar 201 ao registrar usuário com dados válidos")
    void deveRetornar201AoRegistrarUsuarioComDadosValidos() throws Exception {
        // Given
        CriarUsuarioRequest request = CriarUsuarioRequest.builder()
                .nome("Maria").email("maria@teste.com").senha("Senha@123")
                .cpf("123.456.789-00").consentimentoLgpd(true).build();
        UsuarioResponse response = UsuarioResponse.builder()
                .id(1L).nome("Maria").email("maria@teste.com")
                .cpfMascarado("***.***.***-00").perfil("CLIENTE").ativo(true)
                .createdAt(LocalDateTime.now()).build();
        when(authService.registrar(any())).thenReturn(response);

        // When/Then
        mockMvc.perform(post("/api/v1/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Maria"))
                .andExpect(jsonPath("$.email").value("maria@teste.com"))
                .andExpect(jsonPath("$.perfil").value("CLIENTE"));
    }

    @Test
    @DisplayName("Deve retornar 422 ao registrar usuário sem email")
    void deveRetornar422AoRegistrarUsuarioSemEmail() throws Exception {
        // Given - request sem email
        CriarUsuarioRequest request = CriarUsuarioRequest.builder()
                .nome("Maria").senha("Senha@123").cpf("123.456.789-00").consentimentoLgpd(true).build();

        // When/Then
        mockMvc.perform(post("/api/v1/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("VALIDACAO_ERRO"));
    }

    @Test
    @DisplayName("Deve retornar 200 ao fazer login com credenciais válidas")
    void deveRetornar200AoFazerLoginComCredenciaisValidas() throws Exception {
        // Given
        LoginRequest request = LoginRequest.builder().email("maria@teste.com").senha("Senha@123").build();
        LoginResponse loginResponse = LoginResponse.builder()
                .accessToken("jwt-token").tokenType("Bearer").expiresIn(86400L)
                .usuario(UsuarioResponse.builder().id(1L).nome("Maria").email("maria@teste.com")
                        .perfil("CLIENTE").build()).build();
        when(authService.login(any())).thenReturn(loginResponse);

        // When/Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("jwt-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    @DisplayName("Deve retornar 400 ao enviar requisição sem corpo")
    void deveRetornar400AoEnviarRequisicaoSemCorpo() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }
}
