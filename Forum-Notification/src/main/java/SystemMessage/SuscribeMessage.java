package SystemMessage;


import lombok.Data;

@Data
public class SuscribeMessage
{
    private Long publisherId;

    private String publisherNickname;

    private Long postId;

    private String postTitle;

    private String barName;
}
