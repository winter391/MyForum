package Exception;



import Result.Result;
import Result.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
public class MyExceptionHandler
{

    @ExceptionHandler(Exception.class)
    public <T> Result<T> ExceptionHandler(Exception e)
    {
        log.error("ExceptionHandler捕获到全局错误：{}",e.toString());
        return ResultUtil.failed();
    }

    @ExceptionHandler(GlobalException.class)
    public <T> Result<T> GlobalExceptionHandler(GlobalException e)
    {
        log.error("GlobalExceptionHandler捕获到全局错误：{}",e.toString());
        return ResultUtil.failed(e.getMsg());
    }
}
