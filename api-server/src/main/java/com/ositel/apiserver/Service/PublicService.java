package com.ositel.apiserver.Service;

import com.ositel.apiserver.Api.DtoMapper.AppointementMapper;
import com.ositel.apiserver.Api.DtoViewModel.Request.MedecinAvailabilityRequest;
import com.ositel.apiserver.Api.DtoViewModel.Request.NewAppointmentRequest;
import com.ositel.apiserver.Api.DtoViewModel.Response.ApiResponse;
import com.ositel.apiserver.Api.DtoViewModel.Response.AvailabilityMedecinResponse;
import com.ositel.apiserver.db.AppointementRepository;
import com.ositel.apiserver.db.MedecinRepository;
import com.ositel.apiserver.db.PatientRepository;
import com.ositel.apiserver.db.ShiftHoraireRepository;
import com.ositel.apiserver.mail.IMailSender;
import com.ositel.apiserver.model.Appointement;
import com.ositel.apiserver.model.Medecin;
import com.ositel.apiserver.model.Patient;
import com.ositel.apiserver.utils.TweakResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PublicService {

    private final static Logger LOGGER = LoggerFactory.getLogger(MedecinService.class);

    private MedecinRepository medecinRepository;
    private PatientRepository patientRepository;
    private ShiftHoraireRepository shiftHoraireRepository;
    private AppointementRepository appointementRepository;
    private IMailSender mailSender;
    private AppointementMapper appointementMapper;
    private TweakResponse tweakResponse;
    @Autowired
    public PublicService(MedecinRepository medecinRepository, PatientRepository patientRepository, ShiftHoraireRepository shiftHoraireRepository, AppointementRepository appointementRepository, IMailSender mailSender, AppointementMapper appointementMapper, TweakResponse tweakResponse) {
        this.medecinRepository = medecinRepository;
        this.patientRepository = patientRepository;
        this.shiftHoraireRepository = shiftHoraireRepository;
        this.appointementRepository = appointementRepository;
        this.mailSender = mailSender;
        this.appointementMapper = appointementMapper;
        this.tweakResponse = tweakResponse;
    }

    // List all available Medecin
    public List<Medecin> listAllMedecin(){
        return this.medecinRepository.findAll();
    }

    // Get list of availability of a doctor by day.
    public List<AvailabilityMedecinResponse> listAllMedecinAvailability(MedecinAvailabilityRequest medecinAvailability){

        var medecin = this.medecinRepository.findById(medecinAvailability.getMedecinId());
        var date = LocalDate.parse(medecinAvailability.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        if(medecin.isEmpty()){
            return null;
        }
        var medecinAvailabilities = this.appointementRepository.findAllByMedecinAndDate(medecin.get(), date);

        var medecinAvailabilityList =
                medecinAvailabilities.stream().sorted((a1, a2) -> {
                    System.out.printf("sort: %s; %s\n", a1, a2);
                    return a1.getShiftHoraire().getId().compareTo(a2.getShiftHoraire().getId());
                })
                        .map(a -> this.appointementMapper.toAvailabilityMedecinDto(a))
                        .collect(Collectors.toList());

        return this.tweakResponse.listAllAvailiblityByStatus(medecinAvailabilityList);
    }

    // Add new appointement;
    public Object save(NewAppointmentRequest newAppointment){

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

        return appointement;
    }
}
