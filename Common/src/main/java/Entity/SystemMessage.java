package Entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("system_message")
public class SystemMessage implements Message
{
    private Long id;

    private Long receiverId;

    private String message;

    private Short type;

    private Integer chatId;
}