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
    private String username;
    private String email;
    private Authority authority;
    private String token;

}