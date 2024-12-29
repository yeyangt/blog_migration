package com.august.jianshu.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 汇总文章
 */
public  class JianshuArtilceListDetail extends JianshuArticleDetail {

    private static final Logger logger = LoggerFactory.getLogger(JianshuArtilceListDetail.class);

    public JianshuArtilceListDetail(String title, String publishTime, String contentHtml, String categoryName) {
        super(title,publishTime,contentHtml,categoryName);
    }

    private String jianshuAfterUploadContent;


    public String getJianshuAfterUploadContent() {
        return jianshuAfterUploadContent;
    }

    public void setJianshuAfterUploadContent(String jianshuAfterUploadContet) {
        this.jianshuAfterUploadContent = jianshuAfterUploadContet;
    }
}