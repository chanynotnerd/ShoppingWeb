package com.example.shoppingweb.controller;

import com.example.shoppingweb.domain.User;
import com.example.shoppingweb.dto.ResponseDTO;
import com.example.shoppingweb.dto.UserDTO;
import com.example.shoppingweb.dto.UserResponseDTO;
import com.example.shoppingweb.persistance.UserRepository;
import com.example.shoppingweb.security.UserDetailsImpl;
import com.example.shoppingweb.service.UserService;
import com.example.shoppingweb.token.JwtTokenProvider;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${kakao.default.password}")
    private String kakaoPassword;

    @GetMapping("/user/info")
    public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String authToken, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String token = authToken.substring("Bearer ".length());
        logger.info("asdftoken:" + token);
        /*if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증이 필요합니다.");
        }

        User user = userService.getUser(userDetails.getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
        }

        UserResponseDTO userResponseDTO = modelMapper.map(user, UserResponseDTO.class);
        return ResponseEntity.ok(userResponseDTO);*/
        if (userDetails == null) {
            logger.info("User details null");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증이 필요합니다.");
        }

        logger.info("Userinfo requested : " + userDetails.getUsername());
        User user = userService.getUser(userDetails.getUsername());
        return ResponseEntity.ok(user);
    }

    @PutMapping("/user")
    public @ResponseBody ResponseDTO<?> updateUser(@Valid @RequestBody UserDTO userDTO, BindingResult bindingResult) {
        /*System.out.println("principal: " + principal);
        if (principal == null || !principal.getUsername().equals(user.getUsername())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }


        principal.setUser(userService.updateUser(user));
        *//*User findUser = userService.getUser(user.getUsername());

        principal.setUser(userService.updateUser(findUser));*//*
        // 결과를 ResponseDTO로 래핑하여 반환
        return ResponseEntity.ok(new ResponseDTO<>(HttpStatus.OK.value(), "회원 정보 수정 완료"));*/
        // UserDTO 객체에 대한 유효성 검사, kakao 로그인 때문에 막아놓음
        // UserDTO를 User 객체로 변환
        User user = modelMapper.map(userDTO, User.class);
        User findUser = userService.getUser(user.getUsername());
        if (findUser.getUsername() != null) {
            // 유저 추가
            userService.updateUser(user);
            return new ResponseDTO<>(HttpStatus.OK.value(),
                    user.getUsername() + " 님 회원수정 성공");
        } else {
            return new ResponseDTO<>(HttpStatus.BAD_REQUEST.value(),
                    user.getUsername() + " 님 회원 수정 실패.");
        }
    }

    /*@PutMapping("/user")
    public @ResponseBody ResponseDTO<?> updateUser(@RequestBody User user,
                                                   @AuthenticationPrincipal UserDetailsImpl principal) {
        System.out.println("Session info: " + principal);
        if (principal.getUser().getOauth().equals(OAuthType.KAKAO)) {
            // 카카오 회원인 경우 비밀번호 고정
            user.setPassword(kakaoPassword);
        }

        principal.setUser(userService.updateUser(user));
        return new ResponseDTO<>(HttpStatus.OK.value(), user.getUsername() + " 회원 수정 완료");
    }
*/

    /*
    * @PostMapping("/auth/insertUser")
    public @ResponseBody ResponseDTO<?> insertUser(
            @Valid @RequestBody UserDTO userDTO, BindingResult bindingResult) {

        // UserDTO 객체에 대한 유효성 검사, kakao 로그인 때문에 막아놓음
        if (bindingResult.hasErrors()) {
            // 에러가 하나라도 있다면 에러 메세지를 Map에 등록
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMap.put(error.getField(), error.getDefaultMessage());
            }
            return new ResponseDTO<>(HttpStatus.BAD_REQUEST.value(), errorMap);
        }
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
    }*/

    @PostMapping("/auth/login")
    public ResponseEntity<UserResponseDTO> login(@RequestBody UserDTO userDTO, HttpServletResponse response,
                                                 Model model) {
        try {
            UserResponseDTO userResponseDTO = userService.login(userDTO);

            // JWT 토큰을 쿠키에 저장
            Cookie cookie = new Cookie("Authorization", userResponseDTO.getToken());
            cookie.setHttpOnly(true); // JavaScript를 통한 접근 방지
            cookie.setPath("/"); // 쿠키의 유효 경로 설정
            response.addCookie(cookie);

            System.out.println("Login successful: " + userResponseDTO.getToken()); // 추가된 로그

            return new ResponseEntity<>(userResponseDTO, HttpStatus.OK);
        } catch (UsernameNotFoundException | BadCredentialsException e) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/auth/logout")
    public String logout(HttpServletResponse response) {
        // JWT 토큰을 쿠키에 저장
        Cookie cookie = new Cookie("Authorization", null);
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true); // JavaScript를 통한 접근 방지
        cookie.setPath("/"); // 쿠키의 유효 경로 설정
        response.addCookie(cookie);

        return "redirect:/";
    }

    @GetMapping("auth/updateUser")
    public String updateUser() {
        return "th/user/updateUser";
    }

    @GetMapping("/auth/login")
    public String login() {
        return "th/system/login";
    }

    @GetMapping("/auth/insertUser")
    public String insertUser() {
        return "th/user/insertUser";
    }


    @PostMapping("/auth/insertUser")
    public @ResponseBody ResponseDTO<?> insertUser(
            @Valid @RequestBody UserDTO userDTO, BindingResult bindingResult) {

        // UserDTO 객체에 대한 유효성 검사, kakao 로그인 때문에 막아놓음
        if (bindingResult.hasErrors()) {
            // 에러가 하나라도 있다면 에러 메세지를 Map에 등록
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMap.put(error.getField(), error.getDefaultMessage());
            }
            return new ResponseDTO<>(HttpStatus.BAD_REQUEST.value(), errorMap);
        }
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
