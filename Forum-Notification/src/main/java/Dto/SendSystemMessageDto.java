package Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SendSystemMessageDto
{
    //接收者的用户id
    @NotNull(message = "接收者id不能为空")
    private Long receiverId;

    @NotBlank(message = "消息内容不能为空")
    @Size(max = 255, message = "消息内容长度不能超过255个字符")
    private String message;

    //消息类型，0：文字，1：文件
    @NotNull(message = "消息类型不能为空")
    private Short type;
}
