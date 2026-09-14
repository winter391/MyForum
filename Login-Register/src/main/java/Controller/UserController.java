package Controller;


import DTO.LoginDto;
import DTO.RefreshTokenDto;
import DTO.RegisterDto;
import Result.Result;
import Result.ResultUtil;
import Service.UserService;
import V0.LoginV0;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/MyForum")
public class UserController
{
    @Autowired
    private UserService userService;



    @PostMapping("/login")
    public Result<LoginV0> Login(@RequestBody @Valid LoginDto dto)
    {
        LoginV0 V0 = userService.Login(dto);
        return ResultUtil.success(V0);
    }

    @PostMapping("/refreshToken")
    public Result<LoginV0> RefreshToken(@RequestBody @Valid RefreshTokenDto dto)
    {
        return ResultUtil.success(userService.RefreshToken(dto));
    }

    @PostMapping("/register")
    public Result<?> Register(@RequestBody @Valid RegisterDto dto)
    {
        userService.Register(dto);
        return ResultUtil.success();
    }
}