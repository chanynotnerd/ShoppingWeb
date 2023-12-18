package com.example.shoppingweb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
    // private Integer cartItemId;
    private Integer userId;
    private Integer itemId;
    private Integer amount;
}
