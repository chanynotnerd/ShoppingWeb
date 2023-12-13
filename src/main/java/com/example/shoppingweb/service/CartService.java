package com.example.shoppingweb.service;

import com.example.shoppingweb.domain.Cart;
import com.example.shoppingweb.domain.Cart_item;
import com.example.shoppingweb.domain.Item;
import com.example.shoppingweb.domain.User;
import com.example.shoppingweb.persistance.CartRepository;
import com.example.shoppingweb.persistance.Cart_itemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private Cart_itemRepository cartItemRepository;

    @Transactional
    public Cart createCart(User user) {
        Cart cart = Cart.builder()
                .user(user)
                .count(0)
                .build();
        return cartRepository.save(cart);
    }

    // 장바구니에 Item 추가
    @Transactional
    public Cart_item addToCart(Cart cart, Item item, int count) {
        Cart_item cartItem = Cart_item.builder()
                .cart(cart)
                .item(item)
                .count(count)
                .build();
        cart.setCount(cart.getCount() + count);  // 카트의 상품 개수 업데이트
        return cartItemRepository.save(cartItem);
    }

    // 장바구니 조회
    @Transactional(readOnly = true)
    public Optional<Cart> getCart(User user) {
        return cartRepository.findByUserId(user.getId());
    }

    // 장바구니 Item 삭제
    @Transactional
    public void removeItemFromCart(Cart cart, Item item) {
        Cart_item cartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), item.getId());
        if (cartItem != null) {
            cart.setCount(cart.getCount() - cartItem.getCount());  // 카트의 상품 개수 업데이트
            cartItemRepository.delete(cartItem);
        }
    }

}
