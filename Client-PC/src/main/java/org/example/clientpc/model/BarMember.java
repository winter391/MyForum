package org.example.clientpc.model;

import lombok.Data;

/** 吧成员（对应后端 Entity.BarMember）。identity：0 普通成员 1 管理员 2 吧主 */
@Data
public class BarMember {
    private Long id;
    private Long barId;
    private Long userId;
    private Integer identity;
    /** 该用户在该吧内是否被禁言 */
    private Integer isBanned;
}
