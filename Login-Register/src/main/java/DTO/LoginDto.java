package DTO;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "用户登录用Dto")
@Getter
@Setter
public class LoginDto
{
    @NotEmpty
    @Size(min = 3,max = 15)
    @Schema(description = "用户名")
    private String userName;

    @NotEmpty
    @Size(min = 5,max = 20)
    @Schema(description = "用户密码")
    private String password;
}
