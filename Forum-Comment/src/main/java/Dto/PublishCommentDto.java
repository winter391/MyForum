package Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PublishCommentDto
{
    @NotNull(message = "帖子id不能为空")
    private Long postId;

    @NotBlank(message = "评论内容不能为空")
    @Size(max = 255, message = "评论内容长度不能超过255个字符")
    private String content;

    //所回复的评论的id，为null时表示发布的是帖子下的一级评论
    private Long parentId;
}
