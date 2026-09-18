package org.example.clientpc.model;

import lombok.Data;

import java.util.List;

/**
 * 帖子正文 content 的存储格式（对应后端 PostSave）。
 * blocks 顺序即内容顺序：text 块为文字，image 块 content 为图片 URL。
 * width 为客户端自行扩展字段（图片显示宽度，像素），后端解析时忽略未知字段，不影响校验。
 */
@Data
public class PostSave {
    private String title;
    private List<PostSaveBlock> blocks;

    @Data
    public static class PostSaveBlock {
        public static final String TYPE_TEXT = "text";
        public static final String TYPE_IMAGE = "image";

        private String type;
        private String content;
        /** 仅 image 块使用：显示宽度（px） */
        private Integer width;

        public static PostSaveBlock text(String content) {
            PostSaveBlock b = new PostSaveBlock();
            b.type = TYPE_TEXT;
            b.content = content;
            return b;
        }

        public static PostSaveBlock image(String url, Integer width) {
            PostSaveBlock b = new PostSaveBlock();
            b.type = TYPE_IMAGE;
            b.content = url;
            b.width = width;
            return b;
        }
    }
}
