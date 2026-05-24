package com.raizesdonordeste.infrastructure.security;

import com.raizesdonordeste.domain.entity.Usuario;
import com.raizesdonordeste.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Serviço de carregamento de detalhes do usuário para o Spring Security.
 * 
 * Fluxo de autenticação:
 * 1. O Spring Security chama loadUserByUsername com o email do usuário
 * 2. O serviço busca o usuário no banco de dados pelo email
 * 3. Converte o perfil do usuário (PerfilUsuario) em GrantedAuthority com prefixo ROLE_
 * 4. Retorna um UserDetails que o Spring Security usa para autorização
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Tentativa de login com email não cadastrado: {}", email);
                    return new UsernameNotFoundException("Usuário não encontrado com o email: " + email);
                });

        // Verifica se o usuário está ativo
        if (!usuario.getAtivo()) {
            log.warn("Tentativa de login de usuário desativado: {}", email);
            throw new UsernameNotFoundException("Usuário desativado: " + email);
        }

        // Converte o perfil para GrantedAuthority com prefixo ROLE_ (padrão Spring Security)
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + usuario.getPerfil().name());

        return new User(
                usuario.getEmail(),
                usuario.getSenhaHash(),
                Collections.singletonList(authority)
        );
    }
}
