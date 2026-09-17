package Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SearchUserByNickNameDto
{
    @NotBlank(message = "昵称关键字不能为空")
    private String nickName;
}
