package Dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UploadImageDto
{
    @NotNull(message = "用户id不能为空")
    private Long userId;

    @NotNull(message = "帖子id不能为空")
    private Long postId;
}
