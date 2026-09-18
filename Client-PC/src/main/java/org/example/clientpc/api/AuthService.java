package org.example.clientpc.api;

import com.fasterxml.jackson.core.type.TypeReference;
import org.example.clientpc.http.ApiClient;
import org.example.clientpc.http.ServiceUrls;
import org.example.clientpc.model.LoginV0;
import org.example.clientpc.model.Result;
import org.example.clientpc.session.SessionContext;
import org.springframework.stereotype.Service;

import java.util.Map;

/** Login-Register（8082）：登录、注册 */
@Service
public class AuthService {

    private final ApiClient api;
    private final ServiceUrls urls;
    private final SessionContext session;

    public AuthService(ApiClient api, ServiceUrls urls, SessionContext session) {
        this.api = api;
        this.urls = urls;
        this.session = session;
    }

    /** 登录成功后填充 SessionContext */
    public void login(String userName, String password) {
        LoginV0 v0 = api.postForData(
                urls.loginRegister() + "/MyForum/login",
                Map.of("userName", userName, "password", password,
                        "terminal", SessionContext.TERMINAL_PC),
                new TypeReference<Result<LoginV0>>() {});
        session.setUserId(v0.getId());
        session.setNickName(v0.getNickName());
        session.setAccessToken(v0.getAccessToken());
        session.setRefreshToken(v0.getRefreshToken());
        session.setAccessTokenExpireTime(v0.getAccessTokenExpireTime());
        session.setRefreshTokenExpireTime(v0.getRefreshTokenExpireTime());
    }

    public void register(String userName, String password, String nickName) {
        api.post(urls.loginRegister() + "/MyForum/register",
                Map.of("userName", userName, "password", password, "nickName", nickName));
    }
}
