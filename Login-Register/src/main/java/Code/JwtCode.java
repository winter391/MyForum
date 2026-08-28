package Code;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class JwtCode
{
    public static String accessTokenSecret;

    public static String refreshTokenSecret;

    //单位均为秒
    public static Integer accessTokenExpireTime;

    public static Integer refreshTokenExpireTime;


    @Value("${Jwt.accessTokenSecret}")
    public void setAccessTokenSecret(String accessTokenSecret) {
        JwtCode.accessTokenSecret = accessTokenSecret;
    }


    @Value("${Jwt.refreshTokenSecret}")
    public void setRefreshTokenSecret(String refreshTokenSecret) {
        JwtCode.refreshTokenSecret = refreshTokenSecret;
    }


    @Value("${Jwt.accessTokenExpireTime}")
    public void setAccessTokenExpireTime(Integer accessTokenExpireTime) {
        JwtCode.accessTokenExpireTime = accessTokenExpireTime;
    }


    @Value("${Jwt.refreshTokenExpireTime}")
    public void setRefreshTokenExpireTime(Integer refreshTokenExpireTime) {
        JwtCode.refreshTokenExpireTime = refreshTokenExpireTime;
    }
}
