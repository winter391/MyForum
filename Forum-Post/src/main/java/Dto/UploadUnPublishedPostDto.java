package Dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UploadUnPublishedPostDto
{

    @NotNull(message = "帖子标题不得为空")
    @Size(min = 1,max = 20)
    private String title;

    @Size(min = 1)
    @NotNull(message = "帖子内容不得为空")
    private String content;

    @NotNull(message = "贴吧名称不得为空")
    @Size(min = 1,max =12)
    private String barName;

    @NotNull(message = "贴吧id不得为空")
    private Long barId;
}
