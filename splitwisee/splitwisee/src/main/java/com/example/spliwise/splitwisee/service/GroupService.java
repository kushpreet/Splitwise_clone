package com.example.spliwise.splitwisee.service;

import com.example.spliwise.splitwisee.dto.*;
import com.example.spliwise.splitwisee.entity.Expense;
import com.example.spliwise.splitwisee.entity.Group;
import com.example.spliwise.splitwisee.entity.User;
import com.example.spliwise.splitwisee.repository.GroupRepository;
import com.example.spliwise.splitwisee.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;
    @Transactional
    public Group createGroup(GroupDTO groupDTO) {
        Group group = new Group();
        group.setGroupName(groupDTO.getName());


        // Fetch users and set them to the group
        List<User> users = userRepository.findAllById(groupDTO.getUserIds());
        group.setUsers(users);

        return groupRepository.save(group);
    }

    public List<GroupDTO> getAllGroups() {
        List<Group> groups = groupRepository.findAll();
        return groups.stream().map(group -> {
            List<Long> userIds = group.getUsers().stream()
                    .map(User::getId)
                    .collect(Collectors.toList());

            List<GroupDTO.ExpenseDTO> expenseDTOs = group.getExpenses().stream()
                    .map(expense -> {
                        Long paidById = expense.getPaidBy() != null ? expense.getPaidBy().getId() : null;
                        List<GroupDTO.ExpenseDTO.UserShareDTO> userShares = group.getUsers().stream()
                                .map(user -> {
                                    BigDecimal shareAmount = calculateShareAmount(expense, user, paidById);
                                    return new GroupDTO.ExpenseDTO.UserShareDTO(user.getId(), shareAmount);
                                })
                                .collect(Collectors.toList());

                        return new GroupDTO.ExpenseDTO(
                                expense.getId(),
                                expense.getAmount(),
                                paidById,
                                userShares
                        );
                    })
                    .collect(Collectors.toList());

            return new GroupDTO(group.getId(), group.getGroupName(), userIds, expenseDTOs);
        }).collect(Collectors.toList());
    }

    private BigDecimal calculateShareAmount(Expense expense, User user, Long paidById) {
        if (expense.getUsers().isEmpty()) {
            return BigDecimal.ZERO;
        }
        // Exclude the user who paid from the sharing calculation
        if (user.getId().equals(paidById)) {
            return BigDecimal.ZERO;
        }
        // Calculate the number of users sharing the expense (excluding the one who paid)
        long numUsersSharing = expense.getUsers().stream()
                .filter(expenseUser -> !expenseUser.getId().equals(paidById))
                .count();
        // If no users are left to share the expense, return zero
        if (numUsersSharing == 0) {
            return BigDecimal.ZERO;
        }
        return expense.getAmount().divide(BigDecimal.valueOf(numUsersSharing), RoundingMode.HALF_UP);
    }
}