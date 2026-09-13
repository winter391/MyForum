package Mapper;

import Entity.Bar;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface BarMapper extends BaseMapper<Bar>
{
    @Select("select id from bar where name = #{name}")
    public Long getBarIdByName(String name);

    @Update("update bar set member_count = member_count + #{delta} where id = #{id}")
    public void updateMemberCount(@Param("id") Long id, @Param("delta") Integer delta);
}