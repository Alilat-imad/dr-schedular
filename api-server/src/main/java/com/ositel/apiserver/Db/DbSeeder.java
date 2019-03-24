package com.ositel.apiserver.db;

import com.ositel.apiserver.exception.AppException;
import com.ositel.apiserver.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Component
@ConditionalOnProperty(name = "populate-default-data", havingValue = "true")
public class DbSeeder implements CommandLineRunner {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private MedecinRepository medecinRepository;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private AppointementRepository appointementRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    ShiftHoraireRepository shiftHoraireRepository;

    @Override
    public void run(String... args) throws Exception {

        appointementRepository.deleteAll();
        medecinRepository.deleteAll();
        patientRepository.deleteAll();
        shiftHoraireRepository.deleteAll();

        // Delete All users
        userRepository.deleteAll();
        // Delete all roles
        roleRepository.deleteAll();


        Set<Role> roleSet = new HashSet<>();
        // Add default roles
        Role role1 =  this.roleRepository.save(new Role(RoleName.ROLE_ADMIN));
//        Role role2 =  this.roleRepository.save(new Role(RoleName.ROLE_PM));
        Role role3 =  this.roleRepository.save(new Role(RoleName.ROLE_MEDECIN));

        roleSet.add(role1);
//        roleSet.add(role2);
        roleSet.add(role3);


        User user = new User(
                  "medecin"
                , "medecin@mail.com"
                , "medecin"
        );
        user.setPassword(passwordEncoder.encode("medecin"));

        Role userRole = roleRepository.findByName(RoleName.ROLE_MEDECIN)
                .orElseThrow(() -> new AppException("Medecin Role not set."));

        user.setRoles(Collections.singleton(userRole));


        Medecin medecin = new Medecin(
                "ALILAT Imad"
                , "+213770474400"
                , "Algerie"
                , user
                );

//        user.setMedecin(medecin);

        this.medecinRepository.save(medecin);
        User result = userRepository.save(user);


        Patient patient = new Patient("Mohamed", "Mohamed@gmail.com");
        this.patientRepository.save(patient);



        Set<ShiftHoraire> shiftHoraireSet = new HashSet<>();
        ShiftHoraire shiftHoraire1 = new ShiftHoraire(1L, LocalTime.of(8, 0), LocalTime.of(9, 0));
        ShiftHoraire shiftHoraire2 = new ShiftHoraire(2L, LocalTime.of(9, 0), LocalTime.of(10, 0));
        ShiftHoraire shiftHoraire3 = new ShiftHoraire(3L, LocalTime.of(10, 0), LocalTime.of(11, 0));
        ShiftHoraire shiftHoraire4 = new ShiftHoraire(4L, LocalTime.of(11, 0), LocalTime.of(12, 0));
        ShiftHoraire shiftHoraire5 = new ShiftHoraire(5L, LocalTime.of(13, 0), LocalTime.of(14, 0));
        ShiftHoraire shiftHoraire6 = new ShiftHoraire(6L, LocalTime.of(14, 0), LocalTime.of(15, 0));
        ShiftHoraire shiftHoraire7 = new ShiftHoraire(7L, LocalTime.of(15, 0), LocalTime.of(16, 0));
        ShiftHoraire shiftHoraire8 = new ShiftHoraire(8L, LocalTime.of(16, 0), LocalTime.of(17, 0));
        shiftHoraireSet.add(shiftHoraire1);
        shiftHoraireSet.add(shiftHoraire2);
        shiftHoraireSet.add(shiftHoraire3);
        shiftHoraireSet.add(shiftHoraire4);
        shiftHoraireSet.add(shiftHoraire5);
        shiftHoraireSet.add(shiftHoraire6);
        shiftHoraireSet.add(shiftHoraire7);
        shiftHoraireSet.add(shiftHoraire8);
        this.shiftHoraireRepository.saveAll(shiftHoraireSet);


        Set<Appointement> appointementSet = new HashSet<>();
        Appointement appointement1 = new Appointement(medecin, patient, shiftHoraire1, LocalDate.now(), true);
        Appointement appointement2 = new Appointement(medecin, patient, shiftHoraire3, LocalDate.now(), true);
        Appointement appointement3 = new Appointement(medecin, patient, shiftHoraire4, LocalDate.now(), true);
        Appointement appointement4 = new Appointement(medecin, patient, shiftHoraire7, LocalDate.now(), false);
        appointementSet.add(appointement1);
        appointementSet.add(appointement2);
        appointementSet.add(appointement3);
        appointementSet.add(appointement4);
        this.appointementRepository.saveAll(appointementSet);

    }
}
