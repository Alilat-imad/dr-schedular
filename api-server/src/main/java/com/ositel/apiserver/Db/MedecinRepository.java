package com.ositel.apiserver.db;

import com.ositel.apiserver.model.Medecin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedecinRepository extends JpaRepository<Medecin, Long> {

    Optional<Medecin> findById(Long id);

    boolean existsById(Long aLong);
}
