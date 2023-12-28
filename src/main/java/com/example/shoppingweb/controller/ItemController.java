package com.example.shoppingweb.controller;

import com.example.shoppingweb.security.UserDetailsImpl;
import com.example.shoppingweb.service.ItemService;
import com.example.shoppingweb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ItemController {
    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @GetMapping("/item/category/{category}")
    public String getItemListByCategory(@PathVariable String category, Model model,
                                        @PageableDefault(size = 8, sort = "id", direction = Sort.Direction.ASC) Pageable pageable)
    {
        /*if (searchKeyword != null) {
            Page<Item> itemList = itemService.getItemList(pageable);
        } else {
            Page<Item> itemList = itemService.itemSearchList(searchKeyword, pageable);
        }*/

        model.addAttribute("itemList", itemService.getItemListByCategory(category.toUpperCase(), pageable));
        model.addAttribute("category", category);   // 카테고리별 페이징을 위함.
        return "th/index";
    }

    @GetMapping({"", "/"})
    public String getItemList(Model model, @PageableDefault(size = 8, sort = "id",
            direction = Sort.Direction.ASC)Pageable pageable)
    {
        model.addAttribute("itemList", itemService.getItemList(pageable));
        return "th/index";
    }

    @GetMapping("/item/{id}")
    public String getItem(@PathVariable int id, Model model, @AuthenticationPrincipal UserDetailsImpl principal) {
        model.addAttribute("item", itemService.getItem(id));    // 해당 정보의 상품 프론트 출력
        model.addAttribute("user", principal);    // 사용자 아이디를 프론트에 넘겨줌, 데이터 주고받기 위함

        return "th/item/getItem";
    }
}
