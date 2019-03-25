package com.ositel.apiserver.Api;

import com.ositel.apiserver.Api.DtoMapper.AppointementMapper;
import com.ositel.apiserver.Api.DtoViewModel.Request.MedecinAvailabilityRequest;
import com.ositel.apiserver.Api.DtoViewModel.Request.NewAppointmentRequest;
import com.ositel.apiserver.Api.DtoViewModel.Response.ApiResponse;
import com.ositel.apiserver.Service.PublicService;
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

    @Autowired
    private PublicService publicService;
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

        var saveAppointement = this.publicService.save(newAppointment);
        return ResponseEntity.ok(saveAppointement);

    }

    // EndPoint : List all available Medecin
    @GetMapping("/medecin")
    public ResponseEntity<? extends Object> listMedecin(){
        var ListMedecin = this.publicService.listAllMedecin();
        return ResponseEntity.ok(ListMedecin);
    }

    // End Point : Get list of availability of a doctor by day.
    @PostMapping("/availability")
    public ResponseEntity<? extends Object> listMedecinAvailability(@Valid @RequestBody MedecinAvailabilityRequest medecinAvailability, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new ValidationException("Availabilities has errors; Can not fetch all medecin availabilities;");
        }
        var result = this.publicService.listAllMedecinAvailability(medecinAvailability);

        if(result==null)
            return new ResponseEntity<>(new ApiResponse(false,"Medecin not found.."),
                    HttpStatus.NOT_FOUND);

        return ResponseEntity.ok(result);
    }

}
