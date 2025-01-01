package com.august.jianshu.service;

import com.august.jianshu.dto.MemberInfoResponse;
import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.nio.charset.StandardCharsets;

public class JianshPublishClient {

    private static final String SET_TIME_URL = "https://www.jianshu.com/author/notes/%s/publish_schedule/set_time";

    private static final String MEMBER_INFO_URL = "https://www.jianshu.com/member_info";

    private static final Gson gson = new Gson();

    public JianshPublishClient(String cookieValue){
        this.cookieValue=cookieValue;
    }
    private final String cookieValue;


    /**
     * 设置发布时间
     *
     * @param scheduleTime 发布计划的时间
     * @return 响应结果
     */
    public String setPublishTime(long articleId,long scheduleTime) {
        // 创建请求体对象
        String requestBody = "{\"schedule_time\":" + scheduleTime + "}";

        // 创建HttpPost对象
        HttpPost httpPost = new HttpPost(String.format(SET_TIME_URL,articleId));

        // 设置请求头
        httpPost.setHeader("accept", "application/json");
        httpPost.setHeader("accept-language", "zh-CN,zh;q=0.9");
        httpPost.setHeader("cache-control", "no-cache");
        httpPost.setHeader("content-type", "application/json; charset=UTF-8");
        httpPost.setHeader("pragma", "no-cache");
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
            StringEntity entity = new StringEntity(requestBody, "UTF-8");
            httpPost.setEntity(entity);

            // 执行请求
            HttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            String result = EntityUtils.toString(response.getEntity(), "UTF-8");

            if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) {
                return result; // 返回成功的响应内容
            } else {
                System.err.println("请求失败，HTTP状态码: " + statusCode);
                System.err.println("响应内容: " + result);
            }
        } catch (Exception e) {
            throw new RuntimeException("设置定时发布失败:", e);
        }

        throw  new IllegalStateException("设置定时发布失败");
    }

    public  MemberInfoResponse getMemberInfo() {
        // 创建HttpGet对象
        HttpGet httpGet = new HttpGet(MEMBER_INFO_URL);

        // 设置请求头
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
        httpGet.setHeader("Referer", "https://www.jianshu.com/vips");
        httpGet.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // 执行请求
            HttpResponse response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            String result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

            if (statusCode == HttpStatus.SC_OK) {
                // 解析JSON响应为MemberInfoResponse对象
                MemberInfoResponse memberInfo = gson.fromJson(result, MemberInfoResponse.class);
                return memberInfo;
            } else {
                System.err.println("请求失败，HTTP状态码: " + statusCode);
                System.err.println("响应内容: " + result);
            }
        } catch (Exception e) {
            throw new IllegalStateException("获取会员信息失败",e);
        }

        throw new IllegalStateException("获取会员信息失败");
    }



}
