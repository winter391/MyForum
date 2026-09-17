package DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenDto
{
    @NotBlank(message = "refreshToken不能为空")
    private String refreshToken;
}
