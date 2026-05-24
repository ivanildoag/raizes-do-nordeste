package com.raizesdonordeste.application.service;

import com.raizesdonordeste.application.dto.request.CriarUsuarioRequest;
import com.raizesdonordeste.application.dto.request.LoginRequest;
import com.raizesdonordeste.application.dto.response.LoginResponse;
import com.raizesdonordeste.application.dto.response.UsuarioResponse;
import com.raizesdonordeste.domain.entity.Usuario;
import com.raizesdonordeste.domain.enums.PerfilUsuario;
import com.raizesdonordeste.domain.exception.CredenciaisInvalidasException;
import com.raizesdonordeste.domain.exception.RegraDeNegocioException;
import com.raizesdonordeste.domain.repository.FidelidadeRepository;
import com.raizesdonordeste.domain.repository.UsuarioRepository;
import com.raizesdonordeste.infrastructure.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Testes unitários do serviço de autenticação.
 * Nomenclatura em português do Brasil conforme requisito.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService - Testes de autenticação e registro")
class AuthServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private FidelidadeRepository fidelidadeRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @InjectMocks private AuthService authService;

    private CriarUsuarioRequest criarUsuarioRequest;
    private Usuario usuarioSalvo;

    @BeforeEach
    void setUp() {
        criarUsuarioRequest = CriarUsuarioRequest.builder()
                .nome("Maria Teste").email("maria@teste.com").senha("Senha@123")
                .cpf("123.456.789-00").telefone("(85) 99999-0001").consentimentoLgpd(true).build();

        usuarioSalvo = Usuario.builder()
                .id(1L).nome("Maria Teste").email("maria@teste.com")
                .senhaHash("$2a$10$hash").cpf("123.456.789-00").telefone("(85) 99999-0001")
                .perfil(PerfilUsuario.CLIENTE).ativo(true).consentimentoLgpd(true)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
    }

    @Test
    @DisplayName("Deve registrar usuário quando dados são válidos")
    void deveRegistrarUsuarioQuandoDadosSaoValidos() {
        // Given
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(usuarioRepository.existsByCpf(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hash");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioSalvo);
        when(fidelidadeRepository.save(any())).thenReturn(null);

        // When
        UsuarioResponse response = authService.registrar(criarUsuarioRequest);

        // Then
        assertNotNull(response);
        assertEquals("Maria Teste", response.getNome());
        assertEquals("maria@teste.com", response.getEmail());
        assertEquals("CLIENTE", response.getPerfil());
        verify(usuarioRepository).save(any(Usuario.class));
        verify(passwordEncoder).encode("Senha@123");
    }

    @Test
    @DisplayName("Deve lançar exceção quando email já está cadastrado")
    void deveLancarExcecaoQuandoEmailJaCadastrado() {
        // Given
        when(usuarioRepository.existsByEmail("maria@teste.com")).thenReturn(true);

        // When/Then
        RegraDeNegocioException ex = assertThrows(RegraDeNegocioException.class,
                () -> authService.registrar(criarUsuarioRequest));
        assertEquals("Email já cadastrado no sistema", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando CPF já está cadastrado")
    void deveLancarExcecaoQuandoCpfJaCadastrado() {
        // Given
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(usuarioRepository.existsByCpf("123.456.789-00")).thenReturn(true);

        // When/Then
        assertThrows(RegraDeNegocioException.class, () -> authService.registrar(criarUsuarioRequest));
    }

    @Test
    @DisplayName("Deve lançar exceção quando consentimento LGPD não é dado")
    void deveLancarExcecaoQuandoConsentimentoLgpdNaoDado() {
        // Given
        criarUsuarioRequest.setConsentimentoLgpd(false);
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(usuarioRepository.existsByCpf(anyString())).thenReturn(false);

        // When/Then
        RegraDeNegocioException ex = assertThrows(RegraDeNegocioException.class,
                () -> authService.registrar(criarUsuarioRequest));
        assertTrue(ex.getMessage().contains("LGPD"));
    }

    @Test
    @DisplayName("Deve realizar login quando credenciais são válidas")
    void deveRealizarLoginQuandoCredenciaisSaoValidas() {
        // Given
        LoginRequest loginRequest = LoginRequest.builder().email("maria@teste.com").senha("Senha@123").build();
        when(usuarioRepository.findByEmail("maria@teste.com")).thenReturn(Optional.of(usuarioSalvo));
        when(passwordEncoder.matches("Senha@123", "$2a$10$hash")).thenReturn(true);
        when(jwtTokenProvider.generateToken("maria@teste.com", "CLIENTE")).thenReturn("jwt-token");
        when(jwtTokenProvider.getExpirationInSeconds()).thenReturn(86400L);

        // When
        LoginResponse response = authService.login(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals("jwt-token", response.getAccessToken());
        assertEquals("Bearer", response.getTokenType());
        assertNotNull(response.getUsuario());
    }

    @Test
    @DisplayName("Deve lançar exceção quando email não encontrado no login")
    void deveLancarExcecaoQuandoEmailNaoEncontradoNoLogin() {
        // Given
        LoginRequest loginRequest = LoginRequest.builder().email("naoexiste@teste.com").senha("Senha@123").build();
        when(usuarioRepository.findByEmail("naoexiste@teste.com")).thenReturn(Optional.empty());

        // When/Then
        assertThrows(CredenciaisInvalidasException.class, () -> authService.login(loginRequest));
    }

    @Test
    @DisplayName("Deve lançar exceção quando senha está incorreta")
    void deveLancarExcecaoQuandoSenhaIncorreta() {
        // Given
        LoginRequest loginRequest = LoginRequest.builder().email("maria@teste.com").senha("errada").build();
        when(usuarioRepository.findByEmail("maria@teste.com")).thenReturn(Optional.of(usuarioSalvo));
        when(passwordEncoder.matches("errada", "$2a$10$hash")).thenReturn(false);

        // When/Then
        assertThrows(CredenciaisInvalidasException.class, () -> authService.login(loginRequest));
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário está desativado")
    void deveLancarExcecaoQuandoUsuarioDesativado() {
        // Given
        usuarioSalvo.setAtivo(false);
        LoginRequest loginRequest = LoginRequest.builder().email("maria@teste.com").senha("Senha@123").build();
        when(usuarioRepository.findByEmail("maria@teste.com")).thenReturn(Optional.of(usuarioSalvo));

        // When/Then
        assertThrows(CredenciaisInvalidasException.class, () -> authService.login(loginRequest));
    }
}
