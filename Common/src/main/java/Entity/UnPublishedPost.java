package Entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("post_unpublished")
public class UnPublishedPost
{
    private Long id;

    private String title;

    private String content;

    private String publisherNickname;

    private Long publisherId;

    private String barName;

    private Long barId;
}
