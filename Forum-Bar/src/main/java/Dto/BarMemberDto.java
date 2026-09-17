package Dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BarMemberDto
{
    @NotNull(message = "贴吧id不能为空")
    private Long barId;

    @NotNull(message = "用户id不能为空")
    private Long userId;
}
