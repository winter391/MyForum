package org.example.clientpc.model;

import lombok.Data;

/** GetFollowers / GetSubscribers 的返回元素（用户 id + 昵称） */
@Data
public class IdName {
    private Long id;
    private String nickName;
}
