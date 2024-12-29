package com.august.jianshu.dto;

/**
 * 创建分类的返回
 */
public class CreateNotebookResponse {
    private int id;
    private String name;
    private int seq;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getSeq() {
        return seq;
    }

    @Override
    public String toString() {
        return "CreateNotebookResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", seq=" + seq +
                '}';
    }
}
