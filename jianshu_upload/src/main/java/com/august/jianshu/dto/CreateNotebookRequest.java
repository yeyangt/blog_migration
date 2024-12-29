package com.august.jianshu.dto;

/**
 * 创建分类
 */
public class CreateNotebookRequest {
    private String name;

    public CreateNotebookRequest() {
    }

    public CreateNotebookRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
