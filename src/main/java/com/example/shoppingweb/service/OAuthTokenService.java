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
            User user = userRepository.findById(oAuthTokenDTO.getUser_Id()).orElseThrow(() -> new RuntimeException("user not found."));
            String encryptedAccessToken = AES.encrypt(oAuthTokenDTO.getAccessToken(), secretKey); // 엑세스토큰 암호화
            String encryptedRefreshToken = AES.encrypt(oAuthTokenDTO.getRefreshToken(), secretKey); // 리프레시토큰 암호화
            OAuthToken existOAuthToken = oAuthTokenRepository.findByUserId(user.getId());
            // 유저의 OAuthToken이 존재한다면
            if (existOAuthToken != null) {
                // 기존 토큰 값들을 새로운 값으로 set해주고 db에 반영
                existOAuthToken.setAccessToken(encryptedAccessToken);
                existOAuthToken.setRefreshToken(encryptedRefreshToken);
                oAuthTokenRepository.save(existOAuthToken);
            } else {    // 유저의 OAuthToken이 존재하지 않는다면
                // 새 토큰 객체를 만들어 db에 저장
                OAuthToken newOAuthToken = new OAuthToken(encryptedAccessToken, encryptedRefreshToken, user);
                oAuthTokenRepository.save(newOAuthToken);
            }
            /*OAuthToken newOAuthToken = new OAuthToken(encryptedAccessToken, encryptedRefreshToken, user);
            oAuthTokenRepository.save(newOAuthToken);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public OAuthTokenDTO getToken(Integer user_Id) {
        try {
            OAuthToken oAuthToken = oAuthTokenRepository.findById(user_Id).orElseThrow(() -> new RuntimeException("token not found."));
            String decryptedAccessToken = AES.decrypt(oAuthToken.getAccessToken(), secretKey); // 복호화
            String decryptedRefreshToken = AES.decrypt(oAuthToken.getRefreshToken(), secretKey); // 복호화
            return new OAuthTokenDTO(decryptedAccessToken, decryptedRefreshToken, user_Id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
