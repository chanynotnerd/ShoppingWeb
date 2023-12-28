package com.example.shoppingweb.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity

public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userid")
    private User user;

    private int count;  // 항목에 대한 수(A상품, B상품 총 2개)

    private int total;  // 해당 카트의 총 금액

    @OneToMany(mappedBy = "cart", fetch = FetchType.EAGER)
    private List<Cart_item> cartItems = new ArrayList<>();

    public static Cart createCart(User user) {
        Cart cart = new Cart();
        cart.setCount(0);
        cart.setUser(user);
        return cart;
    }

    public Cart generateCount(int id, Cart cart) {
        List<Cart_item> cartItems = cart.getCartItems();
        cart.setCount(0);
        for (Cart_item cartItem : cartItems) {
            if (id == cartItem.getId()) continue;
            cart.setCount(cart.getCount() + cartItem.getCount());

        }
        return cart;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                ", userId=" + (user != null ? user.getId() : null) +
                ", count=" + count +
                ", total=" + total +
                '}';
    }

}
