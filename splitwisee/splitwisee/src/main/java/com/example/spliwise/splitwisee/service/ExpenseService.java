package com.example.spliwise.splitwisee.service;
import com.example.spliwise.splitwisee.dto.ExpenseDTO;
import com.example.spliwise.splitwisee.dto.UserShareDTO;
import com.example.spliwise.splitwisee.entity.Expense;
import com.example.spliwise.splitwisee.entity.Group;
import com.example.spliwise.splitwisee.entity.User;
import com.example.spliwise.splitwisee.exception.ResourceNotFoundException;
import com.example.spliwise.splitwisee.repository.ExpenseRepository;
import com.example.spliwise.splitwisee.repository.GroupRepository;
import com.example.spliwise.splitwisee.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ExpenseService {
    private static final Logger logger = LoggerFactory.getLogger(ExpenseService.class);

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    public ExpenseDTO splitExpense(ExpenseDTO expenseDTO, List<Long> groupUserIds) {
        BigDecimal amount = expenseDTO.getAmount();
        BigDecimal shareAmount = amount.divide(new BigDecimal(groupUserIds.size()), BigDecimal.ROUND_HALF_UP);

        List<UserShareDTO> userShares = groupUserIds.stream()
                .map(userId -> new UserShareDTO(userId, shareAmount))
                .collect(Collectors.toList());

        expenseDTO.setUserShares(userShares);
        return expenseDTO;
    }

    @Transactional
    public ExpenseDTO addExpense(BigDecimal amount, Long groupId, Long paidById) {
        logger.info("Adding expense with amount: {}, groupId: {}, paidById: {}", amount, groupId, paidById);

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        User paidBy = userRepository.findById(paidById)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<User> users = group.getUsers();
        if (users.isEmpty()) {
            throw new ResourceNotFoundException("No users found in the group");
        }

        Expense expense = new Expense();
        expense.setAmount(amount);
        expense.setGroup(group);
        expense.setPaidBy(paidBy);
        expense.setUsers(new ArrayList<>(users)); // Ensure a distinct list of users
        expense = expenseRepository.save(expense);

        // Filter out the paidBy user from the shares calculation
        List<User> shareUsers = users.stream()
                .filter(user -> !user.getId().equals(paidById))
                .collect(Collectors.toList());

        BigDecimal shareAmount = amount.divide(BigDecimal.valueOf(shareUsers.size()), BigDecimal.ROUND_HALF_EVEN);
        List<UserShareDTO> userShares = shareUsers.stream()
                .map(user -> new UserShareDTO(user.getId(), shareAmount))
                .collect(Collectors.toList());

        // Add the share for the payer with zero amount
        userShares.add(new UserShareDTO(paidById, BigDecimal.ZERO));

        logger.info("Expense created with id: {}", expense.getId());

        return new ExpenseDTO(expense.getId(), amount, groupId, paidById, userShares);
    }
}