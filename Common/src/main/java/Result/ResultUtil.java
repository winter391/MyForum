package Result;

import Enums.ResultCode;

public class ResultUtil
{
    public static <T> Result<T> failed()
    {
        return new Result<T>(ResultCode.FAILED.getCode(),ResultCode.FAILED.getMsg(),null);
    }


    public static <T> Result<T> failed(String msg)
    {
        return new Result<T>(ResultCode.FAILED.getCode(),msg,null);
    }

    public static <T> Result<T> success(T data)
    {
        return new Result<T>(ResultCode.SUCCESS.getCode(),ResultCode.SUCCESS.getMsg(),data);
    }

    public static <T> Result<T> success()
    {
        return new Result<T>(ResultCode.SUCCESS.getCode(),ResultCode.SUCCESS.getMsg(),null);
    }

    public static <T> Result<T> error()
    {
        return new Result<T>(ResultCode.ERROR.getCode(),ResultCode.ERROR.getMsg(),null);
    }

    public static <T> Result<T> error(String msg)
    {
        return new Result<T>(ResultCode.ERROR.getCode(),msg,null);
    }


    public static <T> Result<T> sync_error()
    {
        return new Result<T>(ResultCode.SYNC_ERROR.getCode(),ResultCode.SYNC_ERROR.getMsg(),null);
    }
}
