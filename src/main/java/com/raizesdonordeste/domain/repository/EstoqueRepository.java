package com.raizesdonordeste.domain.repository;

import com.raizesdonordeste.domain.entity.Estoque;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EstoqueRepository extends JpaRepository<Estoque, Long> {
    Page<Estoque> findByUnidadeId(Long unidadeId, Pageable pageable);
    Optional<Estoque> findByUnidadeIdAndProdutoId(Long unidadeId, Long produtoId);
}
