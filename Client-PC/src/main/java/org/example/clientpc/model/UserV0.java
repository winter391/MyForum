package org.example.clientpc.model;

import lombok.Data;

/** 搜索模块的用户返回值 */
@Data
public class UserV0 {
    private Long id;
    private String nickName;
    private String headImage;
    private String headImageThumb;
}
