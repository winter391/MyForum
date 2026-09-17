package Mapper;

import Entity.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface CommentMapper extends BaseMapper<Comment>
{
    //点赞数原子自增（或自减，delta传负数），避免并发点赞时计数丢失
    @Update("update comment set like_count = like_count + #{delta} where id = #{id}")
    public void addLikeCount(@Param("id") Long id, @Param("delta") Integer delta);
}
