package Dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LikeCommentDto
{
    @NotNull(message = "评论id不能为空")
    private Long commentId;
}
