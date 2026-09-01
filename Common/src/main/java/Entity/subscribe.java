package Entity;


import com.baomidou.mybatisplus.annotation.TableName;

@TableName("user_subscriber")
public class subscribe
{
    private Long id;

    private Long subscriberId;

    private Long subscribedId;
}
