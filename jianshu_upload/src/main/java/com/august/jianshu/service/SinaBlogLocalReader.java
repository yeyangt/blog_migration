package com.august.jianshu.service;

import com.august.jianshu.dto.JianshuArticleDetail;
import com.august.jianshu.util.PublishTimeUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 从本地读取新浪博客文章
 * （请提前运行SinaBlogSpiderStarter下载文章到本地）
 */
public abstract class SinaBlogLocalReader {

    private static final Logger logger = LoggerFactory.getLogger(SinaBlogLocalReader.class);


    /**
     * 从指定目录中读取所有的 .html 文件，逐一解析，并封装到ArticleDetail对象中
     *
     * @param directoryPath HTML 文件所在目录
     * @param errorPath HTML 错误解析的文章所在目录
     * @return List<ArticleDetail> 封装好的文章列表
     */
    public static List<JianshuArticleDetail> parseLocalHtmlFiles(String directoryPath,String errorPath) {
        List<JianshuArticleDetail> resultList = new ArrayList<>();

        //正常文章
        File dir = new File(directoryPath);
        if (!dir.exists() || !dir.isDirectory()) {
            logger.error("目录不存在或不是文件夹: " + directoryPath);
            return resultList;
        }

        // 获取目录下的所有文件（可根据需要筛选 .html/.htm 等后缀）
        File[] files = dir.listFiles((pathname) -> pathname.isFile() && pathname.getName().endsWith(".html"));
        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("目录下没有找到任何HTML文件:"+directoryPath);
        }

        for (File file : files) {
            JianshuArticleDetail articleDetail = parseSingleHtml(file);
            resultList.add(articleDetail);
        }

        //错误解析的文章
        File errorDir = new File(errorPath);
        File[] errorFiles = errorDir.listFiles((pathname) -> pathname.isFile() && pathname.getName().endsWith(".html"));
        if (errorFiles == null) {
            errorFiles=new File[0];
        }

        for (File file : errorFiles) {
            JianshuArticleDetail articleDetail = parseSingleHtml(file);
            resultList.add(articleDetail);
        }

        resultList.sort(Comparator.comparing(JianshuArticleDetail::getPublishTime));

        return resultList;
    }

    /**
     * 解析单个HTML文件，提取 title / publishTime / categoryName / contentHtml
     *
     * @param htmlFile 单个 HTML 文件
     * @return ArticleDetail 或 null
     */
    private static JianshuArticleDetail parseSingleHtml(File htmlFile) {
        try {
            // 使用 Jsoup 解析本地HTML文件
            Document doc = Jsoup.parse(htmlFile, StandardCharsets.UTF_8.name());

            // 1. 获取文章标题（去除标签，仅保留文本）
            Element titleEle = doc.getElementById("b_title");
            String title = (titleEle != null) ? titleEle.text().trim() : "";

            // 2. 获取发布时间
            //    假设id="b_publish_time"中有类似：“发布时间：2009-11-12 17:11:40”
            //    可以把“发布时间：”替换或截掉
            Element publishTimeEle = doc.getElementById("b_publish_time");
            String publishTime = "";
            if (publishTimeEle != null) {
                publishTime = publishTimeEle.text().trim();
                // 如果存在“发布时间：”这样的前缀，可以去掉
                publishTime = publishTime.replace("发布时间：", "").trim();
                publishTime= PublishTimeUtil.standardizeTime(publishTime);
            }

            // 3. 获取分类
            //    同样，若存在“分类：”这样的前缀，需要去除
            Element categoryEle = doc.getElementById("b_category_name");
            String categoryName = "";
            if (categoryEle != null) {
                categoryName = categoryEle.text().trim();
                categoryName = categoryName.replace("分类：", "").trim();
            }

            // 4. 获取正文HTML
            //    因为要保留正文富文本格式，这里用 .html() 获取内部所有的HTML
            Element contentEle = doc.getElementById("b_article_body");
            String contentHtml = (contentEle != null) ? contentEle.html().trim() : "";
            contentHtml=contentHtml+"<p></p><p></p><p>新注：此文原写于新浪博客，于"+publishTime+"发表。</p>";

            // 组装 ArticleDetail 对象
            return new JianshuArticleDetail(title,publishTime,contentHtml,categoryName);
        } catch (Exception e) {
            throw new IllegalStateException("解析文件出错：" + htmlFile.getAbsolutePath(),e);
        }
    }

    /**
     * 获取文章的分类
     * @param articleDetailList
     * @return
     */
    public static Set<String> collectCategoryName(List<JianshuArticleDetail> articleDetailList) {
        Set<String> nameSet=new HashSet<>();
        for (JianshuArticleDetail articleDetail : articleDetailList) {
            nameSet.add(articleDetail.getCategoryName());
        }
        nameSet.add("blog_migration测试目录");
        return nameSet;
    }
}
