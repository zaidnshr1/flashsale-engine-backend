package com.ecommerce.flashsale_platform.modules.auth.domain.repository;

import com.ecommerce.flashsale_platform.modules.auth.domain.model.RefreshToken;
import com.ecommerce.flashsale_platform.modules.auth.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    @Modifying
    void deleteByUser(User user);
}
