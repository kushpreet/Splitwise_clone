package com.example.spliwise.splitwisee.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String username;
    private String email;
    private List<ExpenseDTO> expenses;
    private List<GroupDTO> groups;
    private String name;
}