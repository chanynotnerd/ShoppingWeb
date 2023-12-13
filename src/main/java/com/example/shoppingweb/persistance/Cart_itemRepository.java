package com.example.shoppingweb.persistance;

import com.example.shoppingweb.domain.Cart_item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Cart_itemRepository extends JpaRepository<Cart_item, Integer> {
    Cart_item findByCartIdAndItemId(int cartid, int itemid);
}
