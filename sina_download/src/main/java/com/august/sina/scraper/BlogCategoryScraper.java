package com.august.sina.scraper;

import com.august.sina.dto.Category;
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

public class BlogCategoryScraper {

    private static final Logger logger = LoggerFactory.getLogger(BlogCategoryScraper.class);


    /**
     * 使用 HttpClient 发送 GET 请求并获取页面 HTML 内容
     *
     * @param url    目标 URL
     * @param cookie 可选的 Cookie
     * @return 页面 HTML 字符串
     * @throws IOException 如果请求失败
     */
    public static String fetchHtml(String url, String cookie) throws IOException {
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
                if (entity != null) {
                    return EntityUtils.toString(entity, "UTF-8");
                } else {
                    throw new IOException("Empty response entity");
                }
            }
        }
    }

    /**
     * 从 HTML 中解析分类链接（不包括 "博文收藏"）
     *
     * @param html 页面 HTML 内容
     * @return 分类列表（每个分类包含名称和链接）
     */
    public static List<Category> parseCategories(String html) {
        List<Category> categories = new ArrayList<>();
        Document doc = Jsoup.parse(html);

        // 找到包含分类列表的区域
        Elements menuLists = doc.select("div.menuList.blog_classList ul li");

        for (Element li : menuLists) {
            // 每个分类链接在 span.SG_dot a 中
            Element linkElement = li.selectFirst("div.menuCell_main span.SG_dot a");
            if (linkElement != null) {
                String name = linkElement.text().trim();
                // 跳过名称为 "博文收藏" 的分类
                if ("博文收藏".equals(name)) {
                    continue;
                }

                String href = linkElement.attr("href");
                String fullUrl;
                if (href.startsWith("//")) {
                    fullUrl = "https:" + href;
                } else if (href.startsWith("http")) {
                    fullUrl = href;
                } else {
                    // 根据情况加上前缀，这里以新浪博客为例，如果href是相对路径可自行调整
                    fullUrl = "https://blog.sina.com.cn" + href;
                }

                categories.add(new Category(name, fullUrl));
            }
        }

        return categories;
    }

}
