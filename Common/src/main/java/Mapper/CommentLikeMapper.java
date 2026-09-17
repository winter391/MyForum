package Mapper;

import Entity.CommentLike;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface CommentLikeMapper extends BaseMapper<CommentLike>
{
    //查询某个用户对某条评论的点赞记录，用来判断是否重复点赞
    @Select("select * from comment_like where comment_id = #{commentId} and user_id = #{userId}")
    public CommentLike getCommentLike(@Param("commentId") Long commentId, @Param("userId") Long userId);
}
