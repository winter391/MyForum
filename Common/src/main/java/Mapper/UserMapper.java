package Mapper;

import Entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User>
{

    @Select("select id from user_subscriber where id = ${id}")
    public Long getSubscriberId(Long id);
}
