package Mapper;

import Entity.UnPublishedPost;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UnPublishedPostMapper extends BaseMapper<UnPublishedPost>
{
    @Select("select id from post_unpublished where publisher_id = #{id}")
    public List<Long> selectUnPublishedPostIdsByPublisherId(Long id);
}
