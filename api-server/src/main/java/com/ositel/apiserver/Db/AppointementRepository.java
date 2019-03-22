package com.ositel.apiserver.db;

import com.ositel.apiserver.model.Appointement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointementRepository extends JpaRepository<Appointement, Long> {
}
