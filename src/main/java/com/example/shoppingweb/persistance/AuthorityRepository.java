package com.example.shoppingweb.persistance;

import com.example.shoppingweb.domain.Authority;
import com.example.shoppingweb.domain.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Integer> {
    Authority findByAuthorityName(RoleType authorityName);
}
