package com.raizesdonordeste.domain.repository;

import com.raizesdonordeste.domain.entity.Fidelidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface FidelidadeRepository extends JpaRepository<Fidelidade, Long> {
    Optional<Fidelidade> findByUsuarioId(Long usuarioId);
}
