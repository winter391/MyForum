package org.example.clientpc.http;

import lombok.Getter;

/**
 * 后端业务失败（Result.code != 10001）时抛出，携带后端 msg。
 */
@Getter
public class ApiException extends RuntimeException {

    private final int code;

    public ApiException(int code, String msg) {
        super(msg);
        this.code = code;
    }
}
