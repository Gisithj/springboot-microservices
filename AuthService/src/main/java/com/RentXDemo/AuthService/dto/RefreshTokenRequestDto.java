package com.RentXDemo.AuthService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author mhmdz
 * Created By Zeeshan on 20-05-2023
 * @project oauth-jwt
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenRequestDto {
    private String token;
}