package com.raizesdonordeste.domain.repository;

import com.raizesdonordeste.domain.entity.Pedido;
import com.raizesdonordeste.domain.enums.CanalPedido;
import com.raizesdonordeste.domain.enums.StatusPedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    Page<Pedido> findByClienteId(Long clienteId, Pageable pageable);
    Page<Pedido> findByCanalPedido(CanalPedido canal, Pageable pageable);
    Page<Pedido> findByStatus(StatusPedido status, Pageable pageable);
    Page<Pedido> findByCanalPedidoAndStatus(CanalPedido canal, StatusPedido status, Pageable pageable);
}
