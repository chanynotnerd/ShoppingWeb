package com.example.shoppingweb.controller;

import com.example.shoppingweb.domain.RoleType;
import com.example.shoppingweb.domain.User;
import com.example.shoppingweb.persistance.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/user")
    public @ResponseBody String insertUser(@RequestBody User user)
    {
        user.setRole(RoleType.USER);
        userRepository.save(user);
        return user.getUsername() + " 회원가입 성공";
    }
}
