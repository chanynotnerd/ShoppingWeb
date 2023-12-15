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

/*    @PostMapping("/item/addToCart")
    public String addToCart(@RequestBody Cart_item cartItem) {
        User user = userService.findUserById(cartItem.getUserId());
        Item item = itemService.getItem(cartItem.getItemId());

        cartService.addCart(user, item, cartItem.getAmount());

        return "redirect:/item/" + cartItem.getItemId();
    }*/

  /*  @PostMapping("/user/add")
    public ResponseEntity<ResponseDTO<String>> addCartItem(@RequestBody Cart_item cartItem) {
        User user = userService.findUserById(cartItem.getUserId());
        Item item = itemService.getItem(cartItem.getItemId());

        cartService.addCart(user, item, cartItem.getAmount());

        return ResponseEntity.ok(new ResponseDTO<>(HttpStatus.OK.value(), "data add successfully"));
    }*/

/*    @GetMapping("/user/add")
    public String addCartItem()
    {
        return "item/getItem";
    }*/
    /*// 장바구니에 Item 추가
    @PostMapping("/user/{userId}/{itemId}")
    public @ResponseBody ResponseDTO<String> addCartItem(@PathVariable("userId") Integer userId,
                                                         @PathVariable("itemId") Integer itemId,
                                                         @RequestParam("amount") int amount) {

        // 로그 출력
        System.out.println("Received request for addCartItem: userId=" + userId + ", itemId=" + itemId + ", amount=" + amount);

        User user = userService.findUserById(userId);
        Item item = itemService.getItem(itemId);

        cartService.addCart(user, item, amount);

        // ResponseDTO를 사용하여 응답 반환
        return new ResponseDTO<>(HttpStatus.OK.value(), "data add successfully");
    }*/

/*    @GetMapping("/user/add")
    public String redirectToItemDetail(@PathVariable("userId") Integer userId,
                                       @PathVariable("itemId") Integer itemId) {
        return "/item/" + itemId;
    }*/

    // 장바구니 보기
    @GetMapping("/user/cart/{userId}")
    public String viewCart(@PathVariable("userId") Integer userId, Model model) {
        User user = userService.findUserById(userId);
        Cart cart = cartService.getCartByUser(user);
        model.addAttribute("cart", cart);
        return "cart";
    }
}