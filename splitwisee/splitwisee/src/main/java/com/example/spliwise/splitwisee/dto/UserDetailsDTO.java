package com.example.spliwise.splitwisee.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsDTO {
    private Long id;
    private String username;
    private String email;
    private BigDecimal totalExpenses;
    private List<GroupDTO> groups;

}