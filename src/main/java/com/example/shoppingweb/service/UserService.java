package com.example.shoppingweb.service;

import com.example.shoppingweb.domain.RoleType;
import com.example.shoppingweb.domain.User;
import com.example.shoppingweb.persistance.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

/*    @Transactional
    public User updateUser(User user)
    {
        User findUser=userRepository.findById(user.getId()).get();
        findUser.setUsername(user.getUsername());
        findUser.setPassword(passwordEncoder.encode(user.getPassword()));
        findUser.setEmail(user.getEmail());

        return findUser;
    }*/

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
    @Transactional
    public void insertUser(User user) {
       /* // 비밀번호를 암호화하여 설정한다.
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        user.setRole(RoleType.USER);
        if(user.getOauth() == null)
        {
            user.setOauth(OAuthType.JBLOG);
        }*/
        user.setRole(RoleType.USER);
        userRepository.save(user);
    }
}
