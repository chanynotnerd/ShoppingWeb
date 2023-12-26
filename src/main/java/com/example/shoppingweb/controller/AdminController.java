package com.example.shoppingweb.controller;

import com.example.shoppingweb.domain.Authority;
import com.example.shoppingweb.domain.Item;
import com.example.shoppingweb.domain.User;
import com.example.shoppingweb.dto.OAuthType;
import com.example.shoppingweb.dto.ResponseDTO;
import com.example.shoppingweb.persistance.AuthorityRepository;
import com.example.shoppingweb.persistance.ItemRepository;
import com.example.shoppingweb.persistance.UserRepository;
import com.example.shoppingweb.security.UserDetailsImpl;
import com.example.shoppingweb.service.AdminService;
import com.example.shoppingweb.service.ItemService;
import com.example.shoppingweb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private SecurityContextLogoutHandler securityContextLogoutHandler;

    @Autowired
    private AdminService adminService;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Value("${kakao.default.password}")
    private String kakaoPassword;

    @GetMapping("/usermanage/search")
    public String searchUsers(@RequestParam("name") String searchKeyword, Model model,
                              @RequestParam(defaultValue = "0") Integer pageNo,
                              @RequestParam(defaultValue = "10") Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<User> userList = userRepository.findByUsernameContaining(searchKeyword, pageable);
        model.addAttribute("users", userList);
        return "/admin/usermanage";
    }

    @DeleteMapping("/usermanage/{id}")
    public @ResponseBody ResponseDTO<?> deleteUser(@PathVariable int id, @AuthenticationPrincipal UserDetailsImpl principal) {
        adminService.deleteUser(id);
        return new ResponseDTO<>(HttpStatus.OK.value(), "사용자 삭제 완료");
    }

    @GetMapping("/usermanage/{id}")
    public String getUser(@PathVariable int id, Model model, @AuthenticationPrincipal UserDetailsImpl principal) {
        User user = adminService.getUserById(id);
        model.addAttribute("user", user);
        return "admin/adminUpdateUser";
    }

    @PutMapping("/usermanage/{id}")
    public @ResponseBody ResponseDTO<?> updateUser(@RequestBody User user,
                                                   @AuthenticationPrincipal UserDetailsImpl principal,
                                                   HttpServletRequest request, HttpServletResponse response) {
        // System.out.println("Controller incame" + user.getAuthority().getAuthorityName());


        // 회원 정보 수정 전, 로그인에 성공한 사용자가 카카오 회원인지 확인
        if (principal.getUser().getOauth().equals(OAuthType.KAKAO)) {
            // 카카오 회원일 경우 비밀번호 고정
            user.setPassword(kakaoPassword);
        }

        Authority authority = user.getAuthority();
        boolean isAuthorityChanged = false;
        if (authority != null) {
            // 기존 Authority 엔티티 가져오기
            Authority existingAuthority = authorityRepository.findByAuthorityName(authority.getAuthorityName());
            if (!existingAuthority.equals(principal.getUser().getAuthority())) {
                isAuthorityChanged = true;
            }
            user.setAuthority(existingAuthority);
        }

        // 회원 정보 수정과 동시에 세션 갱신
        try {
            principal.setUser(adminService.updateUser(user));
            // 사용자의 권한이 변경되었을 경우 로그아웃 처리.
            if (isAuthorityChanged) {
                // 이거 쓸라믄 시큐리티 환경설정 클래스에서 Bean 추가하고 써야 한다. 이거 몰라서 헤맸다.
                securityContextLogoutHandler.logout(request, response, SecurityContextHolder.getContext().getAuthentication());
                return new ResponseDTO<>(HttpStatus.OK.value(), "권한 변경 완료. 다시 로그인해주세요.");
            }

            return new ResponseDTO<>(HttpStatus.OK.value(), user.getUsername() + " 수정 완료");
        } catch (IllegalArgumentException e) {
            return new ResponseDTO<>(HttpStatus.BAD_REQUEST.value(), "유저를 찾을 수 없습니다.");
        }
    }

    @GetMapping("/itemmanage/search")
    public String searchItems(@RequestParam("name") String searchKeyword, Model model,
                              @RequestParam(defaultValue = "0") Integer pageNo,
                              @RequestParam(defaultValue = "10") Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Item> itemList = itemRepository.findByItemNameContaining(searchKeyword, pageable);
        model.addAttribute("items", itemList);
        return "/admin/itemmanage";
    }

    @GetMapping("/itemmanage/insert")
    public String getItemInsert() {
        return "/admin/adminInsertItem";
    }

    @PostMapping("/itemmanage/insert")
    public @ResponseBody ResponseDTO<?> insertItem(@ModelAttribute Item item,
                                                   @RequestParam("itemImage") MultipartFile file) {
        adminService.insertItem(item, file);
        return new ResponseDTO<>(HttpStatus.OK.value(), "아이템 추가 완료.");
    }
    @GetMapping("/itemmanage/{id}")
    public String updateItem(@PathVariable int id, Model model) {
        Item item = itemService.getItem(id);
        model.addAttribute("item", item);
        return "admin/adminUpdateItem";
    }

    @PutMapping("/itemmanage/{id}")
    public @ResponseBody ResponseDTO<?> updateItem(@RequestBody Item item) {
        adminService.updateItem(item);
        return new ResponseDTO<>(HttpStatus.OK.value(),
                item.getId() + "번 아이템를 수정했습니다.");
    }

    @DeleteMapping("/itemmanage/{id}")
    public @ResponseBody ResponseDTO<?> deleteItem(@PathVariable int id) {
        adminService.deleteItem(id);
        return new ResponseDTO<>(HttpStatus.OK.value(), id + "번 아이템 삭제.");
    }

    @GetMapping("/itemmanage")
    public String getItemList(
            Model model,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Item> itemList = adminService.getItemList(pageable);
        model.addAttribute("items", itemList);

        return "/admin/itemmanage";
    }

    @GetMapping("/order")
    public String getOrderPage() {
        return "/admin/order";
    }

    @GetMapping("/usermanage")
    public String getUserPage(Model model,
                              @RequestParam(defaultValue = "0") Integer pageNo,
                              @RequestParam(defaultValue = "10") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<User> userList = adminService.getUsers(pageable);
        // List<User> users = adminService.getAllUsers();
        model.addAttribute("users", userList);
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
