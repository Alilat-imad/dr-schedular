package com.ositel.apiserver.Service;

import com.ositel.apiserver.Api.DtoMapper.AppointementMapper;
import com.ositel.apiserver.Api.DtoViewModel.Request.TodayAppointmentRequest;
import com.ositel.apiserver.Api.DtoViewModel.Response.ApiResponse;
import com.ositel.apiserver.Api.DtoViewModel.Response.TodayAppointmentResponse;
import com.ositel.apiserver.db.AppointementRepository;
import com.ositel.apiserver.db.MedecinRepository;
import com.ositel.apiserver.db.PatientRepository;
import com.ositel.apiserver.db.ShiftHoraireRepository;
import com.ositel.apiserver.mail.IMailSender;
import com.ositel.apiserver.security.UserPrincipal;
import com.ositel.apiserver.utils.TweakResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class MedecinService {

    private final static Logger LOGGER = LoggerFactory.getLogger(MedecinService.class);

    private MedecinRepository medecinRepository;
    private PatientRepository patientRepository;
    private ShiftHoraireRepository shiftHoraireRepository;
    private AppointementRepository appointementRepository;
    private IMailSender mailSender;
    private AppointementMapper appointementMapper;
    private TweakResponse tweakResponse;
    @Autowired
    public MedecinService(MedecinRepository medecinRepository, PatientRepository patientRepository, ShiftHoraireRepository shiftHoraireRepository, AppointementRepository appointementRepository, IMailSender mailSender, AppointementMapper appointementMapper, TweakResponse tweakResponse) {
        this.medecinRepository = medecinRepository;
        this.patientRepository = patientRepository;
        this.shiftHoraireRepository = shiftHoraireRepository;
        this.appointementRepository = appointementRepository;
        this.mailSender = mailSender;
        this.appointementMapper = appointementMapper;
        this.tweakResponse = tweakResponse;
    }

    // Display List of daily events of a doctor (Private cause it hold private patient data )
    public Object AllMedecinAvailability(UserPrincipal currentUser, @RequestBody TodayAppointmentRequest todayAppointment){
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
            return null;
        }
        int size = isAvailable.size();
        var response = this.appointementMapper.toDto(isAvailable, isAvailable.get(0), size);
        return response;
    }


    // Delete Event.
    public Object deleteEvent(String appointmentId){
        var appointment = this.appointementRepository.findById(Long.parseLong(appointmentId));
        if(appointment.isEmpty()){
            return new ResponseEntity<>(new ApiResponse(false,"Appointment not found."),
                    HttpStatus.NOT_FOUND);
        }
        this.appointementRepository.deleteById(Long.parseLong(appointmentId));
        // Send Email
//        this.mailSender.notifyPatient(false, appointment.get().getMedecin().getFullName() ,appointment.get().getPatient().getEmail(), appointment.get().getDate(), appointment.get().getShiftHoraire().getTimeStart(), appointment.get().getShiftHoraire().getTimeEnd());

        return new ResponseEntity<>(new ApiResponse(true,"Event supprimer avec success"),
                HttpStatus.OK);
    }

    // Change Event Status :
    public Object changeEventStatus(String appointmentId){
        var appointment = this.appointementRepository.findById(Long.parseLong(appointmentId));

        if(appointment.isEmpty()){
            return new ResponseEntity<>(new ApiResponse(false,"Appointment not found."),
                    HttpStatus.NOT_FOUND);
        }
        var newAppointmentStatus = !appointment.get().isActive();
        appointment.get().setActive(newAppointmentStatus);

        this.appointementRepository.save(appointment.get());

        // Send Email
//        this.mailSender.notifyPatient(newAppointmentStatus, appointment.get().getMedecin().getFullName() ,appointment.get().getPatient().getEmail(), appointment.get().getDate(), appointment.get().getShiftHoraire().getTimeStart(), appointment.get().getShiftHoraire().getTimeEnd());
        return new ResponseEntity<>(new ApiResponse(true,"Event modifier avec success"),
                HttpStatus.OK);
    }
}
