package com.example.spliwise.splitwisee.service;

import com.example.spliwise.splitwisee.dto.*;
import com.example.spliwise.splitwisee.entity.Expense;
import com.example.spliwise.splitwisee.entity.Group;
import com.example.spliwise.splitwisee.entity.User;
import com.example.spliwise.splitwisee.exception.InvalidInputException;
import com.example.spliwise.splitwisee.exception.ResourceNotFoundException;
import com.example.spliwise.splitwisee.jwt.JwtHelper;
import com.example.spliwise.splitwisee.repository.ExpenseRepository;
import com.example.spliwise.splitwisee.repository.GroupRepository;
import com.example.spliwise.splitwisee.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private JwtHelper jwtHelper;


    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(@Valid UserDTO userDTO) {
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new InvalidInputException("Username is already taken");
        }
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new InvalidInputException("Email is already registered");
        }
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setEmail(userDTO.getEmail());

        return userRepository.save(user);
    }


    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
    @Transactional
    public UserDetailsDTO getUserDetailsWithExpenses(Long userId) {
        User user = getUserById(userId);

        List<Group> groups = groupRepository.findAllByUsers(user);

        // Calculate the total expenses for the user
        BigDecimal totalExpenses = groups.stream()
                .flatMap(group -> group.getExpenses().stream())
                .filter(expense -> expense.getUsers().contains(user))
                .map(expense -> calculateUserShare(expense, user))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // Create DTOs for each group
        List<GroupDTO> groupDTOs = groups.stream()
                .map(group -> {
                    List<GroupDTO.ExpenseDTO> expenseDTOs = group.getExpenses().stream()
                            .map(expense -> new GroupDTO.ExpenseDTO(
                                    expense.getId(),
                                    expense.getAmount(),
                                    expense.getPaidBy() != null ? expense.getPaidBy().getId() : null,
                                    group.getUsers().stream()
                                            .map(expenseUser -> new GroupDTO.ExpenseDTO.UserShareDTO(
                                                    expenseUser.getId(),
                                                    calculateUserShare(expense, expenseUser)))
                                            .collect(Collectors.toList())))
                            .collect(Collectors.toList());

                    List<Long> userIds = group.getUsers().stream()
                            .map(User::getId)
                            .collect(Collectors.toList());

                    return new GroupDTO(
                            group.getId(),
                            group.getGroupName(),
                            userIds,
                            expenseDTOs
                    );
                })
                .collect(Collectors.toList());

        return new UserDetailsDTO(user.getId(), user.getUsername(), user.getEmail(), totalExpenses, groupDTOs);
    }

    // Calculates the amount a user should owe, excluding the payer
    private BigDecimal calculateUserShare(Expense expense, User user) {
        if (expense.getUsers().isEmpty()) {
            return BigDecimal.ZERO;
        }

        // If the user is the payer, they owe nothing
        if (expense.getPaidBy() != null && expense.getPaidBy().equals(user)) {
            return BigDecimal.ZERO;
        }

        long numUsersSharing = expense.getUsers().stream()
                .filter(expenseUser -> !expenseUser.equals(expense.getPaidBy()))
                .count();
        if (numUsersSharing == 0) {
            return BigDecimal.ZERO;
        }

        // Calculate the share amount excluding the payer
        return expense.getAmount().divide(BigDecimal.valueOf(numUsersSharing), RoundingMode.HALF_UP);
    }
    public String authenticateUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidInputException("Invalid email or password"));

        // Check if the password matches
        System.out.println(password+":"+user.getPassword());
        if (passwordEncoder.matches(password, user.getPassword())) {
            // Generate JWT token
            return jwtHelper.generateToken(user.getEmail());
        } else {
            throw new InvalidInputException("Invalid email or password");
        }
    }
}