package com.ositel.apiserver.Api.DtoViewModel.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StateAppointmentRequest {

    private Long appointmentId;
    private boolean status;

}
