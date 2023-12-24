package com.example.shoppingweb.controller;

import com.example.shoppingweb.domain.Authority;
import com.example.shoppingweb.domain.User;
import com.example.shoppingweb.dto.OAuthType;
import com.example.shoppingweb.dto.ResponseDTO;
import com.example.shoppingweb.persistance.AuthorityRepository;
import com.example.shoppingweb.security.UserDetailsImpl;
import com.example.shoppingweb.service.AdminService;
import com.example.shoppingweb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private UserService userService;

    @Value("${kakao.default.password}")
    private String kakaoPassword;

    @GetMapping("/usermanage/{id}")
    public String getUser(@PathVariable int id, Model model, @AuthenticationPrincipal UserDetailsImpl principal) {
        User user = adminService.getUserById(id);
        model.addAttribute("user", user);
        return "admin/adminUpdateUser";
    }

    @PutMapping("/usermanage/{id}")
    public @ResponseBody ResponseDTO<?> updateUser(@RequestBody User user,
                                                   @AuthenticationPrincipal UserDetailsImpl principal) {
        // 회원 정보 수정 전, 로그인에 성공한 사용자가 카카오 회원인지 확인
        if (principal.getUser().getOauth().equals(OAuthType.KAKAO)) {
            // 카카오 회원일 경우 비밀번호 고정
            user.setPassword(kakaoPassword);
        }
        Authority authority = new Authority();
        authority = authorityRepository.save(authority);
        user.setAuthority(authority);

        // 회원 정보 수정과 동시에 세션 갱신
        principal.setUser(adminService.updateUser(user));
        return new ResponseDTO<>(HttpStatus.OK.value(), user.getUsername() + "수정 완료");
    }

    @GetMapping("/itemmanage")
    public String getItemManagePage() {
        return "/admin/itemmanage";
    }

    @GetMapping("/order")
    public String getOrderPage() {
        return "/admin/order";
    }

    @GetMapping("/usermanage")
    public String getUserPage(Model model) {
        List<User> users = adminService.getAllUsers();
        model.addAttribute("users", users);
        return "/admin/usermanage";
    }

    @GetMapping("/payment")
    public String getPaymentPage() {
        return "/admin/payment";
    }

    @GetMapping({"", "/"})
    public String getAdminMain() {
        return "/admin/adminIndex";
    }
}
