package com.RentXDemo.AuthService.repository;
import com.RentXDemo.AuthService.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {

    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUser_Id(Long userId);
    void deleteById(Long id);
}
