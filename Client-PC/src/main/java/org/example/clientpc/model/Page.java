package org.example.clientpc.model;

import lombok.Data;

import java.util.List;

/** MyBatis-Plus 分页返回结构 */
@Data
public class Page<T> {
    private List<T> records;
    private Long total;
    private Long size;
    private Long current;
    private Long pages;
}
