package Dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GetRecommendPostsDto
{
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码从1开始")
    private Integer page;

    @NotNull(message = "每页数量不能为空")
    @Min(value = 1, message = "每页数量至少为1")
    @Max(value = 100, message = "每页数量不能超过100")
    private Integer size;
}
