package org.example.clientpc.model;

import lombok.Data;

/** 评论（对应后端 Entity.Comment） */
@Data
public class Comment {
    private Long id;
    private String content;
    private Long publisherId;
    private String publisherNickname;
    private Long postId;
    /** 所回复评论的 id，null 表示一级评论 */
    private Long parentId;
    private Integer likeCount;
    private Object createTime;
    private Integer isBanned;
}
