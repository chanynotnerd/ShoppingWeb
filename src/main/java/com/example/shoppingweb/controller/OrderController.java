package com.example.shoppingweb.controller;

import com.example.shoppingweb.domain.Cart;
import com.example.shoppingweb.domain.Order;
import com.example.shoppingweb.domain.User;
import com.example.shoppingweb.security.UserDetailsImpl;
import com.example.shoppingweb.service.CartService;
import com.example.shoppingweb.service.OrderService;
import com.example.shoppingweb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @GetMapping({"", "/"})
    public String getOrderPage(@AuthenticationPrincipal UserDetailsImpl principal, Model model) {
        User user = userService.findUserById(principal.getId());
        System.out.println("username: " + user);

        Cart cart = cartService.getCartByUser(user);
        System.out.println("cart: " + cart);


        if (user == null || user.getUsername() == null) {
            return "redirect:/login";
        }
        model.addAttribute("UserId", user.getId());
        model.addAttribute("userName", user.getUsername());
        model.addAttribute("total", cart.getTotal());

        return "PayInput";
    }

    @PostMapping("/checksum")
    public String getPayChecksum(Principal principal, Model model) {
        User user = userService.getUser(principal.getName());
        return "PayCheckSum";
    }

    @PostMapping("/finish")
    public String getOrderFinish(@AuthenticationPrincipal UserDetailsImpl principal, Model model) {
        User user = userService.findUserById(principal.getId());
        Order order = orderService.createOrder(user);
        model.addAttribute("order", order);
        return "PayReturn";
    }
}
