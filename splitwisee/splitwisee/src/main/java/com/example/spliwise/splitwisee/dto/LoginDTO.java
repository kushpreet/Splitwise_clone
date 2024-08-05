package com.example.spliwise.splitwisee.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
public class LoginDTO {

    private String email;

    @NotBlank
    private String password;
}