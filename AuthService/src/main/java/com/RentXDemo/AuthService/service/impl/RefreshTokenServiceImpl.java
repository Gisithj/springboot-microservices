package com.RentXDemo.AuthService.service.impl;

import com.RentXDemo.AuthService.dto.JwtResponseDto;
import com.RentXDemo.AuthService.dto.RefreshTokenRequestDto;
import com.RentXDemo.AuthService.entity.RefreshToken;
import com.RentXDemo.AuthService.entity.User;
import com.RentXDemo.AuthService.repository.RefreshTokenRepository;
import com.RentXDemo.AuthService.repository.UserRepository;
import com.RentXDemo.AuthService.service.IRefreshTokenService;
import com.RentXDemo.AuthService.service.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements IRefreshTokenService {

    @Autowired
    RefreshTokenRepository refreshTokenRepository;
    @Autowired
    JwtService jwtService;
    @Autowired
    UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(UserDetailServiceImpl.class);

    public RefreshToken createRefreshToken(String username){
        log.debug("creating refresh token...");
        RefreshToken refreshToken = RefreshToken.builder()
                .user(userRepository.findByUsername(username))
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(600000))
                .build();
        log.debug(refreshToken.getToken() + " Refresh token created successfully..!!");
        refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public RefreshToken updateRefreshToken(String username){

        // Find the existing refresh token
        User user = userRepository.findByUsername(username);
        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUser_Id(user.getId());

        if(existingToken.isPresent()){
            // Update the token and expiry date
            existingToken.get().setToken(UUID.randomUUID().toString());
            existingToken.get().setExpiryDate(new Date(System.currentTimeMillis()+1000*60*60*24).toInstant());

            // Save the updated token back to the database
            refreshTokenRepository.save(existingToken.get());

            log.debug(existingToken.get().getToken() + " Refresh token updated successfully..!!");
            return existingToken.get();
        } else {
            // If no existing token is found, create a new one
            return createRefreshToken(username);
        }
    }

    public Optional<RefreshToken> findByToken(String token){
        return refreshTokenRepository.findByToken(token);
    }
    public Optional<RefreshToken> findByUser_Id(Long userId){
        return refreshTokenRepository.findByUser_Id(userId);
    }
    public RefreshToken verifyExpiration(RefreshToken token){
        if(token.getExpiryDate().compareTo(Instant.now())<0){
            refreshTokenRepository.deleteById(token.getId());
            throw new RuntimeException(token.getToken() + " Refresh token is expired. Please make a new login..!");
        }
        return token;

    }
    public void deleteRefreshToken(String token){
        String username = jwtService.extractUsername(token);
        RefreshToken refreshToken = refreshTokenRepository.findByUser_Id(userRepository.findByUsername(username).getId())
                .orElseThrow(() -> new RuntimeException("Refresh Token is not in DB..!!"));
        refreshTokenRepository.deleteById(refreshToken.getId());
    }

    public JwtResponseDto GenerateJWTFromRefreshToken(RefreshTokenRequestDto refreshTokenRequestDTO){
        return findByToken(refreshTokenRequestDTO.getToken())
                .map(this::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(userInfo -> {
                    String accessToken = jwtService.GenerateToken(userInfo.getUsername());
                    return JwtResponseDto.builder()
                            .accessToken(accessToken)
                            .token(refreshTokenRequestDTO.getToken()).build();
                }).orElseThrow(() ->new RuntimeException("Refresh Token is not in DB..!!"));
    }

}
