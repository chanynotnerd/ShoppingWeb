package com.example.shoppingweb.service;

import com.example.shoppingweb.domain.Authority;
import com.example.shoppingweb.domain.User;
import com.example.shoppingweb.persistance.AuthorityRepository;
import com.example.shoppingweb.persistance.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Supplier;

@Service
public class AdminService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public User updateUser(User user) {
        User findUser = userRepository.findById(user.getId()).orElse(null);
        if (findUser != null) {
            findUser.setUsername(user.getUsername());
            findUser.setPassword(passwordEncoder.encode(user.getPassword()));
            findUser.setEmail(user.getEmail());

            // 사용자의 권한을 업데이트
            Authority userAuthority = user.getAuthority();
            if (userAuthority != null) {
                Authority existingAuthority = authorityRepository.findByAuthorityName(userAuthority.getAuthorityName());
                findUser.setAuthority(existingAuthority);
            }
            System.out.println("Service incame" + user.getAuthority().getAuthorityName());

            return findUser;
        } else {
            throw new IllegalArgumentException("해당 사용자를 찾을 수 없습니다.");
        }
    }

    @Transactional(readOnly = true)
    public User getUserById(int id) {
        // 검색결과가 없을 때 빈 User 객체로 반환
        User findUser = userRepository.findById(id).orElseGet(
                new Supplier<User>() {
                    @Override
                    public User get() {
                        return new User();
                    }
                });
        return findUser;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
