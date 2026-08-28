package Enums;


import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ResultCode
{
    FAILED(10000,"操作失败"),

    SUCCESS(10001,"操作成功");





    private final Integer code;

    private final String msg;

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
