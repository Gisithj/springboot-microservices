package com.RentXDemo.AuthService.dto;

import com.RentXDemo.AuthService.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserResponse{
    private Long id;
    private String username;
    private String phone;
    private String nic;
    private String city;
    private String postalCode;
    private Set<Role> roles;

}
