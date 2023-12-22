package com.example.shoppingweb.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "authority")
public class Authority {
    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "authority_name")
    private RoleType authorityName;

    @Column(length = 50)
    private String title;
}
