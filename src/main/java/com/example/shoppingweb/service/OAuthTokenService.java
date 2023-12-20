package com.example.shoppingweb.service;

import com.example.shoppingweb.domain.OAuthToken;
import com.example.shoppingweb.domain.User;
import com.example.shoppingweb.dto.OAuthTokenDTO;
import com.example.shoppingweb.persistance.OAuthTokenRepository;
import com.example.shoppingweb.persistance.UserRepository;
import com.example.shoppingweb.security.encryption.AES;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OAuthTokenService {
    @Autowired
    private OAuthTokenRepository oAuthTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${Secret_Key}")
    private String secretKey;

    public void saveToken(OAuthTokenDTO oAuthTokenDTO) {
        try {
            User user = userRepository.findById(oAuthTokenDTO.getUser_Id()).orElseThrow(() -> new RuntimeException("해당 유저가 없습니다."));
            String encryptedAccessToken = AES.encrypt(oAuthTokenDTO.getAccessToken(), secretKey); // 엑세스토큰 암호화
            String encryptedRefreshToken = AES.encrypt(oAuthTokenDTO.getRefreshToken(), secretKey); // 리프레시토큰 암호화
            OAuthToken newOAuthToken = new OAuthToken(encryptedAccessToken, encryptedRefreshToken, user);
            oAuthTokenRepository.save(newOAuthToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public OAuthTokenDTO getToken(Integer user_Id) {
        try {
            OAuthToken oAuthToken = oAuthTokenRepository.findById(user_Id).orElseThrow(() -> new RuntimeException("해당 토큰이 없습니다."));
            String decryptedAccessToken = AES.decrypt(oAuthToken.getAccessToken(), secretKey); // 복호화
            String decryptedRefreshToken = AES.decrypt(oAuthToken.getRefreshToken(), secretKey); // 복호화
            return new OAuthTokenDTO(decryptedAccessToken, decryptedRefreshToken, user_Id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
