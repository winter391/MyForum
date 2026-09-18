package org.example.clientpc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 后端统一返回体 Result&lt;T&gt;。
 * code=10001 成功；10000 业务失败；500 服务器错误；10010 JSON 格式错误。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;

    public static final int SUCCESS = 10001;
    public static final int FAILED = 10000;
    public static final int ERROR = 500;
    public static final int SYNC_ERROR = 10010;

    public boolean isSuccess() {
        return code != null && code == SUCCESS;
    }
}
