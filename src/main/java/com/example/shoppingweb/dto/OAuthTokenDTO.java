package com.example.shoppingweb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OAuthTokenDTO {
    private String accessToken;
    private String refreshToken;
    private Integer user_Id;
}
