package com.august.sina.dto;

import com.august.blog.dto.ArticleDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SinaArticleDetail extends ArticleDetail {

    private static final Logger logger = LoggerFactory.getLogger(SinaArticleDetail.class);


    private ArticleType articleType;

    public SinaArticleDetail(String title, String publishTime, String newContentHtml,ArticleType articleType){
        super(title,publishTime,newContentHtml);
        this.articleType=articleType;
    }

    // 生成 HTML 文件并保存
    public void saveArticle(String saveDir) {

        String fileName= (this.getPublishTime()+"_"+this.getTitle()).replace(":",".");
        // 拼接文件的绝对路径
        String filePath = saveDir + File.separator +fileName+".html";

        // HTML模板
        String htmlContent = "<html>\n" +
                "<head><title>" + this.getTitle() + "</title></head>\n" +
                "<body>\n" +
                "<h1 id=\"b_title\">" + this.getTitle()  + "</h1>\n" +
                "<p id=\"b_publish_time\"><strong>发布时间：</strong>" + getPublishTime() + "</p>\n" +
                "<p id=\"b_category_name\"><strong>分类：</strong>" + getCategoryName() + "</p>\n" +
                "<div id=\"b_article_body\">" + getContentHtml() + "</div>\n" +
                "</body>" +
                "</html>";

        // 写入HTML文件
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(htmlContent);
            logger.debug("HTML文件已生成：{}",filePath);

            this.setLocalPath(filePath);

        } catch (IOException e) {
            throw new IllegalStateException("文章保存到本地报错 ",e);
        }
    }

    // 打开本地HTML文件进行预览
    public void priviewHtml() {
        try {
            // 创建文件对象
            File file = new File(getLocalPath());

            // 检查文件是否存在
            if (file.exists()) {
                // 获取桌面环境，调用浏览器打开本地文件
                Desktop desktop = Desktop.getDesktop();
                desktop.open(file); // 打开文件
            } else {
                logger.info("文件不存在：" + getLocalPath());
            }
        } catch (IOException e) {
            logger.error("文章预览报错",e);
        }
    }

    public ArticleType getArticleType() {
        return articleType;
    }

    public static enum ArticleType {

        /**
         * 解析错误的文章
         */
        ErrorParseArticle,
        /**
         * 大字文章
         */
        BigH1Article,
        /**
         * 小字文章
         */
        SmallH2Article;

    }
}
