package org.example.clientpc.content;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.clientpc.model.PostSave;

import java.util.ArrayList;
import java.util.List;

/**
 * 帖子正文（PostSave blocks JSON）的解析与渲染辅助。
 */
public final class BlocksHtml {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private BlocksHtml() {}

    /** 解析 content JSON，失败或为空时返回空的 PostSave */
    public static PostSave parse(String content) {
        if (content == null || content.isBlank()) {
            return new PostSave();
        }
        try {
            PostSave save = MAPPER.readValue(content, PostSave.class);
            if (save.getBlocks() == null) {
                save.setBlocks(new ArrayList<>());
            }
            return save;
        } catch (Exception e) {
            PostSave save = new PostSave();
            save.setBlocks(new ArrayList<>());
            // 无法解析时把原文当纯文本展示
            save.getBlocks().add(PostSave.PostSaveBlock.text(content));
            return save;
        }
    }

    /** blocks → 用于 WebView 展示的 HTML（文字转义、换行变 br、图片限宽） */
    public static String toHtml(PostSave save) {
        StringBuilder html = new StringBuilder();
        for (PostSave.PostSaveBlock block : save.getBlocks()) {
            if (block == null || block.getType() == null) {
                continue;
            }
            if (PostSave.PostSaveBlock.TYPE_IMAGE.equals(block.getType())
                    && block.getContent() != null && !block.getContent().isBlank()) {
                String width = block.getWidth() != null && block.getWidth() > 0
                        ? " width=\"" + block.getWidth() + "\"" : "";
                html.append("<img src=\"").append(escapeAttr(block.getContent()))
                        .append("\"").append(width).append(">");
            } else if (block.getContent() != null) {
                html.append(escapeText(block.getContent()).replace("\n", "<br>"));
            }
        }
        return html.toString();
    }

    /** 首段文字预览（列表卡片用），无文字时返回 [图片] 占位 */
    public static String textPreview(String content, int maxLen) {
        PostSave save = parse(content);
        for (PostSave.PostSaveBlock block : save.getBlocks()) {
            if (block != null && block.getContent() != null
                    && !PostSave.PostSaveBlock.TYPE_IMAGE.equals(block.getType())
                    && !block.getContent().isBlank()) {
                String text = block.getContent().replace("\n", " ").trim();
                return text.length() > maxLen ? text.substring(0, maxLen) + "…" : text;
            }
        }
        return save.getBlocks().stream()
                .anyMatch(b -> b != null && PostSave.PostSaveBlock.TYPE_IMAGE.equals(b.getType()))
                ? "[图片]" : "";
    }

    /** 统计图片数 */
    public static long imageCount(String content) {
        return parse(content).getBlocks().stream()
                .filter(b -> b != null && PostSave.PostSaveBlock.TYPE_IMAGE.equals(b.getType()))
                .count();
    }

    /** 收集 blocks 中的图片 URL（按出现顺序） */
    public static List<String> imageUrls(String content) {
        List<String> urls = new ArrayList<>();
        for (PostSave.PostSaveBlock block : parse(content).getBlocks()) {
            if (block != null && PostSave.PostSaveBlock.TYPE_IMAGE.equals(block.getType())
                    && block.getContent() != null && !block.getContent().isBlank()) {
                urls.add(block.getContent());
            }
        }
        return urls;
    }

    private static String escapeText(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static String escapeAttr(String s) {
        return escapeText(s).replace("\"", "&quot;");
    }
}
