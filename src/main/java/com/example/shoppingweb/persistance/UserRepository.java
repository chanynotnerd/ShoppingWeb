package com.example.shoppingweb.persistance;

import com.example.shoppingweb.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    /*Optional<User> findById(int id);*/

    /*Page<User> findByUserNameContaining(String searchKeyword, Pageable pageable);*/
}
