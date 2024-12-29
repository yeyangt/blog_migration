package com.august.jianshu.dto;

/**
 * 分类
 */
public class JianshuCategory {
    private long id;
    private String name;
    private Integer seq;

    public JianshuCategory(long id, String name, Integer seq) {
        this.id = id;
        this.name = name;
        this.seq = seq;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getSeq() {
        return seq;
    }

    @Override
    public String toString() {
        return "NotebookItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", seq=" + seq +
                '}';
    }
}
