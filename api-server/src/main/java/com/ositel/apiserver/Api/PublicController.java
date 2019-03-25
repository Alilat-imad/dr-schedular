package com.ositel.apiserver.Api;

import com.ositel.apiserver.Api.DtoMapper.AppointementMapper;
import com.ositel.apiserver.Api.DtoViewModel.Request.MedecinAvailabilityRequest;
import com.ositel.apiserver.Api.DtoViewModel.Request.NewAppointmentRequest;
import com.ositel.apiserver.Api.DtoViewModel.Response.ApiResponse;
import com.ositel.apiserver.db.AppointementRepository;
import com.ositel.apiserver.db.MedecinRepository;
import com.ositel.apiserver.db.PatientRepository;
import com.ositel.apiserver.db.ShiftHoraireRepository;
import com.ositel.apiserver.mail.IMailSender;
import com.ositel.apiserver.model.Appointement;
import com.ositel.apiserver.model.Patient;
import com.ositel.apiserver.utils.TweakResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/public")
@CrossOrigin
public class PublicController {


    private MedecinRepository medecinRepository;
    private PatientRepository patientRepository;
    private ShiftHoraireRepository shiftHoraireRepository;
    private AppointementRepository appointementRepository;
    private IMailSender mailSender;
    private AppointementMapper appointementMapper;
    private TweakResponse tweakResponse;

    @Autowired
    public PublicController(MedecinRepository medecinRepository, PatientRepository patientRepository, ShiftHoraireRepository shiftHoraireRepository, AppointementRepository appointementRepository, IMailSender mailSender, AppointementMapper appointementMapper, TweakResponse tweakResponse) {
        this.medecinRepository = medecinRepository;
        this.patientRepository = patientRepository;
        this.shiftHoraireRepository = shiftHoraireRepository;
        this.appointementRepository = appointementRepository;
        this.mailSender = mailSender;
        this.appointementMapper = appointementMapper;
        this.tweakResponse = tweakResponse;
    }

    //  Add new appointement;
    @PostMapping("appointment/save")
    public ResponseEntity<?> save(@Valid @RequestBody NewAppointmentRequest newAppointment, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new ValidationException("Appointment has errors; Can not save the appointment;");
        }

        var medecin = this.medecinRepository.findById(newAppointment.getMedecinId());
        var shiftHoraire = this.shiftHoraireRepository.findById(newAppointment.getShiftTimeId());
        var date = LocalDate.parse(newAppointment.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        if(medecin.isEmpty()){
            return new ResponseEntity<>(new ApiResponse(false,"Medecin non trouvé."),
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

    // List all available Medecin
    @GetMapping("/medecin")
    public ResponseEntity<? extends Object> listMedecin(){
        var ListMedecin = this.medecinRepository.findAll();
        return ResponseEntity.ok(ListMedecin);
    }

    // Get list of availability of a doctor by day.
    @PostMapping("/availability")
    public ResponseEntity<? extends Object> listMedecinAvailability(@Valid @RequestBody MedecinAvailabilityRequest medecinAvailability, BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            throw new ValidationException("Availabilities has errors; Can not fetch all medecin availabilities;");
        }

        var medecin = this.medecinRepository.findById(medecinAvailability.getMedecinId());
        var date = LocalDate.parse(medecinAvailability.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        if(medecin.isEmpty()){
            return new ResponseEntity<>(new ApiResponse(false,"Medecin not found.."),
                    HttpStatus.NOT_FOUND);
        }
        var medecinAvailabilities = this.appointementRepository.findAllByMedecinAndDate(medecin.get(), date);

        var medecinAvailabilityList =
                medecinAvailabilities.stream().sorted((a1, a2) -> {
                    System.out.printf("sort: %s; %s\n", a1, a2);
                    return a1.getShiftHoraire().getId().compareTo(a2.getShiftHoraire().getId());
                })
                        .map(a -> this.appointementMapper.toAvailabilityMedecinDto(a))
                        .collect(Collectors.toList());

        var result = this.tweakResponse.listAllAvailiblityByStatus(medecinAvailabilityList);

        return ResponseEntity.ok(result);
    }

}
