package com.raizesdonordeste;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Teste básico que verifica se a classe principal da aplicação existe
 * e pode ser instanciada sem erro.
 * 
 * Nota: Não usa @SpringBootTest para evitar dependência de banco de dados
 * durante os testes unitários. Testes de integração completos devem usar
 * Testcontainers com Docker.
 */
class RaizesDoNordesteApplicationTests {

    @Test
    void deveExistirClassePrincipalDaAplicacao() {
        assertDoesNotThrow(() -> new RaizesDoNordesteApplication());
    }
}
