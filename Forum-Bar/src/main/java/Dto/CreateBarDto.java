package Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateBarDto
{
    @NotBlank(message = "贴吧名字不能为空")
    @Size(max = 10, message = "贴吧名字长度不能超过10个字符")
    private String name;

    @Size(max = 200, message = "贴吧简介长度不能超过200个字符")
    private String description;
}
