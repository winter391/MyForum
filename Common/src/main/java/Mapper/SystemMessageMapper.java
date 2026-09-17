package Mapper;


import Entity.SystemMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SystemMessageMapper extends BaseMapper<SystemMessage>
{
    //查询系统发给某个用户的最大会话内序号，用来在Redis缓存失效后恢复chatId的计数
    @Select("select max(chat_id) from system_message where receiver_id = #{receiverId}")
    public Long getMaxChatId(@Param("receiverId") Long receiverId);
}
