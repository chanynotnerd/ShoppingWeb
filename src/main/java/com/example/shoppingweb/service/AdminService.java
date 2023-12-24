package com.example.shoppingweb.service;

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
        User findUser = userRepository.findById(user.getId()).get();
        findUser.setUsername(user.getUsername());
        findUser.setPassword(passwordEncoder.encode(user.getPassword()));
        findUser.setEmail(user.getEmail());
        /*Authority authority = authorityRepository.findByAuthorityName(AuthorityName.valueOf(user.getAuthority()));
        findUser.setAuthority(authority);
*/
        return findUser;
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
