package com.example.shoppingweb.service;

import com.example.shoppingweb.domain.Authority;
import com.example.shoppingweb.domain.RoleType;
import com.example.shoppingweb.domain.User;
import com.example.shoppingweb.dto.OAuthType;
import com.example.shoppingweb.persistance.AuthorityRepository;
import com.example.shoppingweb.persistance.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthorityRepository authorityRepository;

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
    /*@Transactional(readOnly = true)
    public User getCurrentUser(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("principal");
        return user;
    }*/

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

        Authority authority = authorityRepository.findByAuthorityName(RoleType.USER);
        if (authority == null) {
            authority = new Authority();
            authority.setAuthorityName(RoleType.USER);
            authority.setTitle("USER");
            authorityRepository.save(authority);
        }
        user.setAuthority(authority);


        if (user.getOauth() == null) {
            user.setOauth(OAuthType.DEFAULT);
        }
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Page<User> getUserList(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    /*@Transactional(readOnly = true)
    public Page<User> userSearchList(String searchKeyword, Pageable pageable)
    {
        return userRepository.findByUserNameContaining(searchKeyword, pageable);
    }*/

    @Transactional(readOnly = true)
    public boolean isAdmin(User user) {
        return user.getRole() == RoleType.ADMIN;
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
