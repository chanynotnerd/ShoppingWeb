package com.example.shoppingweb.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Cart_item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cartid")
    private Cart cart;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "itemid")
    private Item item;

    private int count;  // 한 항목에 대한 총 수량(A항목 2개, B항목 3개면 총 5개)

    public static Cart_item createCartItem(Cart cart, Item item, int amount) {
        Cart_item cartItem = new Cart_item();
        cartItem.setCart(cart);
        cartItem.setItem(item);
        cartItem.setCount(amount);
        return cartItem;
    }

    // 이미 담겨있는 물건 또 담을 경우 수량 증가
    public void addCount(int count) {
        this.count += count;
    }

    // userId를 반환하는 메소드
    public Integer getUserId() {
        return this.cart != null && this.cart.getUser() != null ? this.cart.getUser().getId() : null;
    }

    // itemId를 반환하는 메소드
    public Integer getItemId() {
        return this.item != null ? this.item.getId() : null;
    }

    public int getAmount() {
        return this.count;
    }

}
