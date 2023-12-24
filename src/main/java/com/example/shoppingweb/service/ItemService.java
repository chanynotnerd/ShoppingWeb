package com.example.shoppingweb.service;

import com.example.shoppingweb.domain.Item;
import com.example.shoppingweb.domain.ItemCategory;
import com.example.shoppingweb.persistance.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ItemService {
    @Autowired
    private ItemRepository itemRepository;

    @Transactional(readOnly = true)
    public Item getItem(int id)
    {
        return itemRepository.findById(id).get();
    }

    @Transactional(readOnly = true)
    public Page<Item> getItemListByCategory(String category, Pageable pageable) {
        ItemCategory itemCategory = ItemCategory.valueOf(category.toUpperCase());
        return itemRepository.findByCategory(itemCategory, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Item> getItemList(Pageable pageable)
    {
        return itemRepository.findAll(pageable);
    }

    /*@Transactional(readOnly = true)
    public Page<Item> itemSearchList(String searchKeyword, Pageable pageable) {
        return itemRepository.findByItemNameContaining(searchKeyword, pageable);
    }*/
}
