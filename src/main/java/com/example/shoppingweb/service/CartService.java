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

/*    // 장바구니에 Item 추가
    @Transactional
    public Cart_item addToCart(User user, Item item, int count) {

    }*/
}
