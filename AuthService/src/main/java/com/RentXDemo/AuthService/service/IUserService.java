package com.RentXDemo.AuthService.service;

import com.RentXDemo.AuthService.dto.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IUserService {

     JwtResponseDto saveUser(UserRequest userRequest);
     UserResponse getUser();
     List<UserResponse> getAllUser();

     UserResponse resetPassword(PasswordResetRequestDTO passwordResetRequestDTO);
     JwtResponseDto loginAuthenticate(AuthRequestDto authRequestDTO);
}
