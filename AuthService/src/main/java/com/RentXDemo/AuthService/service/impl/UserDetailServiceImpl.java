package com.RentXDemo.AuthService.service.impl;
import com.RentXDemo.AuthService.entity.User;
import com.RentXDemo.AuthService.helpers.CustomUserDetails;
import com.RentXDemo.AuthService.repository.UserRepository;
//import com.RentXDemo.AuthService.helpers.CustomUserDetails;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor @NoArgsConstructor@Component
public class UserDetailServiceImpl implements UserDetailsService {
    @Autowired
    private  UserRepository userRepository;


    private static final Logger log = LoggerFactory.getLogger(UserDetailServiceImpl.class);

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {

        log.debug("Entering in loadUserByUsername Method...");
        User user = userRepository.findByUsername(username);
        if(user == null){
            log.error("Username not found: " + username);
            throw new UsernameNotFoundException("could not found user..!!");
        }
        log.info("User Authenticated Successfully..!!!");
        return new CustomUserDetails(user);

    }


}
