package com.raizesdonordeste.domain.repository;

import com.raizesdonordeste.domain.entity.HistoricoFidelidade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoricoFidelidadeRepository extends JpaRepository<HistoricoFidelidade, Long> {
    Page<HistoricoFidelidade> findByFidelidadeId(Long fidelidadeId, Pageable pageable);
}
