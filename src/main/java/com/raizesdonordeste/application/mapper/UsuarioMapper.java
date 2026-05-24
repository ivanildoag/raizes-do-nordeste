package com.raizesdonordeste.application.mapper;

import com.raizesdonordeste.application.dto.request.CriarUsuarioRequest;
import com.raizesdonordeste.application.dto.response.UsuarioResponse;
import com.raizesdonordeste.domain.entity.Usuario;
import com.raizesdonordeste.domain.enums.PerfilUsuario;

/**
 * Mapper para conversão entre Usuario (entidade) e DTOs.
 * LGPD: O CPF é mascarado no formato ***.***.XXX-XX nas respostas.
 */
public class UsuarioMapper {

    private UsuarioMapper() {}

    public static UsuarioResponse toResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .cpfMascarado(mascararCpf(usuario.getCpf()))
                .perfil(usuario.getPerfil().name())
                .ativo(usuario.getAtivo())
                .createdAt(usuario.getCreatedAt())
                .build();
    }

    public static Usuario toEntity(CriarUsuarioRequest request) {
        return Usuario.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .cpf(request.getCpf())
                .telefone(request.getTelefone())
                .perfil(PerfilUsuario.CLIENTE)
                .consentimentoLgpd(request.getConsentimentoLgpd())
                .ativo(true)
                .build();
    }

    /** LGPD: Mascara o CPF para exibição, mostrando apenas os últimos 5 dígitos */
    public static String mascararCpf(String cpf) {
        if (cpf == null || cpf.length() < 5) return "***.***.***-**";
        return "***.***.***-" + cpf.substring(cpf.length() - 2);
    }
}
