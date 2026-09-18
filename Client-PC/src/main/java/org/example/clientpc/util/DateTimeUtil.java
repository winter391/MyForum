package org.example.clientpc.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 后端日期字段的序列化格式不统一（毫秒时间戳或字符串），统一在此解析格式化。
 */
public final class DateTimeUtil {

    private static final DateTimeFormatter OUT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private DateTimeUtil() {}

    public static String format(Object date) {
        if (date == null) {
            return "";
        }
        if (date instanceof Number n) {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(n.longValue()), ZoneId.systemDefault())
                    .format(OUT);
        }
        String s = date.toString().trim();
        if (s.isEmpty()) {
            return "";
        }
        // 纯数字字符串按毫秒时间戳处理
        if (s.chars().allMatch(Character::isDigit)) {
            try {
                return format(Long.parseLong(s));
            } catch (NumberFormatException ignored) {
            }
        }
        // ISO 格式 "2026-09-18T12:00:00..."
        if (s.contains("T")) {
            try {
                return LocalDateTime.parse(s.substring(0, s.lastIndexOf(':') >= 0
                                ? Math.min(s.length(), 19) : s.length()))
                        .format(OUT);
            } catch (Exception ignored) {
            }
        }
        // "yyyy-MM-dd HH:mm:ss" 直接截断秒
        if (s.length() >= 16 && s.charAt(10) == ' ') {
            return s.substring(0, 16);
        }
        return s;
    }
}
