package com.example.shoppingweb.controller;

import com.example.shoppingweb.domain.User;
import com.example.shoppingweb.dto.ResponseDTO;
import com.example.shoppingweb.dto.UserDTO;
import com.example.shoppingweb.security.UserDetailsImpl;
import com.example.shoppingweb.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @PutMapping("/user")
    public @ResponseBody ResponseDTO<?> updateUser(@RequestBody User user,
                                                   @AuthenticationPrincipal UserDetailsImpl principal) {
        System.out.println("세션 정보: " + principal);
        principal.setUser(userService.updateUser(user));
        // userService.updateUser(user);
        return new ResponseDTO<>(HttpStatus.OK.value(), user.getUsername() + " 회원 수정 완료");
    }


    @GetMapping("auth/updateUser")
    public String updateUser() {
        return "user/updateUser";
    }

    @GetMapping("/auth/login")
    public String login() {
        return "system/login";
    }

    @GetMapping("/auth/insertUser")
    public String insertUser() {
        return "user/insertUser";
    }


    @PostMapping("/auth/insertUser")
    public @ResponseBody ResponseDTO<?> insertUser(
            @Valid @RequestBody UserDTO userDTO, BindingResult bindingResult) {

        /*// UserDTO 객체에 대한 유효성 검사, kakao 로그인 때문에 막아놓음
        if (bindingResult.hasErrors()) {
            // 에러가 하나라도 있다면 에러 메세지를 Map에 등록
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMap.put(error.getField(), error.getDefaultMessage());
            }
            return new ResponseDTO<>(HttpStatus.BAD_REQUEST.value(), errorMap);
        }
*/
        // UserDTO를 User 객체로 변환
        User user = modelMapper.map(userDTO, User.class);
        User findUser = userService.getUser(user.getUsername());
        if (findUser.getUsername() == null) {
            // 유저 추가
            userService.insertUser(user);
            return new ResponseDTO<>(HttpStatus.OK.value(),
                    user.getUsername() + " 님 회원가입 성공");
        } else {
            return new ResponseDTO<>(HttpStatus.BAD_REQUEST.value(),
                    user.getUsername() + " 님은 이미 회원이십니다.");
        }
    }
}
