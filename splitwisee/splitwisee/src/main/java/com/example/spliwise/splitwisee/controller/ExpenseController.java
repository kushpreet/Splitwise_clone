package com.example.spliwise.splitwisee.controller;

import com.example.spliwise.splitwisee.dto.ExpenseDTO;
import com.example.spliwise.splitwisee.entity.Expense;
import com.example.spliwise.splitwisee.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ExpenseDTO> addExpense(@RequestBody ExpenseDTO expenseDTO) {
        ExpenseDTO createdExpense = expenseService.addExpense(expenseDTO.getAmount(), expenseDTO.getGroupId(), expenseDTO.getPaidById());
        return ResponseEntity.ok(createdExpense);
    }
    @GetMapping
    public ResponseEntity<List<Expense>> getAllExpenses() {
        List<Expense> expenses = expenseService.getAllExpenses();
        return ResponseEntity.ok(expenses);
    }


}
