package Dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateUnPublishedPostDto
{
    @NotNull(message = "更新内容不能为空")
    private UploadUnPublishedPostDto dto;

    @NotNull(message = "目标帖子id不能为空")
    private Long targetId;
}
