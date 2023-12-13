package com.example.shoppingweb.controller;

import com.example.shoppingweb.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ItemController {
    @Autowired
    private ItemService itemService;

    @GetMapping("/item/category/{category}")
    public String getItemListByCategory(@PathVariable String category, Model model,
                                        @PageableDefault(size = 8, sort = "id", direction = Sort.Direction.ASC) Pageable pageable)
    {
        model.addAttribute("itemList", itemService.getItemListByCategory(category.toUpperCase(), pageable));
        model.addAttribute("category", category);   // 카테고리별 페이징을 위함.
        return "index";
    }

    @GetMapping({"", "/"})
    public String getItemList(Model model, @PageableDefault(size = 8, sort = "id",
            direction = Sort.Direction.ASC)Pageable pageable)
    {
        model.addAttribute("itemList", itemService.getItemList(pageable));
        return "index";
    }

    @GetMapping("/item/{id}")
    public String getItem(@PathVariable int id, Model model)
    {
        model.addAttribute("item", itemService.getItem(id));
        return "item/getItem";
    }

}
