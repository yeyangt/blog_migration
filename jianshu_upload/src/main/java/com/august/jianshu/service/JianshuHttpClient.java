package com.august.jianshu.service;

import com.august.jianshu.dto.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class JianshuHttpClient {

    private static final Logger logger = LoggerFactory.getLogger(JianshuHttpClient.class);

    public JianshuHttpClient(String cookieValue){
        this.cookieValue=cookieValue;
    }
    private final String cookieValue;

    private static final String GET_NOTEBOOKS_URL = "https://www.jianshu.com/author/notebooks";

    // 根据 notebookId 获取该分类下所有文章的接口
    private static final String GET_ARTICLES_URL = "https://www.jianshu.com/author/notebooks/%s/notes";


    // 接口URL：新建分类
    private static final String CREATE_NOTEBOOK_URL = "https://www.jianshu.com/author/notebooks";
    // 接口URL：新建文章
    private static final String CREATE_ARTICLE_URL = "https://www.jianshu.com/author/notes";

    // 接口URL：更新文章
    private static final String UPDATE_ARTICLE_URL = "https://www.jianshu.com/author/notes/%s";

    // 此处为你的Cookie，请替换为实际可用值
    // （注意不要在生产环境直接硬编码Cookie，这里仅作演示）

    private final Gson gson = new Gson();

    /**
     * 新建分类
     *
     * @param name 分类名称
     * @return CreateNotebookResponse
     */
    public CreateNotebookResponse createNotebook(String name) {
        CreateNotebookRequest requestBodyObj = new CreateNotebookRequest(name);
        // 将实体类转换为JSON
        String jsonBody = gson.toJson(requestBodyObj);

        // 构建HttpPost
        HttpPost httpPost = new HttpPost(CREATE_NOTEBOOK_URL);

        // 设置请求头
        httpPost.setHeader("accept", "application/json");
        httpPost.setHeader("accept-language", "zh-CN,zh;q=0.9");
        httpPost.setHeader("content-type", "application/json; charset=UTF-8");
        httpPost.setHeader("priority", "u=1, i");
        httpPost.setHeader("sec-ch-ua", "\"Google Chrome\";v=\"131\", \"Chromium\";v=\"131\", \"Not_A Brand\";v=\"24\"");
        httpPost.setHeader("sec-ch-ua-mobile", "?0");
        httpPost.setHeader("sec-ch-ua-platform", "\"Windows\"");
        httpPost.setHeader("sec-fetch-dest", "empty");
        httpPost.setHeader("sec-fetch-mode", "cors");
        httpPost.setHeader("sec-fetch-site", "same-origin");
        httpPost.setHeader("cookie", cookieValue);
        httpPost.setHeader("Referer", "https://www.jianshu.com/writer");
        httpPost.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // 设置请求体
            StringEntity requestEntity = new StringEntity(jsonBody, ContentType.APPLICATION_JSON);
            httpPost.setEntity(requestEntity);

            // 执行请求并解析响应
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getStatusLine().getStatusCode();
                String result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

                if (statusCode == 200 || statusCode == 201) {
                    return gson.fromJson(result, CreateNotebookResponse.class);
                } else {
                    logger.info("创建分类失败，HTTP状态码: " + statusCode);
                    logger.info("响应内容: " + result);
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("创建分类出错:",e);
        }
        throw new IllegalStateException("创建分类出错:");
    }

    /**
     * 新建文章
     *
     * @param notebookId 新建文章所归属的分类ID
     * @param title      文章标题
     * @param atBottom   是否在底部，默认为true即可
     * @return CreateArticleResponse
     */
    public CreateArticleResponse createArticle(long notebookId, String title, boolean atBottom) {
        CreateArticleRequest requestBodyObj = new CreateArticleRequest(notebookId, title, atBottom);
        // 将实体类转换为JSON
        String jsonBody = gson.toJson(requestBodyObj);

        // 构建HttpPost
        HttpPost httpPost = new HttpPost(CREATE_ARTICLE_URL);

        // 设置请求头
        httpPost.setHeader("accept", "application/json");
        httpPost.setHeader("accept-language", "zh-CN,zh;q=0.9");
        httpPost.setHeader("content-type", "application/json; charset=UTF-8");
        httpPost.setHeader("priority", "u=1, i");
        httpPost.setHeader("sec-ch-ua", "\"Google Chrome\";v=\"131\", \"Chromium\";v=\"131\", \"Not_A Brand\";v=\"24\"");
        httpPost.setHeader("sec-ch-ua-mobile", "?0");
        httpPost.setHeader("sec-ch-ua-platform", "\"Windows\"");
        httpPost.setHeader("sec-fetch-dest", "empty");
        httpPost.setHeader("sec-fetch-mode", "cors");
        httpPost.setHeader("sec-fetch-site", "same-origin");
        httpPost.setHeader("cookie", cookieValue);
        httpPost.setHeader("Referer", "https://www.jianshu.com/writer");
        httpPost.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // 设置请求体
            StringEntity requestEntity = new StringEntity(jsonBody, ContentType.APPLICATION_JSON);
            httpPost.setEntity(requestEntity);

            // 执行请求并解析响应
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getStatusLine().getStatusCode();
                String result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

                if (statusCode == 200 || statusCode == 201) {
                    return gson.fromJson(result, CreateArticleResponse.class);
                } else {
                    logger.info("创建文章失败，HTTP状态码: " + statusCode);
                    logger.info("响应内容: " + result);
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("创建文章失败:",e);
        }
        throw new IllegalStateException("创建文章失败:");
    }

    /**
     * 更新文章
     *
     */
    public UpdateArticleResponse updateArticle(long id, String title, int autosaveControl,String content) {
        UpdateArticleRequest requestBodyObj = new UpdateArticleRequest(id, title, autosaveControl,content);
        // 将实体类转换为JSON
        String jsonBody = gson.toJson(requestBodyObj);

        // 构建HttpPost
        HttpPut httpPut = new HttpPut(String.format(UPDATE_ARTICLE_URL,id));

        // 设置请求头
        httpPut.setHeader("accept", "application/json");
        httpPut.setHeader("accept-language", "zh-CN,zh;q=0.9");
        httpPut.setHeader("content-type", "application/json; charset=UTF-8");
        httpPut.setHeader("priority", "u=1, i");
        httpPut.setHeader("sec-ch-ua", "\"Google Chrome\";v=\"131\", \"Chromium\";v=\"131\", \"Not_A Brand\";v=\"24\"");
        httpPut.setHeader("sec-ch-ua-mobile", "?0");
        httpPut.setHeader("sec-ch-ua-platform", "\"Windows\"");
        httpPut.setHeader("sec-fetch-dest", "empty");
        httpPut.setHeader("sec-fetch-mode", "cors");
        httpPut.setHeader("sec-fetch-site", "same-origin");
        httpPut.setHeader("cookie", cookieValue);
        httpPut.setHeader("Referer", "https://www.jianshu.com/writer");
        httpPut.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // 设置请求体
            StringEntity requestEntity = new StringEntity(jsonBody, ContentType.APPLICATION_JSON);
            httpPut.setEntity(requestEntity);

            // 执行请求并解析响应
            try (CloseableHttpResponse response = httpClient.execute(httpPut)) {
                int statusCode = response.getStatusLine().getStatusCode();
                String result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

                if (statusCode == 200 || statusCode == 201) {
                    return gson.fromJson(result, UpdateArticleResponse.class);
                } else {
                    logger.info("更新文章失败，HTTP状态码: " + statusCode);
                    logger.info("响应内容: " + result);
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("更新文章失败:",e);
        }
        throw new IllegalStateException("更新文章失败:");
    }

    /**
     * 调用 GET /author/notebooks 接口，判断是否存在指定名称的分类
     * @return true=已存在；false=不存在
     */
    public List<JianshuCategory> getNotebookItems() {
        // 发送 GET 请求
        HttpGet httpGet = new HttpGet(GET_NOTEBOOKS_URL);
        // 设置请求头
        httpGet.setHeader("accept", "application/json");
        httpGet.setHeader("accept-language", "zh-CN,zh;q=0.9");
        httpGet.setHeader("Cache-Control", "no-cache");
        httpGet.setHeader("Pragma", "no-cache");
        httpGet.setHeader("priority", "u=1, i");
        httpGet.setHeader("sec-ch-ua", "\"Google Chrome\";v=\"131\", \"Chromium\";v=\"131\", \"Not_A Brand\";v=\"24\"");
        httpGet.setHeader("sec-ch-ua-mobile", "?0");
        httpGet.setHeader("sec-ch-ua-platform", "\"Windows\"");
        httpGet.setHeader("sec-fetch-dest", "empty");
        httpGet.setHeader("sec-fetch-mode", "cors");
        httpGet.setHeader("sec-fetch-site", "same-origin");
        httpGet.setHeader("cookie", cookieValue);
        httpGet.setHeader("Referer", "https://www.jianshu.com/writer");
        httpGet.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(httpGet)) {

            int statusCode = response.getStatusLine().getStatusCode();
            String result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

            if (statusCode == 200) {
                // 解析JSON数组
                Gson gson = new Gson();
                Type listType = new TypeToken<List<JianshuCategory>>(){}.getType();
                List<JianshuCategory> notebookList = gson.fromJson(result, listType);
                if(notebookList==null){
                    return new ArrayList<>();
                }
                return notebookList;

            } else {
                logger.error("响应内容: " + result);
                throw new IllegalStateException("获取分类列表失败, HTTP状态码: " + statusCode);
            }
        } catch (Exception e) {
            throw new IllegalStateException("获取分类列表失败: " ,e);
        }
    }

    public  List<JianshuArticleItemResponse> getArticleItemList(Long notebookId) {
        // 执行 GET 请求，解析JSON，并打印文章标题
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(String.format(GET_ARTICLES_URL,notebookId));

            // 设置请求头
            httpGet.setHeader("accept", "application/json");
            httpGet.setHeader("accept-language", "zh-CN,zh;q=0.9");
            httpGet.setHeader("Cache-Control", "no-cache");
            httpGet.setHeader("Pragma", "no-cache");
            httpGet.setHeader("priority", "u=1, i");
            httpGet.setHeader("sec-ch-ua", "\"Google Chrome\";v=\"131\", \"Chromium\";v=\"131\", \"Not_A Brand\";v=\"24\"");
            httpGet.setHeader("sec-ch-ua-mobile", "?0");
            httpGet.setHeader("sec-ch-ua-platform", "\"Windows\"");
            httpGet.setHeader("sec-fetch-dest", "empty");
            httpGet.setHeader("sec-fetch-mode", "cors");
            httpGet.setHeader("sec-fetch-site", "same-origin");
            httpGet.setHeader("cookie", cookieValue);
            httpGet.setHeader("Referer", "https://www.jianshu.com/writer");
            httpGet.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

            // 执行请求
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                int statusCode = response.getStatusLine().getStatusCode();
                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

                // 如果状态码是200则解析JSON
                if (statusCode == 200) {
                    // 解析为列表
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<JianshuArticleItemResponse>>() {}.getType();
                    List<JianshuArticleItemResponse> articles = gson.fromJson(responseBody, listType);
                    if(articles==null){
                        return new ArrayList<>();
                    }
                    return articles;
                } else {
                    // 如果不是200，打印错误信息
                    logger.info("获取文章列表失败，HTTP状态码:{}" , statusCode);
                    logger.info("响应内容: {}" ,responseBody);
                    throw new IllegalStateException("获取文章列表失败, HTTP状态码: " + statusCode);
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("获取文章列表失败, HTTP状态码: " ,e);

        }
    }



}
