package com.august.sina.scraper;

import com.august.sina.dto.SinaArticleItem;
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
import java.util.ArrayList;
import java.util.List;

public class BlogListScraper {

    private static final Logger logger = LoggerFactory.getLogger(BlogListScraper.class);

    /**
     * 使用 HttpClient 发送 GET 请求并获取页面 HTML 内容
     *
     * @param url 目标 URL
     * @return 页面 HTML 字符串
     * @throws IOException 如果请求失败
     */
    public static String fetchHtml(String url,String cookie) throws IOException, InterruptedException {
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
                if (statusCode == 404) {
                    //翻页超过最大页数时
                    return "";
                }
                if (statusCode != 200) {
                    throw new IOException("Failed to fetch page, status code: " + statusCode);
                }

                HttpEntity entity = response.getEntity();
                Thread.sleep(100);
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
    public static List<SinaArticleItem> parseArticles(String html) {
        List<SinaArticleItem> sinaArticleItems = new ArrayList<>();

        // 解析 HTML
        Document doc = Jsoup.parse(html);

        // 选择包含文章的元素
        Elements articleCells = doc.select("div.articleCell.SG_j_linedot1");

        for (Element cell : articleCells) {
            // 找到标题和链接所在的 <a> 标签
            Element linkElement = cell.selectFirst("span.atc_title > a");

            if (linkElement != null) {
                String title = linkElement.text();
                String href = linkElement.attr("href");

                // 确保链接以 https 开头
                String fullUrl;
                if (href.startsWith("//")) {
                    fullUrl = "https:" + href;
                } else if (href.startsWith("http")) {
                    fullUrl = href;
                } else {
                    fullUrl = "https://blog.sina.com.cn" + href;
                }

                String shortPublishTime=null;
                Element shortPublishTimeElement = cell.selectFirst("span.atc_tm");
                if(shortPublishTimeElement!=null){
                    shortPublishTime=shortPublishTimeElement.text();
                }
                sinaArticleItems.add(new SinaArticleItem(title, fullUrl,shortPublishTime));
            }
        }

        return sinaArticleItems;
    }
}
