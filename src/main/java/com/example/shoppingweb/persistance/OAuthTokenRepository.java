package com.example.shoppingweb.persistance;

import com.example.shoppingweb.domain.OAuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OAuthTokenRepository extends JpaRepository<OAuthToken, Integer> {
    OAuthToken findByUserId(int userId);
}
