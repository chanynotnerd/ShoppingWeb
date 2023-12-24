package com.example.shoppingweb.persistance;

import com.example.shoppingweb.domain.Item;
import com.example.shoppingweb.domain.ItemCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
    Page<Item> findByCategory(ItemCategory category, Pageable pageable);

    /*Page<Item> findByItemNameContaining(String searchKeyword, Pageable pageable);*/
}
