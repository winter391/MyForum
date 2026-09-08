package Mapper;

import Entity.Subscribe;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SubscribeMapper extends BaseMapper<Subscribe>
{

    @Select("select subscribed_id from user_subscriber where subscriber_id = #{subscriberId} and subscribed_id = #{subscribedId}")
    public Long getSubscribedId(@Param("subscriberId") Long subscriberId,@Param("subscribedId") Long subscribedId);


    //获取关注的用户
    @Select("select subscribed_id from user_subscriber where subscriber_id = #{subscriberId}")
    public List<Long> getSubscribedIds(@Param("subscriberId") Long subscriberId);


    //获取粉丝
    @Select("select subscriber_id from user_subscriber where subscribed_id = #{subscribedId}")
    public List<Long> getSubscriberIds(@Param("subscribedId") Long subscribedId);
}
