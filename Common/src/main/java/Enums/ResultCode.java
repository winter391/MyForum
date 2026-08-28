package Enums;


import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ResultCode
{
    FAILED(10000,"操作失败"),

    SUCCESS(10001,"操作成功"),

    ERROR(500,"服务器错误"),

    SYNC_ERROR(10010,"json格式错误");



    private final Integer code;

    private final String msg;

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
