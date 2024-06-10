package com.RentXDemo.AuthService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PasswordResetRequestDTO {
    private String username;
    private String password;
}
