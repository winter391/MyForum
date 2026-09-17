package Dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SetPostPermissionDto
{
    @NotNull(message = "贴吧id不能为空")
    private Long barId;

    @NotNull(message = "发帖权限不能为空")
    @Min(value = 0, message = "发帖权限只能是0或1")
    @Max(value = 1, message = "发帖权限只能是0或1")
    private Integer postPermission;
}
