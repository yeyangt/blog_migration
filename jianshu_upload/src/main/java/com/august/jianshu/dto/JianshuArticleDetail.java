package com.august.jianshu.dto;

import com.august.blog.dto.ArticleDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 文章详情
 */
public  class JianshuArticleDetail extends ArticleDetail {

    private static final Logger logger = LoggerFactory.getLogger(JianshuArticleDetail.class);

    public JianshuArticleDetail(String title, String publishTime, String contentHtml,String categoryName) {
        super(title,publishTime,contentHtml);
        this.setJianshuTitle("【"+this.getPublishTime().substring(0,10)+"】"+title);
        setCategoryName(categoryName);
    }

    public void setJianshuTitle(String jianshuTitle) {
        this.jianshuTitle = jianshuTitle;
    }

    private String jianshuTitle;


    public String getJianshuTitle() {
        return jianshuTitle;
    }

  


}