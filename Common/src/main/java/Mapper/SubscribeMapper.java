package Mapper;

import Entity.Subscribe;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SubscribeMapper extends BaseMapper<Subscribe>
{

    @Select("select subscribed_id from user_subscriber where subscriber_id = #{subscriberId} and subscribed_id = #{subscribedId}")
    public Long getSubscribedId(@Param("subscriberId") Long subscriberId,@Param("subscribedId") Long subscribedId);
}
