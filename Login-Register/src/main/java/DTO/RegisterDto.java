package DTO;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "用户注册用Dto")
@Getter
@Setter
public class RegisterDto
{
    @Size(min = 3,max = 15)
    @Schema(description = "用户名，长度在3到15之间")
    @NotEmpty
    private String userName;

    @NotEmpty
    @Size(min = 5,max = 20)
    @Schema(description = "用户密码，长度在5到20之间")
    private String password;

    @NotEmpty
    @Size(min = 1,max = 15)
    @Schema(description = "用户昵称，长度在1到15之间")
    private String nickName;

}
