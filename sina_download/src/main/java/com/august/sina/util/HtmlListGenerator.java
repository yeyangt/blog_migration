package com.august.sina.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

public abstract class HtmlListGenerator {

    private static final Logger logger = LoggerFactory.getLogger(HtmlListGenerator.class);

    public static String genListHtml(String articlePath,String errorArticlePath, String listDir) {
        // 定义生成的文章列表HTML文件路径
        String outputHtmlFile = listDir+ File.separator+"article_list.html";

        // 调用方法生成HTML文件
        generateHtmlList(articlePath,errorArticlePath, outputHtmlFile);

        return  outputHtmlFile;
    }

    private static void generateHtmlList(String articlePath,String errorArticlePath, String outputHtmlFile) {
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

        // 开始生成HTML文件
        try (FileWriter writer = new FileWriter(outputHtmlFile)) {
            // 写入HTML文件的头部
            writer.write("<html><head><title>文章列表</title></head><body>\n");
            if(errorFiles.length>0){
                writer.write("<h3>1、正常解析下载了"+files.length+"篇文章</h3>\n");
                writer.write("<h3>2、文章内容未正常解析的有"+errorFiles.length+"篇文章，请后续手工处理</h3>\n");
            }else{
                writer.write("<h3>正常解析下载了"+files.length+"篇文章</h3>\n");
            }
            writer.write("<h1>文章汇总列表</h1>\n");
            writer.write("<ul>\n");

            // 遍历所有HTML文件，生成链接
            for (File file : files) {
                // 获取文件名（去除路径）
                String fileName = file.getName();
                // 生成每个文件的超链接
                String fileLink = "file:///" + Paths.get(file.getAbsolutePath()).toUri().getPath();
                writer.write("<li><a href=\"" + fileLink + "\">" + fileName + "</a></li>\n");
            }

            writer.write("</ul>\n");

            if(errorFiles.length>0){
                writer.write("<h1>未正确解析的文章列表</h1>\n");
                writer.write("<ul>\n");
                for (File file : errorFiles) {
                    // 获取文件名（去除路径）
                    String fileName = file.getName();
                    // 生成每个文件的超链接
                    String fileLink = "file:///" + Paths.get(file.getAbsolutePath()).toUri().getPath();
                    writer.write("<li><a href=\"" + fileLink + "\">" + fileName + "</a></li>\n");
                }
                writer.write("</ul>\n");
            }

            // 写入HTML文件的尾部
            writer.write("</body></html>\n");

            logger.info("文章列表已生成在 " + outputHtmlFile);
        } catch (IOException e) {
           logger.error("生成文件列表错误",e);
        }
    }

    /**
     * 预览文章列表
     * @param listHtmlPath
     */
    public static void priviewHtml(String listHtmlPath) {
        try {
            // 创建文件对象
            File file = new File(listHtmlPath);

            // 检查文件是否存在
            if (file.exists()) {
                // 获取桌面环境，调用浏览器打开本地文件
                Desktop desktop = Desktop.getDesktop();
                desktop.open(file); // 打开文件
            } else {
                logger.info("文件不存在：" + listHtmlPath);
            }
        } catch (IOException e) {
            logger.error("文章预览报错",e);
        }
    }
}
