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

    // 장바구니에 Item 추가
    @PostMapping("/user/{id}/{itemId}")
    public String addCartItem(@PathVariable("id") Integer id, @PathVariable("itemId") Integer itemId, @RequestParam("amount") int amount) {
        User user = userService.findUserById(id);
        Item item = itemService.getItem(itemId);

        cartService.addCart(user, item, amount);

        return "redirect:/item/" + itemId;
    }
    @GetMapping("/user/cart/{id}")
    public String viewCart(@PathVariable("id") Integer id, Model model) {
        User user = userService.findUserById(id);
        Cart cart = cartService.getCartByUser(user);
        model.addAttribute("cart", cart);
        return "cart";
    }
}
