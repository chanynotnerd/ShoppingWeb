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
@Table(name = "ITEMS")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;    // 아이템 일련번호

    @Column(nullable = false, length = 100)
    private String itemName;

    @Column(nullable = false, length = 50)
    private String price;

    @Column(length = 50)
    private String discountPercent;

    @Column(length = 50)
    private String discountPrice;

    @Enumerated(EnumType.STRING)
    private ItemCategory category;
}
