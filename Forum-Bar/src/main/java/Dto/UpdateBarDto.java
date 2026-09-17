package Dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateBarDto
{
    @NotNull(message = "贴吧id不能为空")
    private Long id;

    @Size(min = 1, max = 12, message = "贴吧名字长度必须在1到12个字符之间")
    private String name;

    @Size(max = 200, message = "贴吧简介长度不能超过200个字符")
    private String description;

    private String coverImage;
}
