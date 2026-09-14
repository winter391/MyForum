package Dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class getUnPublishedPostDto
{
    @NotNull(message =  "帖子id不得为空")
    private Long id;
}
