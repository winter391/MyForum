package org.example.clientpc.session;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * 当前登录会话。登录成功后填充，退出登录时清空。
 */
@Data
@Component
public class SessionContext {

    /** 用户 id（后端约定 PC 终端 terminal=0） */
    public static final int TERMINAL_PC = 0;

    private Long userId;
    private String nickName;
    private String accessToken;
    private String refreshToken;
    /** accessToken 有效期（秒） */
    private Integer accessTokenExpireTime;
    /** refreshToken 有效期（秒） */
    private Integer refreshTokenExpireTime;

    public boolean isLoggedIn() {
        return accessToken != null && userId != null;
    }

    public void clear() {
        userId = null;
        nickName = null;
        accessToken = null;
        refreshToken = null;
        accessTokenExpireTime = null;
        refreshTokenExpireTime = null;
    }
}
