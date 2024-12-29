package com.august.sina.scraper;

import com.august.blog.dto.ArticleDetail;
import com.august.sina.dto.SinaArticleDetail;
import com.august.sina.scraper.convert.Base64ImagesHtmlConvert;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 文章详情抓取类
 */
public class ArticleDetailScraper {

    private static final Logger logger = LoggerFactory.getLogger(ArticleDetailScraper.class);


    /**
     * 使用 HttpClient 发送 GET 请求并获取页面 HTML 内容
     *
     * @param url 目标 URL
     * @param cookie 可选的Cookie字符串
     * @return 页面 HTML 字符串
     * @throws IOException 如果请求失败
     */
    public static String fetchHtml(String url, String cookie) throws IOException, InterruptedException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                    "AppleWebKit/537.36 (KHTML, like Gecko) " +
                    "Chrome/58.0.3029.110 Safari/537.3");
            if (cookie != null && !cookie.isEmpty()) {
                httpGet.setHeader("Cookie", cookie);
            }

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    throw new IOException("Failed to fetch page, status code: " + statusCode);
                }

                HttpEntity entity = response.getEntity();
                Thread.sleep(100);
                if (entity != null) {
                    return EntityUtils.toString(entity, "UTF-8");
                } else {
                    throw new IOException("Empty response entity");
                }
            }
        }
    }

    /**
     * 从单篇文章的HTML中解析出标题、发布时间、内容HTML
     *
     * @param shortTitle 目录中的文章标题（如果过长会截断）
     * @param html 文章页面的HTML内容
     * @return 文章详情对象
     */
    public static SinaArticleDetail parseArticleDetail(String shortTitle,String shortPublishTime,String html,String cookie) {

        try{
            Document doc = Jsoup.parse(html);

            // 大标题
            Element titleElement = doc.selectFirst("div.BNE_title h1.h1_tit");
            String title = titleElement != null ? titleElement.text().trim() : "";
            if(StringUtils.isNotBlank(title)){
                return parseBigArticleDetail(doc,cookie);
            }

            // 小标题
            titleElement = doc.selectFirst("div.articalTitle h2.titName");
            title = titleElement != null ? titleElement.text().trim() : "";
            if(StringUtils.isNotBlank(title)){
                return parseSmallArticleDetail(doc,cookie);
            }
            throw new IllegalStateException("遇到了新类型的文章，需要添加解析方案,"+html);
        }catch (Exception e){
            logger.warn("警告！！！！！。解析文章内容[{}]出错，将不再解析文章内容，具体报错信息：",shortTitle,e);
            return new SinaArticleDetail(shortTitle,StringUtils.defaultIfBlank(shortPublishTime,"2099-12-30 12.12.12" ), "解析本篇文章内容出错，因此未获取到文章内容，请再文章上传到简书后，登录简书博客手工将文章修改正确。（PS：目录不需要挪动，因为上传简书时不会遗漏扫error目录）", SinaArticleDetail.ArticleType.ErrorParseArticle);
        }

    }

    /**
     * 新样式大字文章
     */
    private static SinaArticleDetail parseBigArticleDetail(Document doc,String cookie) {

        // 标题
        Element titleElement = doc.selectFirst("div.BNE_title h1.h1_tit");
        String title = titleElement != null ? titleElement.text().trim() : "";

        // 发布时间
        Element timeElement = doc.selectFirst("span#pub_time");
        String publishTime = timeElement != null ? timeElement.text().trim() : "";

        // 内容（HTML）
        // 文章内容在 div.BNE_cont 中，将其内部的HTML作为文章内容
        Element contentElement = doc.selectFirst("div.BNE_cont");
        if(contentElement==null||StringUtils.isEmpty(contentElement.html())){
            throw new IllegalStateException("遇到了新类型的文章，需要添加解析方案,"+title);
        }

        String contentHtml = contentElement.html().trim();
        String newContentHtml=Base64ImagesHtmlConvert.convertImagesToBase64(contentHtml,title,cookie);

        return new SinaArticleDetail(title, publishTime, newContentHtml, SinaArticleDetail.ArticleType.BigH1Article);
    }

    /**
     * 旧样式小字文章
     */
    private static SinaArticleDetail parseSmallArticleDetail(Document doc,String cookie) {

        // 标题
        Element titleElement = doc.selectFirst("div.articalTitle h2.titName");
        String title = titleElement != null ? titleElement.text().trim() : "";

        // 发布时间
        Element timeElement = doc.selectFirst("div.articalTitle span.time");
        String publishTime = timeElement != null ? timeElement.text().trim() : "";
        publishTime=publishTime.replace("(","").replace(")","");
        // 内容（HTML）
        // 文章内容在 div.BNE_cont 中，将其内部的HTML作为文章内容
        Element contentElement = doc.selectFirst("div.articalContent");
        if(contentElement==null||StringUtils.isEmpty(contentElement.html())){
            throw new IllegalStateException("遇到了新类型的文章，需要添加解析方案,"+title);
        }

        String contentHtml =contentElement.html().trim();
        String newContentHtml=Base64ImagesHtmlConvert.convertImagesToBase64(contentHtml,title,cookie);

        return new SinaArticleDetail(title, publishTime, newContentHtml, SinaArticleDetail.ArticleType.SmallH2Article);
    }


}
