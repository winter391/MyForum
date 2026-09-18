package org.example.clientpc.model;

import lombok.Data;

/** Login-Register 登录返回值 */
@Data
public class LoginV0 {
    private Long id;
    private String nickName;
    private String accessToken;
    private Integer accessTokenExpireTime;
    private String refreshToken;
    private Integer refreshTokenExpireTime;
}
