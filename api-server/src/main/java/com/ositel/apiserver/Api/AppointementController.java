package com.ositel.apiserver.Api;

import com.ositel.apiserver.Api.DtoMapper.AppointementMapper;
import com.ositel.apiserver.Api.DtoViewModel.ApiResponse;
import com.ositel.apiserver.Api.DtoViewModel.*;
import com.ositel.apiserver.db.AppointementRepository;
import com.ositel.apiserver.db.MedecinRepository;
import com.ositel.apiserver.db.PatientRepository;
import com.ositel.apiserver.db.ShiftHoraireRepository;
import com.ositel.apiserver.mail.IMailSender;
import com.ositel.apiserver.model.Appointement;
import com.ositel.apiserver.model.Patient;
import com.ositel.apiserver.security.CurrentUser;
import com.ositel.apiserver.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/appointment")
@CrossOrigin
public class AppointementController {

    private MedecinRepository medecinRepository;
    private PatientRepository patientRepository;
    private ShiftHoraireRepository shiftHoraireRepository;
    private AppointementRepository appointementRepository;
    private IMailSender mailSender;
    private AppointementMapper appointementMapper;

    @Autowired
    public AppointementController(
            MedecinRepository medecinRepository
            , PatientRepository patientRepository
            , ShiftHoraireRepository shiftHoraireRepository
            , AppointementRepository appointementRepository
            , IMailSender mailSender
            , AppointementMapper appointementMapper
    ) {
        this.medecinRepository = medecinRepository;
        this.patientRepository = patientRepository;
        this.shiftHoraireRepository = shiftHoraireRepository;
        this.appointementRepository = appointementRepository;
        this.mailSender = mailSender;
        this.appointementMapper = appointementMapper;
    }


    //  Add new appointement;
    @PostMapping("/save")
    public ResponseEntity<?> save(@Valid @RequestBody NewAppointmentRequest newAppointment, BindingResult bindingResult){
        // First check if there no error in the requested file.
        if(bindingResult.hasErrors()){
            throw new ValidationException("Appointment has errors; Can not save the appointment;");
        }
        // TODO : The above section, should be put inside a custom Mapper to keep the controller simple.
        // Then get the required data to perform the data cooherance.
        var medecin = this.medecinRepository.findById(newAppointment.getMedecinId());
        var shiftHoraire = this.shiftHoraireRepository.findById(newAppointment.getShiftTimeId());
        var date = LocalDate.parse(newAppointment.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        System.out.println("date => "+ date);
        if(medecin.isEmpty()){
            return new ResponseEntity<>(new ApiResponse(false,"Medecin not found."),
                    HttpStatus.NOT_FOUND);
        }
        if(shiftHoraire.isEmpty()){
            return new ResponseEntity<>(new ApiResponse(false,"Time shift does not exist, please try again."),
                    HttpStatus.BAD_REQUEST);
        }

        var isAvailable = this.appointementRepository.findByMedecinAndDateAndShiftHoraire(medecin.get(), date, shiftHoraire.get());
        // Check if not already take.
        if(isAvailable.isPresent()){
            return new ResponseEntity<>(new ApiResponse(false,"Time shift already taken, please choose another one."),
                    HttpStatus.BAD_REQUEST);
        }
        var patientName = newAppointment.getName();
        var patientEmail = newAppointment.getEmail();



        // Finally perform the save operation
        Patient patient = new Patient(patientName, patientEmail);
        Patient savedPatient = this.patientRepository.save(patient);
        Appointement appointement = new Appointement(medecin.get(), savedPatient,shiftHoraire.get(), date, false);
        this.appointementRepository.save(appointement);
        return ResponseEntity.ok(appointement);

    }

    @PreAuthorize("hasRole('MEDECIN')")
    @DeleteMapping("/{appointmentId}")
    public ResponseEntity<?> delete(@PathVariable String appointmentId){
        var appointment = this.appointementRepository.findById(Long.parseLong(appointmentId));
        if(appointment.isEmpty()){
            return new ResponseEntity<>(new ApiResponse(false,"Appointment not found."),
                    HttpStatus.NOT_FOUND);
        }
        this.appointementRepository.deleteById(Long.parseLong(appointmentId));
//        this.mailSender.notifyPatient(false, appointment.get().getMedecin().getFullName() ,appointment.get().getPatient().getEmail(), appointment.get().getDate(), appointment.get().getShiftHoraire().getTimeStart(), appointment.get().getShiftHoraire().getTimeEnd());

        return new ResponseEntity<>(new ApiResponse(true,"Deleting appointment done successfully"),
                HttpStatus.OK);
    }

    @PreAuthorize("hasRole('MEDECIN')")
    @PostMapping("/status/{appointmentId}")
    public ResponseEntity<?> status(@PathVariable String appointmentId){

        var appointment = this.appointementRepository.findById(Long.parseLong(appointmentId));

        if(appointment.isEmpty()){
            return new ResponseEntity<>(new ApiResponse(false,"Appointment not found."),
                    HttpStatus.NOT_FOUND);
        }
        var newAppointmentStatus = !appointment.get().isActive();
        appointment.get().setActive(newAppointmentStatus);

        this.appointementRepository.save(appointment.get());
//        this.mailSender.notifyPatient(newAppointmentStatus, appointment.get().getMedecin().getFullName() ,appointment.get().getPatient().getEmail(), appointment.get().getDate(), appointment.get().getShiftHoraire().getTimeStart(), appointment.get().getShiftHoraire().getTimeEnd());

        return new ResponseEntity<>(new ApiResponse(true,"Updating appointment done successfully"),
                HttpStatus.OK);
    }

    @PreAuthorize("hasRole('MEDECIN')")
    @PostMapping("/all")
    public ResponseEntity<? extends Object> today(@CurrentUser UserPrincipal currentUser, @Valid @RequestBody TodayAppointmentRequest todayAppointment, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            throw new ValidationException("Appointment has errors; Can not update the status of the appointment;");
        }
        var medecin = this.medecinRepository.findById(currentUser.getId());
        var date = LocalDate.parse(todayAppointment.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        if(medecin.isEmpty()){
            return new ResponseEntity<>(new ApiResponse(false,"Medecin not found."),
                    HttpStatus.NOT_FOUND);
        }
        var isAvailable = this.appointementRepository.findAllByMedecinAndDate(medecin.get(), date);
        if(isAvailable.isEmpty()){
//            return new ResponseEntity<>(new ApiResponse(false,"No event for today."),
//                    HttpStatus.NOT_FOUND);
            return ResponseEntity.ok(new TodayAppointmentResponse(date, 0,null));
        }
        int size = isAvailable.size();
        var response = this.appointementMapper.toDto(isAvailable, isAvailable.get(0), size);
        return ResponseEntity.ok(response);

    }

}
