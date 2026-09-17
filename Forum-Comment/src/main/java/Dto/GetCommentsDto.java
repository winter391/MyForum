package Dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GetCommentsDto
{
    @NotNull(message = "帖子id不能为空")
    private Long postId;

    //排序方式，0：按时间，1：按点赞数，具体含义见Code包下的CommentCode
    @NotNull(message = "排序方式不能为空")
    @Min(value = 0, message = "排序方式只能是0或1")
    @Max(value = 1, message = "排序方式只能是0或1")
    private Integer sortType;

    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码从1开始")
    private Integer page;

    @NotNull(message = "每页数量不能为空")
    @Min(value = 1, message = "每页数量至少为1")
    @Max(value = 100, message = "每页数量不能超过100")
    private Integer size;
}
