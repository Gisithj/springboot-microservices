package com.RentXDemo.AuthService.controller;

import com.RentXDemo.AuthService.dto.*;
import com.RentXDemo.AuthService.service.JwtService;
import com.RentXDemo.AuthService.service.impl.RefreshTokenServiceImpl;
import com.RentXDemo.AuthService.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@CrossOrigin("*")
@RequestMapping(value = "/auth")
@RestController

public class AuthController {
    @Autowired
    private UserServiceImpl userServiceImpl;

    @Autowired
    private JwtService jwtService;

    @Autowired
    RefreshTokenServiceImpl refreshTokenService;
    @Autowired
    private AuthenticationManager authenticationManager;


    @PostMapping(value = "/sign-up")
    public ResponseEntity<JwtResponseDto> saveUser(@RequestBody UserRequest userRequest) {
        JwtResponseDto jwtResponseDto = userServiceImpl.saveUser(userRequest);
        return ResponseEntity.ok(jwtResponseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> AuthenticateAndGetToken(@RequestBody AuthRequestDto authRequestDTO){
            JwtResponseDto jwtResponseDto = userServiceImpl.loginAuthenticate(authRequestDTO);
            return ResponseEntity.ok(jwtResponseDto);
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token){
            refreshTokenService.deleteRefreshToken(token);
            return ResponseEntity.ok().body("Logout successfully..!!");

    }

    @PutMapping("/password-reset")
    public ResponseEntity<UserResponse> resetPassword(@RequestBody PasswordResetRequestDTO passwordResetRequestDTO){
            UserResponse userResponse = userServiceImpl.resetPassword(passwordResetRequestDTO);
            return ResponseEntity.ok().body(userResponse);

    }
    @PostMapping("/refreshToken")
    public ResponseEntity<JwtResponseDto> refreshToken(@RequestBody RefreshTokenRequestDto refreshTokenRequestDTO){
        JwtResponseDto jwtResponseDto = refreshTokenService.GenerateJWTFromRefreshToken(refreshTokenRequestDTO);
        return ResponseEntity.ok(jwtResponseDto);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        // Log the exception, then return an error response
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
