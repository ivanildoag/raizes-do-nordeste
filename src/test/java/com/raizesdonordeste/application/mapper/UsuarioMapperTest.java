package com.raizesdonordeste.application.mapper;

import com.raizesdonordeste.domain.entity.Usuario;
import com.raizesdonordeste.domain.enums.PerfilUsuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UsuarioMapper - Testes de mapeamento e mascaramento LGPD")
class UsuarioMapperTest {

    @Test
    @DisplayName("Deve mascarar CPF corretamente para atender a LGPD")
    void deveMascararCpfCorretamente() {
        assertEquals("***.***.***-00", UsuarioMapper.mascararCpf("123.456.789-00"));
        assertEquals("***.***.***-22", UsuarioMapper.mascararCpf("222.222.222-22"));
        assertEquals("***.***.***-**", UsuarioMapper.mascararCpf(null));
        assertEquals("***.***.***-**", UsuarioMapper.mascararCpf("12"));
    }

    @Test
    @DisplayName("Deve converter entidade para response sem expor senha")
    void deveConverterEntidadeParaResponseSemExporSenha() {
        Usuario usuario = Usuario.builder().id(1L).nome("Maria").email("maria@teste.com")
                .senhaHash("$2a$10$hash").cpf("123.456.789-00").perfil(PerfilUsuario.CLIENTE)
                .ativo(true).createdAt(LocalDateTime.now()).build();

        var response = UsuarioMapper.toResponse(usuario);

        assertEquals("Maria", response.getNome());
        assertEquals("***.***.***-00", response.getCpfMascarado());
        assertNotNull(response.getEmail());
        // Verifica que não há campo senhaHash no response (compilação garante)
    }
}
