package com.example.shoppingweb.dto;

import com.example.shoppingweb.domain.ItemCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDTO {
    @NotNull(message = "아이템 이름이 전달되지 않았습니다.")
    @NotBlank(message = "아이템 이름은 필수 입력 항목입니다.")
    private String itemName;

    @NotNull(message = "아이템 가격이 전달되지 않았습니다.")
    @NotBlank(message = "아이템 가격은 필수 입력 항목입니다.")
    private int price;

    @NotNull(message = "제품 카테고리가 전달되지 않았습니다.")
    @NotBlank(message = "카테고리는 필수 입력 항목입니다.")
    private ItemCategory category;
}
