package com.raizesdonordeste.application.service;

import com.raizesdonordeste.application.dto.request.CriarUsuarioRequest;
import com.raizesdonordeste.application.dto.request.LoginRequest;
import com.raizesdonordeste.application.dto.response.LoginResponse;
import com.raizesdonordeste.application.dto.response.UsuarioResponse;
import com.raizesdonordeste.application.mapper.UsuarioMapper;
import com.raizesdonordeste.domain.entity.Fidelidade;
import com.raizesdonordeste.domain.entity.Usuario;
import com.raizesdonordeste.domain.enums.PerfilUsuario;
import com.raizesdonordeste.domain.exception.CredenciaisInvalidasException;
import com.raizesdonordeste.domain.exception.RegraDeNegocioException;
import com.raizesdonordeste.domain.repository.FidelidadeRepository;
import com.raizesdonordeste.domain.repository.UsuarioRepository;
import com.raizesdonordeste.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço de autenticação e registro de usuários.
 * 
 * Segurança:
 * - Senhas são hasheadas com BCrypt antes de persistir
 * - Tokens JWT são gerados com email e perfil do usuário
 * - Credenciais inválidas retornam erro genérico (não revela se email existe)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final FidelidadeRepository fidelidadeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /** Registra um novo usuário no sistema */
    @Transactional
    public UsuarioResponse registrar(CriarUsuarioRequest request) {
        // Valida unicidade do email
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RegraDeNegocioException("Email já cadastrado no sistema");
        }

        // Valida unicidade do CPF
        if (usuarioRepository.existsByCpf(request.getCpf())) {
            throw new RegraDeNegocioException("CPF já cadastrado no sistema");
        }

        // LGPD: Verifica se o consentimento foi dado
        if (!Boolean.TRUE.equals(request.getConsentimentoLgpd())) {
            throw new RegraDeNegocioException("O consentimento para tratamento de dados (LGPD) é obrigatório");
        }

        // Cria a entidade e hasheia a senha com BCrypt
        Usuario usuario = UsuarioMapper.toEntity(request);
        usuario.setSenhaHash(passwordEncoder.encode(request.getSenha()));
        usuario.setPerfil(PerfilUsuario.CLIENTE);

        usuario = usuarioRepository.save(usuario);

        // Cria registro de fidelidade para o novo usuário
        Fidelidade fidelidade = Fidelidade.builder()
                .usuario(usuario)
                .pontosAcumulados(0)
                .pontosResgatados(0)
                .saldo(0)
                .build();
        fidelidadeRepository.save(fidelidade);

        log.info("Novo usuário registrado: {} (perfil: {})", usuario.getEmail(), usuario.getPerfil());
        return UsuarioMapper.toResponse(usuario);
    }

    /** Autentica o usuário e retorna o token JWT */
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        // Busca o usuário pelo email
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("Tentativa de login com email não encontrado: {}", request.getEmail());
                    return new CredenciaisInvalidasException("E-mail ou senha inválidos.");
                });

        // Verifica se o usuário está ativo
        if (!usuario.getAtivo()) {
            throw new CredenciaisInvalidasException("Usuário desativado. Entre em contato com o suporte.");
        }

        // Verifica a senha com BCrypt
        if (!passwordEncoder.matches(request.getSenha(), usuario.getSenhaHash())) {
            log.warn("Tentativa de login com senha incorreta para: {}", request.getEmail());
            throw new CredenciaisInvalidasException("E-mail ou senha inválidos.");
        }

        // Gera o token JWT
        String token = jwtTokenProvider.generateToken(usuario.getEmail(), usuario.getPerfil().name());

        log.info("Login realizado com sucesso: {} (perfil: {})", usuario.getEmail(), usuario.getPerfil());

        return LoginResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getExpirationInSeconds())
                .usuario(UsuarioMapper.toResponse(usuario))
                .build();
    }
}
