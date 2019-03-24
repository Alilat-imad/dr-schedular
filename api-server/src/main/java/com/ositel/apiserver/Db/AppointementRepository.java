package com.ositel.apiserver.db;

import com.ositel.apiserver.model.Appointement;
import com.ositel.apiserver.model.Medecin;
import com.ositel.apiserver.model.Patient;
import com.ositel.apiserver.model.ShiftHoraire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointementRepository extends JpaRepository<Appointement, Long> {
    List<Appointement> findAllByMedecin(Medecin medecin);
    List<Appointement> findAllByPatient(Patient patient);
    List<Appointement> findAllByMedecinAndDate(Medecin medecin, LocalDate date);

    Optional<Appointement> findByMedecinAndDateAndShiftHoraire(Medecin medecin, LocalDate date, ShiftHoraire shiftHoraire);
}
