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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

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
        System.out.println(cartItemDTO); // 클라이언트가 보낸 데이터 출력
        User user = userService.findUserById(cartItemDTO.getUserId());
        Item item = itemService.getItem(cartItemDTO.getItemId());
        cartService.insertCart(user, item, cartItemDTO.getAmount());
        return new ResponseDTO<>(HttpStatus.OK.value(), "장바구니에 제품이 담겼습니다.");
    }

    @GetMapping("/cart/add")
    public String AddCart()
    {
        return "item/getItem";
    }


    @GetMapping("/cart/user")
    public String viewCart(HttpSession session, Model model) {
        Integer userId = getLoggedInUserId(session);
        if (userId == null) {
            // 로그인하지 않은 사용자의 경우, 로그인 페이지로 리다이렉트
            return "/";
        }
        model.addAttribute("userId", userId);
        User user = userService.findUserById(userId);
        Cart cart = cartService.getCartByUser(user);
        if (cart == null) {
            // 사용자에게 할당된 Cart가 없는 경우, 새 Cart를 생성
            cart = cartService.insertCart(user, null, 0);
        }
        model.addAttribute("cart", cart);

        int total = 0;
        for (Cart_item cartItem : cart.getCartItems()) {
            total += cartItem.getItem().getPrice() * cartItem.getCount();
        }
        model.addAttribute("total", total);
        return "cart/getCart";
    }
    @DeleteMapping("/cart/item")
    public @ResponseBody ResponseDTO<?> deleteOne(@RequestBody CartItemDTO cartItemDTO){

        cartService.deleteOneCartItem(cartItemDTO.getItemId());
        return new ResponseDTO<>(HttpStatus.OK.value(),
                cartItemDTO.getItemId() + "번 항목 삭제.");
    }

    @PatchMapping("/cart/updateCount")
    public @ResponseBody ResponseDTO<?> updateCartItemCount(@RequestBody CartItemDTO cartItemDTO) {
        System.out.println(cartItemDTO); // 클라이언트가 보낸 데이터 출력
        if (cartItemDTO.getUserId() == null || cartItemDTO.getItemId() == null) {
            Map<String, String> errors = new HashMap<>();
            if (cartItemDTO.getUserId() == null) errors.put("userId", "User ID is missing");
            if (cartItemDTO.getItemId() == null) errors.put("itemId", "Item ID is missing");
            return new ResponseDTO<>(HttpStatus.BAD_REQUEST.value(), errors);
        }
        User user = userService.findUserById(cartItemDTO.getUserId());
        Item item = itemService.getItem(cartItemDTO.getItemId());

        cartService.updateCartItemCount(user, item, cartItemDTO.getAmount());
        return new ResponseDTO<>(HttpStatus.OK.value(), cartItemDTO.getItemId() + "번 항목 수량 변경.");
    }
}