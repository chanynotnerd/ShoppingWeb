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
                cart.getCartItems().add(cartItem);
            }
            // 상품이 장바구니에 이미 존재한다면 수량만 증가
            else {
                cartItem.addCount(amount);
                cartItemRepository.save(cartItem);
            }
            cart = cart.generateCount(-1, cart);
            cartRepository.saveAndFlush(cart);
        }
        cart.setTotal(calculateTotal(cart));
        cartRepository.save(cart);
        return cart;
    }


    private int calculateTotal(Cart cart) {
        int total = 0;
        for (Cart_item cartItem : cart.getCartItems()) {
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
        return total;
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

    @Transactional
    public void deleteOneCartItem(int id) {
        System.out.println("Deleting item with ID: " + id);
        Cart_item cartItem = cartItemRepository.findById(id).get();
        Cart cart = cartItem.getCart();
        cartItemRepository.delete(cartItem);
        cart = cart.generateCount(id, cart);
        cartRepository.save(cart);


        System.out.println("Item deleted successfully.");
    }

    @Transactional
    public Cart updateCartItemCount(User user, Item newItem, int amount) {
        // System.out.println("Updating item with ID: " + id);
        // 유저 id로 해당 유저의 장바구니 찾기
        Cart cart = cartRepository.findByUserId(user.getId()).orElse(null);
        Item item = itemRepository.findById(newItem.getId()).orElse(null);

        Cart_item cartItem = cartItemRepository.findByCart_IdAndItem_Id(cart.getId(), item.getId());

        cartItem.setCount(amount);
        cart = cart.generateCount(user.getId(), cart);
        cart.setTotal(calculateTotal(cart));
        cartItemRepository.save(cartItem);
        return cart;
    }

    @Transactional
    public void clearCart(User user) {
        Cart cart = cartRepository.findByUserId(user.getId()).orElse(null);
        if (cart != null) {
            List<Cart_item> cartItems = new ArrayList<>(cart.getCartItems());
            for (Cart_item cartItem : cartItems) {
                cartItemRepository.delete(cartItem);
            }
            cart.getCartItems().clear();
            cart.generateCount(0, cart);
            cartRepository.save(cart);
        }
    }

    @Transactional(readOnly = true)
    public Cart getCartByUser(User user) {
        return cartRepository.findByUserId(user.getId()).orElse(null);
    }
}
