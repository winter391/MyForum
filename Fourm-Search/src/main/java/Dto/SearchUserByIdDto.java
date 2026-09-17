package Dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SearchUserByIdDto
{
    @NotNull(message = "用户id不能为空")
    private Long userId;
}
