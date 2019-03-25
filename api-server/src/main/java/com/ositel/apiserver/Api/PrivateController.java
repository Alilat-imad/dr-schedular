package com.ositel.apiserver.Api;

import com.ositel.apiserver.Api.DtoMapper.AppointementMapper;
import com.ositel.apiserver.Api.DtoViewModel.Request.NewAppointmentRequest;
import com.ositel.apiserver.Api.DtoViewModel.Request.TodayAppointmentRequest;
import com.ositel.apiserver.Api.DtoViewModel.Response.ApiResponse;
import com.ositel.apiserver.Api.DtoViewModel.Response.TodayAppointmentResponse;
import com.ositel.apiserver.Service.MedecinService;
import com.ositel.apiserver.db.AppointementRepository;
import com.ositel.apiserver.db.MedecinRepository;
import com.ositel.apiserver.db.PatientRepository;
import com.ositel.apiserver.db.ShiftHoraireRepository;
import com.ositel.apiserver.mail.IMailSender;
import com.ositel.apiserver.model.Appointement;
import com.ositel.apiserver.model.Patient;
import com.ositel.apiserver.security.CurrentUser;
import com.ositel.apiserver.security.UserPrincipal;
import com.ositel.apiserver.utils.TweakResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/private")
@CrossOrigin
public class PrivateController {

    private MedecinRepository medecinRepository;
    private AppointementRepository appointementRepository;
    private IMailSender mailSender;
    private AppointementMapper appointementMapper;
    private MedecinService medecinService;

    @Autowired
    public PrivateController(MedecinRepository medecinRepository, AppointementRepository appointementRepository, IMailSender mailSender, AppointementMapper appointementMapper, MedecinService medecinService) {
        this.medecinRepository = medecinRepository;
        this.appointementRepository = appointementRepository;
        this.mailSender = mailSender;
        this.appointementMapper = appointementMapper;
        this.medecinService = medecinService;
    }

    // Display List of daily events of a doctor (Private cause it hold private patient data )
    @PreAuthorize("hasRole('MEDECIN')")
    @PostMapping("events/daily")
    public ResponseEntity<? extends Object> today(@CurrentUser UserPrincipal currentUser, @Valid @RequestBody TodayAppointmentRequest todayAppointment, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            throw new ValidationException("Appointment has errors; Can not update the status of the appointment;");
        }
        var date = LocalDate.parse(todayAppointment.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        var response = this.medecinService.AllMedecinAvailability(currentUser, todayAppointment);
        if(response==null)
            return ResponseEntity.ok(new TodayAppointmentResponse(date, 0, null));
        return ResponseEntity.ok(response);

    }

    // Delete an appointment
    @PreAuthorize("hasRole('MEDECIN')")
    @DeleteMapping("events/{appointmentId}")
    public ResponseEntity<?> delete(@PathVariable String appointmentId){

        var response =this.medecinService.deleteEvent(appointmentId);
        return ResponseEntity.ok(response);
    }

    // Change the status of an event
    @PreAuthorize("hasRole('MEDECIN')")
    @PostMapping("events/status/{appointmentId}")
    public ResponseEntity<?> status(@PathVariable String appointmentId){

        var response = this.medecinService.changeEventStatus(appointmentId);
        return ResponseEntity.ok(response);
    }

}
