package com.RentXDemo.AuthService.controller;

import com.RentXDemo.AuthService.dto.UserResponse;
import com.RentXDemo.AuthService.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RequestMapping(value = "/user")
@RestController
public class UserController {
    @Autowired
    private UserServiceImpl userServiceImpl;
    @GetMapping("/getUsers")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        try {
            List<UserResponse> userResponses = userServiceImpl.getAllUser();
            return ResponseEntity.ok(userResponses);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/getUserProfile")
    public ResponseEntity<UserResponse> getUserProfile() {
        try {
            UserResponse userResponse = userServiceImpl.getUser();
            return ResponseEntity.ok().body(userResponse);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/test")
    public String test() {
        return "Welcome";
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        // Log the exception, then return an error response
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
