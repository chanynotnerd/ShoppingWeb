package com.example.shoppingweb.controller;

import com.example.shoppingweb.domain.Cart;
import com.example.shoppingweb.domain.Cart_item;
import com.example.shoppingweb.domain.Item;
import com.example.shoppingweb.domain.User;
import com.example.shoppingweb.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/cart")
public class CartController {
    @Autowired
    public CartService cartService;

    // 장바구니 생성
    @PostMapping("/create")
    public Cart createCart(@RequestBody User user) {
        return cartService.createCart(user);
    }

    // 장바구니에 Item 추가
    @PostMapping("/add")
    public Cart_item addToCart(@RequestBody Cart cart, @RequestBody Item item, @RequestParam int count) {
        return cartService.addToCart(cart, item, count);
    }

    // 장바구니 조회
    @GetMapping("/get")
    public Optional<Cart> getCart(@RequestBody User user) {
        return cartService.getCart(user);
    }

    // 장바구니 Item 삭제
    @DeleteMapping("/remove")
    public void removeItemFromCart(@RequestBody Cart cart, @RequestBody Item item) {
        cartService.removeItemFromCart(cart, item);
    }
}
