package com.example.shoppingweb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private int id;

    @NotNull(message = "Username이 전달되지 않았습니다.")
    @NotBlank(message = "Username은 필수 입력 항목입니다.")
    @Size(min = 5, max = 15, message = "Username은 다섯 글자 이상 15자 미만으로 입력하세요.")
    private String username;

    @NotNull(message = "Password 파라미터가 전달되지 않았습니다.")
    @NotBlank(message = "Password는 필수 입력 항목입니다.")
    @Size(min = 6, max = 12, message = "password는 여섯 글자 이상 12자 미만으로 입력하세요.")
    private String password;

    @NotNull(message = "Email이 전달되지 않았습니다.")
    @NotBlank(message = "Email은 필수 입력 항목입니다.")
    @Email(message = "이메일 형식이 아닙니다.")
    private String email;

    @NotNull(message = "우편번호가 전달되지 않았습니다.")
    @NotBlank(message = "우편번호는 필수 입력 항목입니다.")
    @Size(min = 5, max = 5, message = "postcode는 다섯 글자로 입력하세요.")
    private String postcode;

    @NotNull(message = "주소가 전달되지 않았습니다.")
    @NotBlank(message = "주소는 필수 입력 항목입니다.")
    private String address;

    @NotNull(message = "상세주소가 전달되지 않았습니다.")
    @NotBlank(message = "상세주소는 필수 입력 항목입니다.")
    private String detailAddress;
}
