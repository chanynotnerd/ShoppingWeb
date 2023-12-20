package com.example.shoppingweb.security.encryption;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Properties;

public class AES {
    private static String secretKey;    // 비밀 키 저장 변수
    private static Key keySpec; // 비밀키 스팩(필요한 세부 정보) 저장 변수

    // 클래스 로드하면 시 한 번만 초기화 수행
    static {
        Properties properties = new Properties();   // 프로퍼티 객체 생성
        try {
            // secretKey.properties 파일 가져옴
            properties.load(AES.class.getClassLoader().getResourceAsStream("secretKey.properties"));
            // SECRET_KEY 값을 가져와서 secretKey 변수에 저장
            secretKey = properties.getProperty("SECRET_KEY");

            byte[] keyBytes = new byte[16]; // 16바이트 배열 생성
            byte[] encodingBytes = secretKey.getBytes(StandardCharsets.UTF_8);  // secretKey를 바이트 배열로 변환

            int arrayLength = encodingBytes.length; // 배열 크기 지정(16바이트) 이거 넘으면 예외 발생
            if (arrayLength > keyBytes.length) {
                arrayLength = keyBytes.length;
            }
            // keyBytes 배열에 arrayLength 크기만큼 encodingBytes 배열 복사.
            System.arraycopy(encodingBytes, 0, keyBytes, 0, arrayLength);
            // 비밀 키 스펙 생성
            keySpec = new SecretKeySpec(keyBytes, "AES");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String encrypt(String encryptToken, String secretKey) throws Exception {
        // AES/CBC/PKCS5Padding 암호화 객체 생성
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        // 암호화 모드로 초기화
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        // 입력된 문자열을 바이트 배열로 변환하여 암호화
        byte[] encrypted = cipher.doFinal(encryptToken.getBytes("UTF-8"));
        // 암호화된 바이트 배열을 Base64 인코딩하여 문자열로 반환
        return new String(Base64.getEncoder().encode(encrypted));
    }

    public static String decrypt(String decryptToken, String secretKey) throws Exception {
        // AES/CBC/PKCS5Padding 암호화 객체 생성
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        // 복호화 모드로 초기화
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        // 입력된 문자열을 바이트 배열로 변환하여 Base64 디코딩
        byte[] byteStr = Base64.getDecoder().decode(decryptToken.getBytes());
        // 디코딩된 바이트 배열을 복호화하여 문자열로 반환
        return new String(cipher.doFinal(byteStr), "UTF-8");
    }
}
