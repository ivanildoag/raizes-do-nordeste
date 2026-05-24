package com.raizesdonordeste.application.service;

import com.raizesdonordeste.application.dto.request.CriarPedidoRequest;
import com.raizesdonordeste.application.dto.request.MovimentarEstoqueRequest;
import com.raizesdonordeste.application.dto.response.PedidoResponse;
import com.raizesdonordeste.application.mapper.PedidoMapper;
import com.raizesdonordeste.domain.entity.*;
import com.raizesdonordeste.domain.enums.*;
import com.raizesdonordeste.domain.exception.*;
import com.raizesdonordeste.domain.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Serviço de gerenciamento de pedidos — fluxo crítico do sistema.
 * 
 * Fluxo principal:
 * 1. Cliente cria pedido com canal e itens
 * 2. Sistema valida unidade, produtos e estoque
 * 3. Sistema calcula totais e reserva estoque
 * 4. Pedido é criado com status AGUARDANDO_PAGAMENTO
 * 5. Após pagamento aprovado, status muda para PAGO
 * 6. Gerente/cozinha atualiza: EM_PREPARO -> PRONTO -> ENTREGUE
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final UnidadeRepository unidadeRepository;
    private final ProdutoRepository produtoRepository;
    private final EstoqueService estoqueService;

    /**
     * Cria um novo pedido validando unidade, produtos e estoque.
     * Regras:
     * - Unidade deve existir e estar ativa
     * - Todos os produtos devem existir e estar ativos
     * - Estoque deve ser suficiente para todos os itens na unidade
     * - canalPedido é obrigatório (multicanalidade)
     */
    @Transactional
    public PedidoResponse criar(CriarPedidoRequest request, Long clienteId) {
        // Busca o cliente
        Usuario cliente = usuarioRepository.findById(clienteId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente não encontrado"));

        // Valida unidade ativa
        Unidade unidade = unidadeRepository.findById(request.getUnidadeId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Unidade não encontrada com id: " + request.getUnidadeId()));
        if (!unidade.getAtiva()) {
            throw new RegraDeNegocioException("Unidade não está ativa para receber pedidos");
        }

        // Valida produtos e estoque, calcula totais
        List<ItemPedido> itens = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        List<EstoqueInsuficienteException.DetalheEstoque> detalhesEstoque = new ArrayList<>();

        for (var itemRequest : request.getItens()) {
            Produto produto = produtoRepository.findById(itemRequest.getProdutoId())
                    .orElseThrow(() -> new EntidadeNaoEncontradaException(
                            "Produto não encontrado com id: " + itemRequest.getProdutoId()));

            if (!produto.getAtivo()) {
                throw new RegraDeNegocioException("Produto '" + produto.getNome() + "' não está disponível");
            }

            // Verifica estoque na unidade
            if (!estoqueService.verificarDisponibilidade(unidade.getId(), produto.getId(), itemRequest.getQuantidade())) {
                detalhesEstoque.add(new EstoqueInsuficienteException.DetalheEstoque(
                        "itens[" + itemRequest.getProdutoId() + "].quantidade",
                        "Estoque insuficiente para: " + produto.getNome()));
            }

            BigDecimal subtotal = produto.getPreco().multiply(BigDecimal.valueOf(itemRequest.getQuantidade()));
            total = total.add(subtotal);

            itens.add(ItemPedido.builder()
                    .produto(produto)
                    .quantidade(itemRequest.getQuantidade())
                    .precoUnitario(produto.getPreco())
                    .subtotal(subtotal)
                    .build());
        }

        // Se houver itens com estoque insuficiente, lança exceção
        if (!detalhesEstoque.isEmpty()) {
            throw new EstoqueInsuficienteException(
                    "Não há quantidade suficiente para um ou mais itens.", detalhesEstoque);
        }

        // Cria o pedido
        Pedido pedido = Pedido.builder()
                .cliente(cliente)
                .unidade(unidade)
                .canalPedido(request.getCanalPedido())
                .status(StatusPedido.AGUARDANDO_PAGAMENTO)
                .total(total)
                .observacao(request.getObservacao())
                .itens(new ArrayList<>())
                .build();

        // Associa os itens ao pedido
        for (ItemPedido item : itens) {
            item.setPedido(pedido);
            pedido.getItens().add(item);
        }

        pedido = pedidoRepository.save(pedido);

        // Reserva o estoque (saída) para cada item
        for (var itemRequest : request.getItens()) {
            estoqueService.movimentar(MovimentarEstoqueRequest.builder()
                    .unidadeId(unidade.getId())
                    .produtoId(itemRequest.getProdutoId())
                    .quantidade(itemRequest.getQuantidade())
                    .tipo(TipoMovimentacao.SAIDA)
                    .motivo("Reserva para pedido #" + pedido.getId())
                    .build(), clienteId);
        }

        log.info("Pedido #{} criado - Canal: {}, Unidade: {}, Total: R${}, Itens: {}",
                pedido.getId(), pedido.getCanalPedido(), unidade.getNome(), total, itens.size());

        return PedidoMapper.toResponse(pedido);
    }

    @Transactional(readOnly = true)
    public PedidoResponse buscarPorId(Long id) {
        return PedidoMapper.toResponse(buscarPedido(id));
    }

    /** Lista pedidos com filtros opcionais por canal e status (multicanalidade) */
    @Transactional(readOnly = true)
    public Page<PedidoResponse> listarPedidos(CanalPedido canal, StatusPedido status, Pageable pageable) {
        if (canal != null && status != null) {
            return pedidoRepository.findByCanalPedidoAndStatus(canal, status, pageable).map(PedidoMapper::toResponse);
        } else if (canal != null) {
            return pedidoRepository.findByCanalPedido(canal, pageable).map(PedidoMapper::toResponse);
        } else if (status != null) {
            return pedidoRepository.findByStatus(status, pageable).map(PedidoMapper::toResponse);
        }
        return pedidoRepository.findAll(pageable).map(PedidoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<PedidoResponse> listarPedidosDoCliente(Long clienteId, Pageable pageable) {
        return pedidoRepository.findByClienteId(clienteId, pageable).map(PedidoMapper::toResponse);
    }

    /**
     * Atualiza o status do pedido com validação de transição.
     * Regra: Apenas transições válidas são permitidas conforme máquina de estados.
     */
    @Transactional
    public PedidoResponse atualizarStatus(Long id, StatusPedido novoStatus) {
        Pedido pedido = buscarPedido(id);

        if (!pedido.isTransicaoValida(novoStatus)) {
            throw new TransicaoStatusInvalidaException(
                    "Transição de status inválida: " + pedido.getStatus() + " → " + novoStatus);
        }

        StatusPedido statusAnterior = pedido.getStatus();
        pedido.setStatus(novoStatus);
        pedido = pedidoRepository.save(pedido);

        log.info("Status do pedido #{} atualizado: {} → {}", id, statusAnterior, novoStatus);
        return PedidoMapper.toResponse(pedido);
    }

    /**
     * Cancela um pedido e restaura o estoque.
     * Regra: Apenas o dono do pedido ou ADMIN/GERENTE podem cancelar.
     */
    @Transactional
    public PedidoResponse cancelar(Long id, Long usuarioId) {
        Pedido pedido = buscarPedido(id);

        if (!pedido.isTransicaoValida(StatusPedido.CANCELADO)) {
            throw new TransicaoStatusInvalidaException(
                    "Pedido não pode ser cancelado no status atual: " + pedido.getStatus());
        }

        pedido.setStatus(StatusPedido.CANCELADO);
        pedido = pedidoRepository.save(pedido);

        // Restaura o estoque dos itens cancelados
        for (ItemPedido item : pedido.getItens()) {
            estoqueService.movimentar(MovimentarEstoqueRequest.builder()
                    .unidadeId(pedido.getUnidade().getId())
                    .produtoId(item.getProduto().getId())
                    .quantidade(item.getQuantidade())
                    .tipo(TipoMovimentacao.ENTRADA)
                    .motivo("Cancelamento do pedido #" + pedido.getId())
                    .build(), usuarioId);
        }

        log.info("Pedido #{} cancelado. Estoque restaurado.", id);
        return PedidoMapper.toResponse(pedido);
    }

    public Pedido buscarPedido(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Pedido não encontrado com id: " + id));
    }
}
