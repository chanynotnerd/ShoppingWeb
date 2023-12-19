package com.example.shoppingweb.service;

import com.example.shoppingweb.domain.RoleType;
import com.example.shoppingweb.domain.User;
import com.example.shoppingweb.persistance.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.function.Supplier;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public User updateUser(User user) {
        User findUser = userRepository.findById(user.getId()).get();
        findUser.setUsername(user.getUsername());
        findUser.setPassword(passwordEncoder.encode(user.getPassword()));
        findUser.setEmail(user.getEmail());
        findUser.setPostcode(user.getPostcode());
        findUser.setAddress(user.getAddress());
        findUser.setDetailAddress(user.getDetailAddress());

        return findUser;
    }

    @Transactional(readOnly = true)
    public User getUser(String username)
    {
        // 검색결과가 없을 때 빈 User 객체로 반환
        User findUser = userRepository.findByUsername(username).orElseGet(
                new Supplier<User>() {
                    @Override
                    public User get() {
                        return new User();
                    }
                });
        return findUser;
    }
    @Transactional(readOnly = true)
    public User getCurrentUser(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("principal");
        return user;
    }

    @Transactional(readOnly = true)
    public User findUserById(int id)
    {
        // 검색결과가 없을 때 빈 User 객체로 반환
        User findUser = userRepository.findById(id).orElseGet(User::new);
        return findUser;
    }

    @Transactional
    public void insertUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(RoleType.USER);
        userRepository.save(user);
    }

    /*@Transactional
    public User updateUser(User user)
    {
        User findUser=userRepository.findById(user.getId()).get();
        findUser.setUsername(user.getUsername());
        findUser.setPassword(passwordEncoder.encode(user.getPassword()));
        findUser.setEmail(user.getEmail());

        return findUser;
    }*/
}
