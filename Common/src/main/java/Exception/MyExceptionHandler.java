package Exception;



import Result.Result;
import Result.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
        return ResultUtil.error();
    }

    @ExceptionHandler(GlobalException.class)
    public <T> Result<T> GlobalExceptionHandler(GlobalException e)
    {
        log.error("GlobalExceptionHandler捕获到全局错误：{}",e.toString());
        return ResultUtil.failed(e.getMsg());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public <T> Result<T> MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e)
    {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .filter(m -> m != null)
                .findFirst()
                .orElse("不合法输入");
        log.error("捕捉到不合法输入：{}",e.toString());
        return ResultUtil.error(msg);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public <T> Result<T> HttpMessageNotReadableExceptionHandler(HttpMessageNotReadableException e)
    {
        log.error("来自前端的格式错误：{}",e.toString());
        return ResultUtil.sync_error();

    }
}
