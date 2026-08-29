package Mapper;


import Entity.PrivateMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface PrivateMessageMapper extends BaseMapper<PrivateMessage>
{
    @Select("select max(chat_id) from private_message where chat_key = ${chatKey}")
    public Long maxChatId(@Param("chatKey") String chatKey);
}
