package com.example.shoppingweb.dto;

import com.example.shoppingweb.domain.Authority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {
    private int id;
    private String username;
    private String email;
    private String postcode;
    private String address;
    private String detailAddress;
    private Authority authority;
    private String token;
    private String refreshToken;
}