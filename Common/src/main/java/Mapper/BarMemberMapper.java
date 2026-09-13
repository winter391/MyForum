package Mapper;

import Entity.BarMember;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface BarMemberMapper extends BaseMapper<BarMember>
{
    @Select("select * from bar_member where bar_id = #{barId} and user_id = #{userId}")
    public BarMember getBarMember(@Param("barId") Long barId, @Param("userId") Long userId);

    @Select("select * from bar_member where bar_id = #{barId} order by identity desc, id asc")
    public List<BarMember> getBarMembersByBarId(@Param("barId") Long barId);

    @Delete("delete from bar_member where bar_id = #{barId}")
    public void deleteByBarId(@Param("barId") Long barId);
}