package com.example.shoppingweb.service;

import com.example.shoppingweb.domain.Cart;
import com.example.shoppingweb.domain.Cart_item;
import com.example.shoppingweb.domain.Item;
import com.example.shoppingweb.domain.User;
import com.example.shoppingweb.persistance.CartRepository;
import com.example.shoppingweb.persistance.Cart_itemRepository;
import com.example.shoppingweb.persistance.ItemRepository;
import com.example.shoppingweb.persistance.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private Cart_itemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;


    @Transactional
    public Cart insertCart(User user, Item newItem, int amount) {
        // 유저 id로 해당 유저의 장바구니 찾기
        Cart cart = cartRepository.findByUserId(user.getId()).orElse(null);

        // 장바구니가 존재하지 않는다면
        if (cart == null) {
            cart = Cart.createCart(user);
            cartRepository.save(cart);
        }

        if (newItem != null) {

            Item item = itemRepository.findById(newItem.getId()).orElse(null);
            Cart_item cartItem = cartItemRepository.findByCart_IdAndItem_Id(cart.getId(), item.getId());

            // 상품이 장바구니에 존재하지 않는다면 카트상품 생성 후 추가
            if (cartItem == null) {
                cartItem = Cart_item.createCartItem(cart, item, amount);
                cartItemRepository.save(cartItem);
            }
            // 상품이 장바구니에 이미 존재한다면 수량만 증가
            else {
                cartItem.addCount(amount);
                cartItemRepository.save(cartItem);
            }
            // 카트 상품 총 개수 증가
            cart.setCount(cart.getCount() + amount);
        }
        cartRepository.save(cart);
        return cart;
    }

    @Transactional(readOnly = true)
    public List<Cart_item> userCartView(Cart cart) {
        List<Cart_item> cart_items = cartItemRepository.findAll();
        List<Cart_item> user_items = new ArrayList<>();

        for (Cart_item cart_item : cart_items) {
            if (cart_item.getCart().getId() == cart.getId()) {
                user_items.add(cart_item);
            }
        }

        return user_items;
    }

    @Transactional(readOnly = true)
    public void deleteOneCartItem(Integer itemId) {
        Cart_item cartItem = cartItemRepository.findById(itemId).orElse(null);
        System.out.printf("응애!!" + cartItem);



        // cartItemRepository.dele teById(itemId);
        if (cartItem != null)
            cartItemRepository.delete(cartItem);
    }

    /* public Cart getCartByUserId(int userId) {
         User user = userRepository.findById(userId)
                 .orElseThrow(() -> new IllegalArgumentException("Invalid userId: " + userId));

         return cartRepository.findByUserId(userId)
                 .orElseThrow(() -> new IllegalArgumentException("No cart found for user: " + userId));
     }*/
    @Transactional(readOnly = true)
    public Cart getCartByUser(User user) {
        return cartRepository.findByUserId(user.getId()).orElse(null);
    }
}
