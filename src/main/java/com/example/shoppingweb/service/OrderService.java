package com.example.shoppingweb.service;

import com.example.shoppingweb.domain.*;
import com.example.shoppingweb.persistance.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.NoSuchElementException;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartService cartService;

    @Transactional
    public Order createOrder(User user) {
        Cart cart = cartService.getCartByUser(user);

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(cart.getTotal());
        order.setOrderItems(new ArrayList<>());

        for (Cart_item cartItem : cart.getCartItems()) {
            Order_Item orderItem = new Order_Item();
            orderItem.setOrder(order);
            orderItem.setItem(cartItem.getItem());
            orderItem.setQuantity(cartItem.getCount());
            order.getOrderItems().add(orderItem);
        }

        orderRepository.save(order);
        return order;
    }

    @Transactional(readOnly = true)
    public Order getOrderById(int id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Order null."));
    }

}
