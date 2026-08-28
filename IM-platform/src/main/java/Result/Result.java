package Result;


import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class Result<T>
{
    //状态码
    private Integer code;

    //消息
    private String msg;

    //传递的结果数据
    private T data;
}
