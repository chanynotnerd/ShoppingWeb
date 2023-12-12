package com.example.shoppingweb.controller;

import com.example.shoppingweb.dto.ResponseDTO;
import com.example.shoppingweb.domain.User;
import com.example.shoppingweb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
public class LoginController {
    @Autowired
    private UserService userService;

    @GetMapping("/auth/logout")
    public String logout(HttpSession session)
    {
        session.invalidate();
        return "redirect:/";
    }

    @PostMapping("/auth/login")
    public @ResponseBody ResponseDTO<?> login(@RequestBody User user,
                                              HttpSession session)
    {
        User findUser = userService.getUser(user.getUsername());

        if (findUser.getUsername() == null) {
            return new ResponseDTO<>(HttpStatus.BAD_REQUEST.value(),
                    "아이디가 존재하지 않습니다.");
        } else {
            if (user.getPassword().equals(findUser.getPassword())) {
                session.setAttribute("principal", findUser);
                return new ResponseDTO<>(HttpStatus.OK.value(),
                        findUser.getUsername() + "님 로그인 성공하셨습니다");
            } else {
                return new ResponseDTO<>(HttpStatus.BAD_REQUEST.value(),
                        "비밀번호 오류");
            }
        }
    }
    @GetMapping("/auth/login")
    public String login()
    {
        return "system/login";
    }
}
