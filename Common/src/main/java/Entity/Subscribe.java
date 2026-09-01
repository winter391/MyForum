package Entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

@TableName("user_subscriber")
@Data
public class Subscribe
{
    private Long id;

    private Long subscriberId;

    private Long subscribedId;

    public Subscribe(Long subscribedId, Long subscriberId) {
        this.subscribedId = subscribedId;
        this.subscriberId = subscriberId;
    }
}
