package Dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SetPostPinDto
{
    @NotNull(message = "贴吧id不能为空")
    private Long barId;

    @NotNull(message = "帖子id不能为空")
    private Long postId;

    @NotNull(message = "置顶状态不能为空")
    @Min(value = 0, message = "置顶状态只能是0或1")
    @Max(value = 1, message = "置顶状态只能是0或1")
    private Integer pin;
}
