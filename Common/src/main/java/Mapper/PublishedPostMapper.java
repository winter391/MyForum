package Mapper;

import Entity.PublishedPost;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface PublishedPostMapper extends BaseMapper<PublishedPost>
{
    @Update("update post_published set view_count = view_count + 1 where id = #{id}")
    public void addViewCount(@Param("id") Long id);
}
