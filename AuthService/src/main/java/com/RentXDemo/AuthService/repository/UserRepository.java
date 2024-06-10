package com.RentXDemo.AuthService.repository;

import com.RentXDemo.AuthService.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    User findByUsername(String username);
    User findByEmail(String email);
    User findFirstById(Long id);
    void deleteByUsername(String username);
}
