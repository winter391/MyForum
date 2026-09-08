package DTO;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;



@Data
public class SendStringPrivateMessageDto
{



    @NotNull
    @NotEmpty
    private Long receiverId;



    @NotNull
    @NotEmpty
    private String message;
}
