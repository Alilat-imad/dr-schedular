package com.ositel.apiserver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shift_horaires")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ShiftHoraire {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalTime timeStart;
    private LocalTime timeEnd;

    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            mappedBy = "patient")
    @JsonIgnore
    private List<Appointement> appointements = new ArrayList<>();


    public ShiftHoraire(Long id, LocalTime timeStart, LocalTime timeEnd) {
        this.id = id;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
    }
}
