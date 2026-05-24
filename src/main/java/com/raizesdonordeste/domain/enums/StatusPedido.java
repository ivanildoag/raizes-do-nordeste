package com.raizesdonordeste.domain.enums;

/**
 * Status possíveis de um pedido.
 * O fluxo de transição segue a ordem:
 * AGUARDANDO_PAGAMENTO -> PAGO -> EM_PREPARO -> PRONTO -> ENTREGUE
 * Cancelamento é possível a partir de AGUARDANDO_PAGAMENTO ou PAGO.
 */
public enum StatusPedido {
    AGUARDANDO_PAGAMENTO,
    PAGO,
    EM_PREPARO,
    PRONTO,
    ENTREGUE,
    CANCELADO
}
