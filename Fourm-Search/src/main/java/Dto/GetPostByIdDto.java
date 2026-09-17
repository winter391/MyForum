package Dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GetPostByIdDto
{
    @NotNull(message = "帖子id不能为空")
    private Long postId;
}
