package Interceptor;


import Code.JwtCode;
import Code.RedisCode;
import Entity.User;
import Entity.UserSession;
import Util.JwtUtil;
import com.alibaba.fastjson2.JSON;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import Exception.GlobalException;

@Component
public class interceptor implements HandlerInterceptor
{
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
       String token = request.getHeader("accessToken");
       if(!JwtUtil.verifyToken(token, JwtCode.accessTokenSecret))
       {
           throw new GlobalException("token失效");
       }
       String Jsonstr = JwtUtil.getInfo(token,JwtCode.accessTokenSecret);
       UserSession user = JSON.parseObject(Jsonstr,UserSession.class);
       Long banned = (Long)redisTemplate.opsForValue().get(String.join(":",RedisCode.user_id_with_banned,user.getId().toString()));
       if(banned!=null)
       {
           throw new GlobalException("用户已被封禁");
       }
       request.setAttribute("session",user);
       return true;
    }
}
