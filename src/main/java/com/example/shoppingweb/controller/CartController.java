package com.example.shoppingweb.controller;

import com.example.shoppingweb.domain.Cart;
import com.example.shoppingweb.domain.Cart_item;
import com.example.shoppingweb.domain.Item;
import com.example.shoppingweb.domain.User;
import com.example.shoppingweb.dto.CartItemDTO;
import com.example.shoppingweb.dto.ResponseDTO;
import com.example.shoppingweb.service.CartService;
import com.example.shoppingweb.service.ItemService;
import com.example.shoppingweb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
public class CartController {
    @Autowired
    private CartService cartService;
    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    private Integer getLoggedInUserId(HttpSession session) {
        User user = (User) session.getAttribute("principal");
        return user != null ? user.getId() : null;
    }

    @PostMapping("/cart/add")
    public @ResponseBody ResponseDTO<?> AddCart(@RequestBody CartItemDTO cartItemDTO)
    {
        if (cartItemDTO.getUserId() == null || cartItemDTO.getItemId() == null) {
            Map<String, String> errors = new HashMap<>();
            if (cartItemDTO.getUserId() == null) errors.put("userId", "User ID is missing");
            if (cartItemDTO.getItemId() == null) errors.put("itemId", "Item ID is missing");
            return new ResponseDTO<>(HttpStatus.BAD_REQUEST.value(), errors);
        }

        User user = userService.findUserById(cartItemDTO.getUserId());
        Item item = itemService.getItem(cartItemDTO.getItemId());
        cartService.insertCart(user, item, cartItemDTO.getAmount());
        return new ResponseDTO<>(HttpStatus.OK.value(), "success");
    }

    @GetMapping("/cart/add")
    public String AddCart()
    {
        return "item/getItem";
    }

    @GetMapping("/user/{userId}")
    public String getCartByUserId(@PathVariable int userId, Model model) {
        Cart cart = cartService.getCartByUserId(userId);
        model.addAttribute("cart", cart);
        return "cart";
    }

    @GetMapping("/cart/user")
    public String viewCart(HttpSession session, Model model) {
        Integer userId = getLoggedInUserId(session);
        if (userId == null) {
            // 로그인하지 않은 사용자의 경우, 로그인 페이지로 리다이렉트
            return "redirect:/auth/login";
        }

        User user = userService.findUserById(userId);
        Cart cart = cartService.getCartByUser(user);
        if (cart == null) {
            // 사용자에게 할당된 Cart가 없는 경우, 새 Cart를 생성
            cart = Cart.createCart(user);
            cartService.insertCart(user, null, 0);
        }
        model.addAttribute("cart", cart);
        return "cart/getCart";
    }

/*    // 장바구니 보기
    @GetMapping("/cart/user/{userId}")
    public String viewCart(@PathVariable("userId") Integer userId, Model model) {
        User user = userService.findUserById(userId);
        Cart cart = cartService.getCartByUser(user);
        model.addAttribute("cart", cart);
        return "getCart";
    }*/

}