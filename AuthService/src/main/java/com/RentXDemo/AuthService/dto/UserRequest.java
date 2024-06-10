package com.RentXDemo.AuthService.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserRequest {

    private String username;
    private String password;
    private String email;
    private String phone;
    private String nic;
    private String city;
    private String postalCode;
    private String role;


}