package Exception;


import Enums.ResultCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GlobalException extends RuntimeException
{
    private Integer code;

    private String msg;

    public GlobalException(String Msg)
    {
        code = ResultCode.FAILED.getCode();
        msg = Msg;
    }

}
