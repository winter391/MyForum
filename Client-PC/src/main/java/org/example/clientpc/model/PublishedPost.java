package org.example.clientpc.model;

import lombok.Data;

/**
 * 已发布帖子（对应后端 Entity.PublishedPost，表 post_published）。
 * content 为 PostSave 的 JSON 字符串；createTime 序列化格式不定，用 Object 承接。
 */
@Data
public class PublishedPost {
    private Long id;
    private String title;
    private String content;
    private String publisherNickname;
    private Long publisherId;
    private String barName;
    private Long barId;
    /** 是否置顶（1 置顶） */
    private Integer pin;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Object createTime;
    private Integer isBanned;
}
