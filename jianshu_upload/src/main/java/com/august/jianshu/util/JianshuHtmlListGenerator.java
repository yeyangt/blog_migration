package com.august.jianshu.util;

import com.august.blog.util.DirUtil;
import com.august.jianshu.dto.JianshuArtilceListDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public abstract class JianshuHtmlListGenerator {

    private static final Logger logger = LoggerFactory.getLogger(JianshuHtmlListGenerator.class);


    public static JianshuArtilceListDetail genHtmlListContet(String articlePath, String errorArticlePath) {
        File folder = new File(articlePath);
        // 获取文件夹中的所有HTML文件
        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".html"));
        if (files == null){
            logger.warn("文件夹中没有HTML文件！");
            files= new File[0];
        }

        File errorFolder = new File(errorArticlePath);
        File[] errorFiles = errorFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".html"));
        if(errorFiles==null){
            errorFiles=new File[0];
        }

        //文章内容
        StringBuilder builder=new StringBuilder();

        String title="blog_migration汇总信息_非正式博文，仅测试";
        String publishTime="2099-12-30 00:00:00";
        String categoryName="blog_migration测试目录";

        // 写入HTML文件的头部
        builder.append("<h1 id=\"b_title\">" + "blog_migration汇总信息，仅用于阅览，如有需要可删除本文"  + "</h1>\n" );
        builder.append( "<p id=\"b_publish_time\"><strong>发布时间：</strong>" + publishTime+ "</p>\n" );
        builder.append("<p id=\"b_category_name\"><strong>分类：</strong>" + categoryName + "</p>\n");

        if(errorFiles.length>0){
            builder.append("<h3>1、准备上传"+files.length+"篇正常的文章</h3>\n");
            builder.append("<h3>2、准备上传"+errorFiles.length+"篇文章内容未正常解析的文章，请后续在简书上手工更新这些文章</h3>\n");
        }else{
            builder.append("<h3>"+files.length+"篇文章</h3>\n");
        }
        builder.append("<h1>文章汇总列表</h1>\n");
        builder.append("<ul>\n");

        // 遍历所有HTML文件，生成链接
        for (File file : files) {
            // 获取文件名（去除路径）
            String fileName = file.getName();
            builder.append("<li>" + fileName + "</li>\n");
        }

        builder.append("</ul>\n");

        if(errorFiles.length>0){
            builder.append("<h1>未正确解析内容的文章列表</h1>\n");
            builder.append("<ul>\n");
            for (File file : errorFiles) {
                // 获取文件名（去除路径）
                String fileName = file.getName();
                builder.append("<li>" + fileName + "</li>\n");
            }
            builder.append("</ul>\n");
        }
        builder.append("<p>------------------</p>");
        String imgHtml="<a href=\"####\" target=\"_blank\"><img style=\"border-image: none; max-width: 100%;\" \n" +
               "src=\"file:///"+DirUtil.getTetImgPath()+"\" alt=\"该图片仅用于测试简书上传图片是否成功\" title=\"\"></a>";

        String content=builder+imgHtml;

        JianshuArtilceListDetail detail= new JianshuArtilceListDetail(title,publishTime,content,categoryName);

        String afterUploadContent=builder.toString().replace("准备上传","已经上传");
        detail.setJianshuAfterUploadContent(afterUploadContent);
        return detail;

    }


}
