package com.example.spliwise.splitwisee.controller;

import com.example.spliwise.splitwisee.Request_Response.JwtRequest;
import com.example.spliwise.splitwisee.Request_Response.JwtResponse;
import com.example.spliwise.splitwisee.dto.*;
import com.example.spliwise.splitwisee.entity.User;
import com.example.spliwise.splitwisee.exception.InvalidInputException;
import com.example.spliwise.splitwisee.exception.ResourceNotFoundException;
import com.example.spliwise.splitwisee.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@Valid @RequestBody UserDTO userDTO) {
        try {
            User user = userService.registerUser(userDTO);
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }


    }


    @GetMapping("/{userId}")
    public ResponseEntity<UserDetailsDTO> getUserDetails(@PathVariable Long userId) {
        try {
            UserDetailsDTO userDetails = userService.getUserDetailsWithExpenses(userId);
            return new ResponseEntity<>(userDetails, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody JwtRequest jwtRequest) {
        try {
            String jwt = String.valueOf(userService.authenticateUser(jwtRequest.getEmail(), jwtRequest.getPassword()));
            return new ResponseEntity<>(new JwtResponse("Login successful", jwt), HttpStatus.OK);
        } catch (InvalidInputException e) {
            return new ResponseEntity<>(new JwtResponse("Invalid email or password", null), HttpStatus.UNAUTHORIZED);
        }
    }
}