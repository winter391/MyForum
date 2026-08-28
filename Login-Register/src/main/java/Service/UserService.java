package Service;

import DTO.LoginDto;
import DTO.RegisterDto;
import Entity.User;
import V0.LoginV0;
import com.baomidou.mybatisplus.spring.service.IService;

public interface UserService extends IService<User>
{
    public LoginV0 Login(LoginDto dto);

    public boolean Register(RegisterDto dto);
}
