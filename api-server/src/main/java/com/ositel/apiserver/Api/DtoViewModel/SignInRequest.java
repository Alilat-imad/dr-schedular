package com.ositel.apiserver.Api.DtoViewModel;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class SignInRequest {

    @NotBlank
    @Size(min = 3)
    private String usernameOrEmail;

    @NotBlank
    private String password;

    // Getters & Setters

    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }

    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
