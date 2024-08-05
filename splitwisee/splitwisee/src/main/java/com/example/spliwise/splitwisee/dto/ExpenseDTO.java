package com.example.spliwise.splitwisee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseDTO {
    private Long id;
    @NotNull
    private BigDecimal amount;
    private Long groupId;
    private Long paidById;
    private List<UserShareDTO> userShares;

}