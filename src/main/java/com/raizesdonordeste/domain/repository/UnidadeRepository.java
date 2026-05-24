package com.raizesdonordeste.domain.repository;

import com.raizesdonordeste.domain.entity.Unidade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnidadeRepository extends JpaRepository<Unidade, Long> {
    Page<Unidade> findByAtivaTrue(Pageable pageable);
}
