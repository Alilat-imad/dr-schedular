package com.ositel.apiserver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
@Entity
@Table(name = "patients")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "full_name")
    @Size(min = 5, max = 30)
    private String fullName;

    @NotBlank
    @Email
    private String email;

    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            mappedBy = "patient")
    @JsonIgnore
    private List<Appointement> appointements = new ArrayList<>();


    public Patient(@NotBlank @Size(min = 5, max = 30) String fullName, @NotBlank @Email String email) {
        this.fullName = fullName;
        this.email = email;
    }
}
