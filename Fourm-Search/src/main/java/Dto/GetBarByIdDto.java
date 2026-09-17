package Dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GetBarByIdDto
{
    @NotNull(message = "贴吧id不能为空")
    private Long barId;
}
