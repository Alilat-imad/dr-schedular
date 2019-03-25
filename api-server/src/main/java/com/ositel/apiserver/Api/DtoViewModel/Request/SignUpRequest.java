package com.ositel.apiserver.Api.DtoViewModel.Request;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class SignUpRequest {


    @NotBlank
    private String fullName;
    private String phone;
    private String address;




    @NotBlank
    @Size(min = 4, max = 15)
    private String username;

    @NotBlank
    @NaturalId
    @Size(max = 40)
    @Email
    private String email;

    @NotBlank
    @Size(min = 5, max = 30)
    private String password;

}
