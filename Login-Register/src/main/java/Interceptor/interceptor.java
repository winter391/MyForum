package Interceptor;


import Code.JwtCode;
import Entity.User;
import Util.JwtUtil;
import com.alibaba.fastjson2.JSON;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import Exception.GlobalException;


public class interceptor implements HandlerInterceptor
{

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
       String token = request.getHeader("accessToken");
       if(!JwtUtil.verifyToken(token, JwtCode.accessTokenSecret))
       {
           throw new GlobalException("token失效");
       }
       String Jsonstr = JwtUtil.getInfo(token,JwtCode.accessTokenSecret);
       User user = JSON.parseObject(Jsonstr,User.class);
       request.setAttribute("user",user);
       return true;
    }
}
