package com.example.shoppingweb.persistance;

import com.example.shoppingweb.domain.Cart_item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Cart_itemRepository extends JpaRepository<Cart_item, Integer> {
    // Cart_item findByCartIdAndItemId(int cartid, int itemid);
    Cart_item findByCart_IdAndItem_Id(int cartId, int itemId);

    Cart_item findByItem_Id(int itemId);

    Cart_item findByCount(int count);
}
