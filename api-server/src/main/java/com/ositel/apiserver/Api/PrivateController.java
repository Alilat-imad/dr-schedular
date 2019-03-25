package com.ositel.apiserver.Api;

import com.ositel.apiserver.Api.DtoMapper.AppointementMapper;
import com.ositel.apiserver.Api.DtoViewModel.Request.NewAppointmentRequest;
import com.ositel.apiserver.Api.DtoViewModel.Request.TodayAppointmentRequest;
import com.ositel.apiserver.Api.DtoViewModel.Response.ApiResponse;
import com.ositel.apiserver.Api.DtoViewModel.Response.TodayAppointmentResponse;
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

    public PrivateController(
            MedecinRepository medecinRepository
            , AppointementRepository appointementRepository
            , IMailSender mailSender
            , AppointementMapper appointementMapper
    ) {
        this.medecinRepository = medecinRepository;
        this.appointementRepository = appointementRepository;
        this.mailSender = mailSender;
        this.appointementMapper = appointementMapper;
    }

    // Display List of daily events of a doctor (Private cause it hold private patient data )
    @PreAuthorize("hasRole('MEDECIN')")
    @PostMapping("events/daily")
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


    // Delete an appointment
    @PreAuthorize("hasRole('MEDECIN')")
    @DeleteMapping("events/{appointmentId}")
    public ResponseEntity<?> delete(@PathVariable String appointmentId){
        var appointment = this.appointementRepository.findById(Long.parseLong(appointmentId));
        if(appointment.isEmpty()){
            return new ResponseEntity<>(new ApiResponse(false,"Appointment not found."),
                    HttpStatus.NOT_FOUND);
        }
        this.appointementRepository.deleteById(Long.parseLong(appointmentId));
        // Send Email
        this.mailSender.notifyPatient(false, appointment.get().getMedecin().getFullName() ,appointment.get().getPatient().getEmail(), appointment.get().getDate(), appointment.get().getShiftHoraire().getTimeStart(), appointment.get().getShiftHoraire().getTimeEnd());

        return new ResponseEntity<>(new ApiResponse(true,"Deleting appointment done successfully"),
                HttpStatus.OK);
    }

    // Change the status of an event
    @PreAuthorize("hasRole('MEDECIN')")
    @PostMapping("events/status/{appointmentId}")
    public ResponseEntity<?> status(@PathVariable String appointmentId){

        var appointment = this.appointementRepository.findById(Long.parseLong(appointmentId));

        if(appointment.isEmpty()){
            return new ResponseEntity<>(new ApiResponse(false,"Appointment not found."),
                    HttpStatus.NOT_FOUND);
        }
        var newAppointmentStatus = !appointment.get().isActive();
        appointment.get().setActive(newAppointmentStatus);

        this.appointementRepository.save(appointment.get());

        // Send Email
        this.mailSender.notifyPatient(newAppointmentStatus, appointment.get().getMedecin().getFullName() ,appointment.get().getPatient().getEmail(), appointment.get().getDate(), appointment.get().getShiftHoraire().getTimeStart(), appointment.get().getShiftHoraire().getTimeEnd());

        return new ResponseEntity<>(new ApiResponse(true,"Updating appointment done successfully"),
                HttpStatus.OK);
    }

}
