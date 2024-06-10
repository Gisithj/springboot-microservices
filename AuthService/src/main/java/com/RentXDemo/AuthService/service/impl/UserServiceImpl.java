package com.RentXDemo.AuthService.service.impl;

import com.RentXDemo.AuthService.dto.*;
import com.RentXDemo.AuthService.entity.RefreshToken;
import com.RentXDemo.AuthService.entity.Role;
import com.RentXDemo.AuthService.entity.User;
import com.RentXDemo.AuthService.exception.EmailAlreadyExistsException;
import com.RentXDemo.AuthService.exception.UserAlreadyExistsException;
import com.RentXDemo.AuthService.repository.UserRepository;
import com.RentXDemo.AuthService.service.IUserService;
import com.RentXDemo.AuthService.service.JwtService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.lang.reflect.Type;
import java.util.List;
@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RefreshTokenServiceImpl refreshTokenService;
    @Autowired
    JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;
    ModelMapper modelMapper = new ModelMapper();

    @Override
    public JwtResponseDto saveUser(UserRequest userRequest) {
        try {
            // Check if username and password are present in the request
            if(userRequest == null){
                throw new RuntimeException("Parameters not found in request..!!");
            }
            // Check if a user with the same username already exists
            User existingUser = userRepository.findByUsername(userRequest.getUsername());
            if(existingUser!=null){
                throw new UserAlreadyExistsException("User found with the same username: " + userRequest.getUsername());
            }
            // Check if a user with the same email already exists
            User existingUserByEmail = userRepository.findByEmail(userRequest.getEmail());
            if(existingUserByEmail != null){
                throw new EmailAlreadyExistsException("User found with the same email: " + userRequest.getEmail());
            }

            // Encode the password
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String encodedPassword = encoder.encode(userRequest.getPassword());

            // Map the UserRequest details to a User entity
            User user = modelMapper.map(userRequest, User.class);

            // Set the role of the user
            switch (userRequest.getRole().toUpperCase()) {
                case "RENTER":
                    user.getRoles().add(Role.RENTER);
                    break;
                case "ADMIN":
                    user.getRoles().add(Role.ADMIN);
                    break;
                default:
                    user.getRoles().add(Role.CUSTOMER);
                    break;
            }
            user.setPassword(encodedPassword);
            User savedUser = userRepository.save(user);

            // Create a refresh token for the user
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedUser.getUsername());

            // Generate an access token for the user
            String accessToken = jwtService.GenerateToken(savedUser.getUsername());

            // Return a JwtResponseDto with the access token and refresh token
            return JwtResponseDto.builder()
                    .accessToken(accessToken)
                    .token(refreshToken.getToken()).build();
        }catch (Exception e) {
            return new JwtResponseDto(null, e.getMessage());
        }

    }

    @Override
    public UserResponse getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetail = (UserDetails) authentication.getPrincipal();
        // Get the username from the UserDetails object
        String usernameFromAccessToken = userDetail.getUsername();
        // Find the user in the database by username
        User user = userRepository.findByUsername(usernameFromAccessToken);
        // Return the User entity
        return modelMapper.map(user, UserResponse.class);

    }

    @Override
    public List<UserResponse> getAllUser() {
        // Get all users from the database
        List<User> users = (List<User>) userRepository.findAll();
        // Define the type of the list of UserResponse DTOs
        Type setOfDTOsType = new TypeToken<List<UserResponse>>(){}.getType();
        // Return the list of User entities
        return modelMapper.map(users, setOfDTOsType);
    }

    @Override
    public UserResponse resetPassword(PasswordResetRequestDTO passwordResetRequestDTO) {
        // Get the username from the UserDetails object
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetail = (UserDetails) authentication.getPrincipal();
        String usernameFromAccessToken = userDetail.getUsername();
        // Find the user in the database by username
        User user = userRepository.findByUsername(usernameFromAccessToken);
        // Encode the new password
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(passwordResetRequestDTO.getPassword());
        // Set the new password for the user
        user.setPassword(encodedPassword);
        // Save the user in the database
        User savedUser = userRepository.save(user);
        // Return the User entity
        return modelMapper.map(savedUser, UserResponse.class);
    }

    @Override
    public JwtResponseDto loginAuthenticate(AuthRequestDto authRequestDTO) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequestDTO.getUsername(), authRequestDTO.getPassword()));
        if(authentication.isAuthenticated()){
            RefreshToken refreshToken = refreshTokenService.updateRefreshToken(authRequestDTO.getUsername());
            return JwtResponseDto.builder()
                    .accessToken(jwtService.GenerateToken(authRequestDTO.getUsername()))
                    .token(refreshToken.getToken()).build();
        } else {
            throw new UsernameNotFoundException("invalid user request..!!");
        }
    }


}