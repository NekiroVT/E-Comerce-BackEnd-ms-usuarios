package com.msusuarios.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerificarOtpRequest {
    private String email;
    private String otp;
}
