package Service.Impl;

import Code.JwtCode;
import DTO.LoginDto;
import DTO.RefreshTokenDto;
import DTO.RegisterDto;
import Entity.User;
import Entity.UserSession;
import Mapper.UserMapper;
import Service.UserService;
import Util.CopyProperties;
import Util.JwtUtil;
import V0.LoginV0;
import Exception.GlobalException;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService
{
    @Override
    public LoginV0 Login(LoginDto dto)
    {
        User user = getOne(dto.getUserName());
        if(user==null||!user.getPassword().equals(dto.getPassword()))
        {
            throw new GlobalException("用户名或密码错误");
        }
        if(user.getIsBanned())
        {
            throw new GlobalException("该用户已被封禁");
        }
        UserSession session = CopyProperties.copyProperties(user,UserSession.class);
        session.setTerminal(dto.getTerminal());
        String accessToken = JwtUtil.createToken(user.getId(), JSON.toJSONString(session), JwtCode.accessTokenSecret,JwtCode.accessTokenExpireTime);
        String refreshToken = JwtUtil.createToken(user.getId(), JSON.toJSONString(session), JwtCode.refreshTokenSecret,JwtCode.refreshTokenExpireTime);
        LoginV0 V0 = new LoginV0();
        V0.setId(user.getId());
        V0.setNickName(user.getNickName());
        V0.setAccessToken(accessToken);
        V0.setAccessTokenExpireTime(JwtCode.accessTokenExpireTime);
        V0.setRefreshToken(refreshToken);
        V0.setRefreshTokenExpireTime(JwtCode.refreshTokenExpireTime);
        return V0;
    }

    @Override
    public LoginV0 RefreshToken(RefreshTokenDto dto)
    {
        if(!JwtUtil.verifyToken(dto.getRefreshToken(), JwtCode.refreshTokenSecret))
        {
            throw new GlobalException("refreshToken失效，请重新登录");
        }
        UserSession oldSession = JSON.parseObject(JwtUtil.getInfo(dto.getRefreshToken(),JwtCode.refreshTokenSecret),UserSession.class);
        User user = getById(oldSession.getId());
        if(user==null)
        {
            throw new GlobalException("该用户不存在");
        }
        if(user.getIsBanned())
        {
            throw new GlobalException("该用户已被封禁");
        }
        UserSession session = CopyProperties.copyProperties(user,UserSession.class);
        session.setTerminal(oldSession.getTerminal());
        String accessToken = JwtUtil.createToken(user.getId(), JSON.toJSONString(session), JwtCode.accessTokenSecret,JwtCode.accessTokenExpireTime);
        LoginV0 V0 = new LoginV0();
        V0.setId(user.getId());
        V0.setNickName(user.getNickName());
        V0.setAccessToken(accessToken);
        V0.setAccessTokenExpireTime(JwtCode.accessTokenExpireTime);
        return V0;
    }

    @Override
    public boolean Register(RegisterDto dto)
    {
        User user = getOne(dto.getUserName());
        if(user!=null)
        {
            throw new GlobalException("该用户名用户已存在");
        }
        User newUser = new User();
        newUser.setPassword(dto.getPassword());
        newUser.setNickName(dto.getNickName());
        newUser.setUserName(dto.getUserName());
        save(newUser);
        return true;
    }

    private User getOne(String userName)
    {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper = wrapper.eq(User::getUserName,userName);
        return getOne(wrapper);
    }
}
