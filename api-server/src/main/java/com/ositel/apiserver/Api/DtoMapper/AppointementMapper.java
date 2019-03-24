package com.ositel.apiserver.Api.DtoMapper;

import com.ositel.apiserver.Api.DtoViewModel.TodayAppointmentListDto;
import com.ositel.apiserver.Api.DtoViewModel.TodayAppointmentResponse;
import com.ositel.apiserver.db.AppointementRepository;
import com.ositel.apiserver.db.MedecinRepository;
import com.ositel.apiserver.model.Appointement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.text.Element;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AppointementMapper {

    @Autowired
    private AppointementRepository appointementRepository;
    @Autowired
    private MedecinRepository medecinRepository;

    public TodayAppointmentResponse toDto(List<Appointement> appointmentList, Appointement appointement, int size){

        var TodayAppointmentResponse = new TodayAppointmentResponse();

        TodayAppointmentResponse.setDate(appointement.getDate());
        TodayAppointmentResponse.setSize(size);
        TodayAppointmentResponse.setEvents(
                appointmentList.stream()
                        .sorted((a1, a2) -> {
                            System.out.printf("sort: %s; %s\n", a1, a2);
                            return a1.getShiftHoraire().getId().compareTo(a2.getShiftHoraire().getId());
                        })
                        .map(a -> this.test(a))

                        .collect(Collectors.toList())
        );

        return TodayAppointmentResponse;
    }


    public TodayAppointmentListDto test(Appointement appointement){

        var todayAppointmentListDto = new TodayAppointmentListDto();
        todayAppointmentListDto.setAppointmentId(appointement.getId());
        todayAppointmentListDto.setPatientEmail(appointement.getPatient().getEmail());
        todayAppointmentListDto.setPatientName(appointement.getPatient().getFullName());
        todayAppointmentListDto.setStatus(appointement.isActive());
        todayAppointmentListDto.setShift_horaire_id(appointement.getShiftHoraire().getId());
        todayAppointmentListDto.setTimeStart(appointement.getShiftHoraire().getTimeStart());
        todayAppointmentListDto.setTimeEnd(appointement.getShiftHoraire().getTimeEnd());
        return todayAppointmentListDto;
    }

}
