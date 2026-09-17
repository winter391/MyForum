package Dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GetPublishedPostsDto
{
    @NotNull(message = "贴吧id不能为空")
    private Long barId;

    @NotNull(message = "排序方式不能为空")
    @Min(value = 0, message = "排序方式只能是0到3")
    @Max(value = 3, message = "排序方式只能是0到3")
    private Integer sortType;

    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码从1开始")
    private Integer page;

    @NotNull(message = "每页数量不能为空")
    @Min(value = 1, message = "每页数量至少为1")
    @Max(value = 100, message = "每页数量不能超过100")
    private Integer size;
}
