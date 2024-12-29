package com.august.blog.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 文章详情
 */
public  class ArticleDetail {

    private static final Logger logger = LoggerFactory.getLogger(ArticleDetail.class);


    private String title;
    private String publishTime;
    private String contentHtml;
    private String categoryName;

    /**
     * 本地存储位置
     */
    private String localPath;

    public ArticleDetail(String title, String publishTime, String contentHtml) {
        this.title = title;
        this.publishTime = publishTime;
        this.contentHtml = contentHtml;
    }


    public String getTitle() {
        return title;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public String getContentHtml() {
        return contentHtml;
    }

    @Override
    public String toString() {
        return "ArticleDetail{" +
                "title='" + title + '\'' +
                ", publishTime='" + publishTime + '\'' +
                ", contentHtml='" + contentHtml + '\'' +
                '}';
    }


    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}