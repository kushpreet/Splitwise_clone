package com.example.spliwise.splitwisee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GroupResponseDTO {
    private Long id;
    private String groupName;
    private List<UserDTO> users;
    private List<ExpenseDTO> expenses;
}