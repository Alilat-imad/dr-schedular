package com.ositel.apiserver.db;

import com.ositel.apiserver.model.ShiftHoraire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShiftHoraireRepository extends JpaRepository<ShiftHoraire, Long> {
    boolean existsById(Long aLong);
}
