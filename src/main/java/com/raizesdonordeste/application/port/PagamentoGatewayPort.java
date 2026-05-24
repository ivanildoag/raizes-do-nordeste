package com.raizesdonordeste.application.port;

import com.raizesdonordeste.domain.enums.StatusPagamento;
import java.math.BigDecimal;

/**
 * Interface (port) do gateway de pagamento.
 * Em Clean Architecture, esta é a porta que a camada de aplicação usa.
 * A implementação concreta (mock) fica na camada de infraestrutura.
 */
public interface PagamentoGatewayPort {
    ResultadoPagamento processar(BigDecimal valor, String formaPagamento);

    record ResultadoPagamento(StatusPagamento status, String transacaoId, String mensagem) {}
}
