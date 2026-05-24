package com.raizesdonordeste.domain.enums;

/**
 * Status possíveis de um pagamento mock.
 * O gateway simulado retorna um destes status.
 */
public enum StatusPagamento {
    PENDENTE,
    APROVADO,
    RECUSADO,
    TIMEOUT
}
