package com.august.sina.scraper;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class BlogMainScraper {

    private static final Logger logger = LoggerFactory.getLogger(BlogMainScraper.class);

    /**
     * 使用 HttpClient 发送 GET 请求并获取页面 HTML 内容
     *
     * @param url 目标 URL
     * @return 页面 HTML 字符串
     * @throws IOException 如果请求失败
     */
    public static String fetchHtml(String url,String cookie) throws IOException {
        // 创建一个 CloseableHttpClient 实例
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // 创建一个 HttpGet 请求
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                    "AppleWebKit/537.36 (KHTML, like Gecko) " +
                    "Chrome/58.0.3029.110 Safari/537.3");
            httpGet.setHeader("Cookie",cookie);

            // 发送请求并获取响应
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    throw new IOException("Failed to fetch page, status code: " + statusCode);
                }

                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // 将响应内容转换为字符串
                    return EntityUtils.toString(entity, "UTF-8");
                } else {
                    throw new IOException("Empty response entity");
                }
            }
        }
    }

    /**
     * 使用 Jsoup 解析 HTML 并提取文章标题和链接
     *
     * @param html 页面 HTML 内容
     * @return 文章列表
     */
    public static String parseCategoryUrl(String html) throws IOException {
        // 解析 HTML
        Document doc = Jsoup.parse(html);

        // 选择包含文章的元素，基于用户提供的 HTML 结构
        Element blognavInfo = doc.selectFirst("div.blognavInfo");
        if(blognavInfo==null){
            throw new IOException("未找到导航");
        }
        Elements spanElements=blognavInfo.children();
        for (Element spanElement : spanElements) {
            Element aElement=spanElement.child(0);
            if(aElement.is("a")){
                String aHtml=aElement.html();
                if(aHtml.contains("博文目录")){
                    return "https:"+aElement.attr("href").replace("https:","").replace("http:","");
                }
            }
        }

        throw new IOException("未找到博文目录页面");
    }
}
