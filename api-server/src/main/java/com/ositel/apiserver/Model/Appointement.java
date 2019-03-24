package com.ositel.apiserver.model;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "appointements")
@Getter
@Setter
public class Appointement implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medecin_id", nullable = true)
    private Medecin medecin;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_horaire_id", nullable = false)
    private ShiftHoraire shiftHoraire;


    private LocalDate date;

    private boolean isActive;

    public Appointement() {
    }

    public Appointement(Medecin medecin, Patient patient, ShiftHoraire shiftHoraire, LocalDate date, boolean isActive) {
        this.medecin = medecin;
        this.patient = patient;
        this.shiftHoraire = shiftHoraire;
        this.date = date;
        this.isActive = isActive;
    }
}
