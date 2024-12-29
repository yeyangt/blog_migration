package com.august.sina.dto;

import java.util.Objects;

/**
 * 文章列表中的文章条目
 */
public class SinaArticleItem {
    private final String shortTitle;
    private final String shortPublishTime;
    private final String url;
    private String categoryName;

    public SinaArticleItem(String shortTitle, String url, String shortPublishTime) {
        this.shortTitle = shortTitle;
        this.url = url;
        this.shortPublishTime=shortPublishTime;
    }

    public String getShortTitle() {
        return shortTitle;
    }

    public String getShortPublishTime() {
        return shortPublishTime;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
            return "Title: " + shortTitle + "\nURL: " + url;
        }

    @Override
    public boolean equals(Object o) {
        // 检查引用是否相同
        if (this == o) return true;

        // 检查类型和类是否匹配
        if (o == null || getClass() != o.getClass()) return false;

        // 转换为特定的类
        SinaArticleItem that = (SinaArticleItem) o;

        // 比较字段，确保对象相等
        return ((SinaArticleItem) o).getUrl().equals(this.getUrl());
    }

    @Override
    public int hashCode() {
        // 计算字段的hash值，并合并
        return Objects.hash(this.getUrl());
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}