package org.example.clientpc.model;

import lombok.Data;

/** 吧（对应后端 Entity.Bar） */
@Data
public class Bar {
    private Long id;
    private String name;
    private String description;
    private String coverImage;
    private Integer memberCount;
    private Integer postCount;
    private Long masterId;
    private String masterNickname;
    /** 0 正常 1 封禁 */
    private Integer isBanned;
    /** 发帖权限：0 所有人可发帖，1 仅成员可发帖 */
    private Integer postPermission;
}
