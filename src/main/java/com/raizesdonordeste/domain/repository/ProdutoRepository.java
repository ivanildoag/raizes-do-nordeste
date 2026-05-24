package com.raizesdonordeste.domain.repository;

import com.raizesdonordeste.domain.entity.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    Page<Produto> findByAtivoTrue(Pageable pageable);
    Page<Produto> findByCategoriaAndAtivoTrue(String categoria, Pageable pageable);
}
