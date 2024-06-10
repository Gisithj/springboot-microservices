package com.RentXDemo.AuthService.service;

import com.RentXDemo.AuthService.entity.RefreshToken;

import java.util.Optional;

public interface IRefreshTokenService {
    RefreshToken createRefreshToken(String username);
    RefreshToken updateRefreshToken(String username);
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUser_Id(Long userId);
    RefreshToken verifyExpiration(RefreshToken token);
    void deleteRefreshToken(String token);
}
