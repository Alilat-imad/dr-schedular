package com.ositel.apiserver.Api.DtoViewModel;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Getter
public class NotificationRequest {
    // TODO: add in repository : findByID !
    @NotBlank
    private Long userId;
    @NotBlank
    private Date appointement;

}
