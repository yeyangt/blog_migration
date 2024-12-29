package com.august.jianshu.service;

import com.august.jianshu.dto.TokenResponse;
import com.august.jianshu.dto.UploadImageResponse;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.methods.*;
import org.apache.http.entity.*;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.*;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public class JianshuImgUploadClient {
    private static final String GET_TOKEN_URL = "https://www.jianshu.com/upload_images/token.json?filename=";
    private static final String UPLOAD_URL = "https://upload.qiniup.com/";

    private final Gson gson = new Gson();
    private final String cookieValue;  // 需要传给简书的 cookie

    public JianshuImgUploadClient(String cookieValue) {
        this.cookieValue = cookieValue;
    }

    /**
     * 上传图片：获取token -> 上传到简书 -> 返回上传结果
     * @param localFilePath 本地文件路径
     */
    public UploadImageResponse uploadImage(String localFilePath) {

        localFilePath=localFilePath.replace("file:///", "");

        String finalFileName=null;
        finalFileName = Paths.get(localFilePath).getFileName().toString();

        TokenResponse tokenResponse = getUploadToken(finalFileName);
        if (tokenResponse == null) {
            throw new IllegalStateException("无法获取简书图片上传token");
        }

        // 拿到 token、key 后，再请求简书的 upload.qiniup.com 进行上传
        return doUploadToQiniu(localFilePath, finalFileName, tokenResponse);
    }

    /**
     * 第一步：调用简书的接口，获取 token、key
     * GET https://www.jianshu.com/upload_images/token.json?filename=xxx
     */
    private TokenResponse getUploadToken(String fileName) {
        String url = GET_TOKEN_URL + fileName;

        HttpGet httpGet = new HttpGet(url);
        // 下面这些 Header 通常要和浏览器抓包的保持一致
        httpGet.setHeader("accept", "application/json");
        httpGet.setHeader("accept-language", "zh-CN,zh;q=0.9");
        httpGet.setHeader("cache-control", "no-cache");
        httpGet.setHeader("pragma", "no-cache");
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
             CloseableHttpResponse response = httpClient.execute(httpGet))
        {
            int statusCode = response.getStatusLine().getStatusCode();
            String result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

            if (statusCode == 200) {
                return gson.fromJson(result, TokenResponse.class);
            } else {
                System.err.println("获取Token失败, HTTP状态码: " + statusCode);
                System.err.println("响应内容: " + result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 第二步：使用 token/key，把图片文件上传到简书
     * POST https://upload.qiniup.com/   (Content-Type: multipart/form-data)
     */
    private UploadImageResponse doUploadToQiniu(String localFilePath, String fileName, TokenResponse tokenResponse) {
        HttpPost httpPost = new HttpPost(UPLOAD_URL);

        // 模拟 form-data 的 multipart请求体
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.RFC6532);
        // 添加 token、key 等普通字段
        builder.addPart("token", new StringBody(tokenResponse.getToken(), ContentType.TEXT_PLAIN));
        builder.addPart("key",   new StringBody(tokenResponse.getKey(),   ContentType.TEXT_PLAIN));
        // 如果需要传 x:protocol=https，参考你的抓包
        builder.addPart("x:protocol", new StringBody("https", ContentType.TEXT_PLAIN));

        // 添加真正的文件字段 - file
        File file = new File(localFilePath);
        if (!file.exists()) {
            throw new IllegalStateException("本地文件不存在: " + localFilePath);
        }
        // name="file"; filename="xxx.png"
        builder.addPart("file", new FileBody(file, ContentType.create("image/png"), fileName));

        HttpEntity multipart = builder.build();
        httpPost.setEntity(multipart);

        // 下面这些 Header 也要尽量与抓包相近
        httpPost.setHeader("accept", "application/json");
        httpPost.setHeader("accept-language", "zh-CN,zh;q=0.9");
        httpPost.setHeader("cache-control", "no-cache");
        httpPost.setHeader("pragma", "no-cache");
        httpPost.setHeader("priority", "u=1, i");
        httpPost.setHeader("sec-ch-ua", "\"Google Chrome\";v=\"131\", \"Chromium\";v=\"131\", \"Not_A Brand\";v=\"24\"");
        httpPost.setHeader("sec-ch-ua-mobile", "?0");
        httpPost.setHeader("sec-ch-ua-platform", "\"Windows\"");
        httpPost.setHeader("sec-fetch-dest", "empty");
        httpPost.setHeader("sec-fetch-mode", "cors");
        httpPost.setHeader("sec-fetch-site", "cross-site");
        httpPost.setHeader("Referer", "https://www.jianshu.com/");
        httpPost.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

        // 注意：简书的这个接口通常不需要 cookie，但如果你想保持一致，可以加上
        // httpPost.setHeader("cookie", cookieValue);

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(httpPost))
        {
            int statusCode = response.getStatusLine().getStatusCode();
            String result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

            if (statusCode == 200) {
                // 简书返回的格式：{"format":"png","height":340,"url":"https://...","width":320}
                return gson.fromJson(result, UploadImageResponse.class);
            } else {
                System.err.println("上传图片失败, HTTP状态码: " + statusCode);
                System.err.println("响应内容: " + result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
