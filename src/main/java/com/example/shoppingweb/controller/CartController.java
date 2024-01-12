package com.example.shoppingweb.controller;

import com.example.shoppingweb.domain.Cart;
import com.example.shoppingweb.domain.Cart_item;
import com.example.shoppingweb.domain.Item;
import com.example.shoppingweb.domain.User;
import com.example.shoppingweb.dto.CartItemDTO;
import com.example.shoppingweb.dto.ResponseDTO;
import com.example.shoppingweb.security.UserDetailsImpl;
import com.example.shoppingweb.service.CartService;
import com.example.shoppingweb.service.ItemService;
import com.example.shoppingweb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
        try {
            User user = userService.findUserById(cartItemDTO.getUserId());
            Item item = itemService.getItem(cartItemDTO.getItemId());
            cartService.insertCart(user, item, cartItemDTO.getAmount());
        } catch (Exception e) {
            Map<String, String> errors = new HashMap<>();
            errors.put("serverError", e.getMessage());
            return new ResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), errors);
        }
        return new ResponseDTO<>(HttpStatus.OK.value(), "장바구니에 제품이 담겼습니다.");
        /*User user = userService.findUserById(cartItemDTO.getUserId());
        Item item = itemService.getItem(cartItemDTO.getItemId());
        cartService.insertCart(user, item, cartItemDTO.getAmount());
        return new ResponseDTO<>(HttpStatus.OK.value(), "장바구니에 제품이 담겼습니다.");*/
    }

    @GetMapping("/cart/add")
    public String AddCart()
    {
        return "th/item/getItem";
    }


    @GetMapping("/cart/user")
    public String viewCart(@AuthenticationPrincipal UserDetailsImpl principal, Model model) {

        User user = userService.findUserById(principal.getId());  // userDetails에 있는 사용자 아이디를 DB에서 가져옴
        Cart cart = cartService.getCartByUser(user);    // 사용자의 장바구니를 가져옴.
        if (cart == null) {
            // 사용자에게 할당된 Cart가 없는 경우, 새 Cart를 생성
            cart = cartService.insertCart(user, null, 0);
        }
        model.addAttribute("cart", cart);   // 가져온 장바구니 프론트에 출력
        model.addAttribute("userId", user.getId()); // 사용자 아이디를 프론트에 넘겨줌, 데이터 주고받기 위함

        // 장바구니 내 수량과 상품의 가격을 적용한 총 금액 출력
        int total = 0;
        for (Cart_item cartItem : cart.getCartItems()) {
            // total += cartItem.getItem().getPrice() * cartItem.getCount();
            Item item = cartItem.getItem();
            int price = item.getPrice();
            if (item.getDiscountPercent() != null) {
                // 할인율이 적용된 가격 계산
                int discountPrice = (int) (price - (price * (item.getDiscountPercent() / 100.0)));
                total += discountPrice * cartItem.getCount();
            } else {
                // 할인이 없는 경우 기본 가격 사용
                total += price * cartItem.getCount();
            }
        }
        model.addAttribute("total", total); // 총 금액 프론트에 출력
        return "th/cart/getCart";
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