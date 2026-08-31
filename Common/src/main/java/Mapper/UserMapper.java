package Mapper;

import Entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User>
{

    @Select("select id from user_subscriber where subscriber_id = #{subscriber_id} and subscribed_id = #{subscribed_id}")
    public Long getSubscriberId(@Param("subscriber_id") Long subscriber_id,@Param("subscribed_id") Long subscribed_id);
}
