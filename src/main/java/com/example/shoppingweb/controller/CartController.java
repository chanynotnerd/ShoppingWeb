package com.example.shoppingweb.controller;

import com.example.shoppingweb.domain.Cart;
import com.example.shoppingweb.domain.Cart_item;
import com.example.shoppingweb.domain.Item;
import com.example.shoppingweb.domain.User;
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

import java.util.Optional;

@Controller
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartService cartService;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;

    // 장바구니에 Item 추가 (POST 요청)
    @PostMapping("/user/{userId}/{itemId}")
    public @ResponseBody ResponseDTO<String> addCartItem(@PathVariable("userId") Integer userId,
                                                         @PathVariable("itemId") Integer itemId,
                                                         @RequestParam("amount") int amount) {

        // 로그 추가
        System.out.println("Received request for addCartItem: userId=" + userId + ", itemId=" + itemId + ", amount=" + amount);

        User user = userService.findUserById(userId);
        Item item = itemService.getItem(itemId);

        cartService.addCart(user, item, amount);

        // ResponseDTO를 사용하여 응답 반환
        return new ResponseDTO<>(HttpStatus.OK.value(), "data add successfully");
    }

    @GetMapping("/user/{userId}/{itemId}")
    public String redirectToItemDetail(@PathVariable("userId") Integer userId,
                                       @PathVariable("itemId") Integer itemId) {
        // 여기서 추가적인 로직을 수행할 수 있음
        return "redirect:/item/" + itemId;
    }

    // 장바구니 보기 (GET 요청)
    @GetMapping("/user/cart/{userId}")
    public String viewCart(@PathVariable("userId") Integer userId, Model model) {
        User user = userService.findUserById(userId);
        Cart cart = cartService.getCartByUser(user);
        model.addAttribute("cart", cart);
        return "cart";
    }
}