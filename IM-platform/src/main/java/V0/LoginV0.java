package V0;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "登录用V0")
public class LoginV0
{

    @Schema(description = "用户id")
    private Long id;


    @Schema(description = "用户昵称")
    private String nickName;


    @Schema(description = "准入令牌，用来在登录时验证身份")
    private String accessToken;


    @Schema(description = "准入令牌过期时间，单位:秒")
    private Integer accessTokenExpireTime;


    @Schema(description = "刷新令牌，用来在准入令牌失效之后重新获得准入令牌")
    private String refreshToken;


    @Schema(description = "刷新令牌过期时间，单位:秒")
    private Integer refreshTokenExpireTime;
}
