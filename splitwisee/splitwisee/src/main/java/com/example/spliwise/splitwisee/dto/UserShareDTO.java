package com.example.spliwise.splitwisee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class UserShareDTO {
    private Long userId;
    private BigDecimal shareAmount;
}