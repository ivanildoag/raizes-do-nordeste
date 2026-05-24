package com.raizesdonordeste.infrastructure.gateway;

import com.raizesdonordeste.application.port.PagamentoGatewayPort;
import com.raizesdonordeste.domain.enums.StatusPagamento;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;

/**
 * Gateway mock de pagamento.
 * 
 * Simula uma integração com gateway de pagamento externo.
 * Em produção, seria substituído por integração real (ex: PagSeguro, Mercado Pago).
 * 
 * Comportamento simulado:
 * - 70% de chance de APROVAÇÃO
 * - 20% de chance de RECUSA
 * - 10% de chance de TIMEOUT (falha de comunicação)
 * 
 * Adiciona um delay de 100-500ms para simular latência de rede.
 */
@Slf4j
@Component
public class PagamentoMockGateway implements PagamentoGatewayPort {

    private final Random random = new Random();

    @Override
    public ResultadoPagamento processar(BigDecimal valor, String formaPagamento) {
        String transacaoId = UUID.randomUUID().toString();

        // Simula latência de rede (100-500ms)
        try {
            Thread.sleep(100 + random.nextInt(400));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Simula resultado do pagamento
        int chance = random.nextInt(100);
        StatusPagamento status;
        String mensagem;

        if (chance < 70) {
            // 70% - Pagamento aprovado
            status = StatusPagamento.APROVADO;
            mensagem = "Pagamento aprovado com sucesso";
            log.info("Pagamento APROVADO - Transação: {}, Valor: R${}, Forma: {}",
                    transacaoId, valor, formaPagamento);
        } else if (chance < 90) {
            // 20% - Pagamento recusado
            status = StatusPagamento.RECUSADO;
            mensagem = "Pagamento recusado pela operadora";
            log.warn("Pagamento RECUSADO - Transação: {}, Valor: R${}, Forma: {}",
                    transacaoId, valor, formaPagamento);
        } else {
            // 10% - Timeout/falha
            status = StatusPagamento.TIMEOUT;
            mensagem = "Timeout na comunicação com o gateway de pagamento";
            log.error("Pagamento TIMEOUT - Transação: {}, Valor: R${}, Forma: {}",
                    transacaoId, valor, formaPagamento);
        }

        return new ResultadoPagamento(status, transacaoId, mensagem);
    }
}
