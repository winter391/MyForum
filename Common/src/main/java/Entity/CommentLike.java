package Entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//该实体对应comment_like表，存储了每个用户对每条评论的点赞记录
//由于任何人都不可以删除评论，点赞记录只能插入和删除，评论本体不会被删除
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("comment_like")
public class CommentLike
{
    private Long id;

    //被点赞的评论的id
    private Long commentId;

    //点赞用户的id
    private Long userId;
}
