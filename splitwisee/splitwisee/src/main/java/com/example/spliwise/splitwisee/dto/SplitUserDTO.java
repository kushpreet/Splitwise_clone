package com.example.spliwise.splitwisee.dto;

import jakarta.persistence.Entity;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
@Data

public class SplitUserDTO {
    @NotNull
    private Long userId;

    @NotNull
    private String name;

    @NotNull
    @Email
    private String email;
}