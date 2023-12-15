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
@Table(name = "ITEM")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;    // 아이템 일련번호

    @Column(nullable = false, length = 100)
    private String itemName;

    @Column(nullable = false)
    private int price;

    @Column
    private Integer discountPercent;

    @Column
    private Integer discountPrice;

    @Enumerated(EnumType.STRING)
    private ItemCategory category;
}
