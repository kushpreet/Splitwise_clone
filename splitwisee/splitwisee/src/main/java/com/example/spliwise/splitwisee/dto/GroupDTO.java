package com.example.spliwise.splitwisee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupDTO {

    private Long id;

    @NotBlank(message = "Group name is mandatory")
    private String name;

    private List<Long> userIds;

    private List<ExpenseDTO> expenses;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExpenseDTO {
        private Long id;
        private BigDecimal amount;
        private Long paidBy; // User ID of the person who paid
        private List<UserShareDTO> userShares; // List of user shares

        // Nested DTO for User Share
        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class UserShareDTO {
            private Long userId;
            private BigDecimal shareAmount;
        }
    }
}