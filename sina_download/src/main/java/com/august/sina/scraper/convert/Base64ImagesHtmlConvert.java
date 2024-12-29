package com.august.sina.scraper.convert;

import com.august.blog.util.DirUtil;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Base64ImagesHtmlConvert {

    private static final Logger logger = LoggerFactory.getLogger(Base64ImagesHtmlConvert.class);
    private static AtomicInteger imageCounter = new AtomicInteger(0);

    /**
     * 将HTML中的所有<img>标签的src替换为data URI (base64)的方式。
     * 同时将图片数据保存到本地D盘中以便验证下载结果。
     *
     * @param htmlContent 原始HTML内容
     * @return 替换后的HTML内容
     */
    public static String convertImagesToBase64(String htmlContent,String title,String cookie) {
        Document doc = Jsoup.parseBodyFragment(htmlContent);

        //部分图片是A标签包装的，处理一下:<a><img></a>
        Elements aList = doc.select("a");
        for (Element aElement : aList) {
            String href = aElement.attr("href");
            if(isValidImgURL(href)){
                aElement.attr("href", "###");
            }
        }

        Elements imgList = doc.select("img");
        for (Element img : imgList) {

            //隐藏图片跳过
            String style = img.attr("style");
            if(style.equalsIgnoreCase("display:none")){
                img.remove();
                continue;
            }

            String src = img.attr("real_src");
            if (src.isEmpty()) {
                throw new IllegalArgumentException("URL为空"+src);
            }
            if(!isValidImgURL(src)){
                throw new IllegalArgumentException("URL不合法"+src);
            }
//            if(!isValid690URL(src)){
//                throw new IllegalArgumentException("URL非690URL"+src);
//            }
            //这是新浪的防盗链，所以实际URL是另一个，打开Js后跳转到realUrl
            String realUrl=replace690URL(src);
            try {
                byte[] imageData = downloadImage(realUrl,cookie);
                if (imageData != null && imageData.length > 0) {
                    // 简单假设为jpg类型，可根据Content-Type解析更精确的类型
                    String base64 = Base64.getEncoder().encodeToString(imageData);
                    String dataUri = "data:image/jpeg;base64," + base64;

                    // 同时将图片保存到本地
                    String imgPath=saveImageToLocal(imageData,title);

                    String srcNew="file:///" + imgPath;
                    img.attr("src", srcNew);
                }
            } catch (IOException e) {
                logger.error("图片下载报错",e);
            }

            // 删除real_src和abdata属性
            img.removeAttr("real_src");
            img.removeAttr("abdata");

        }

        if(doc.html().contains("sinaimg.cn")){
            throw new IllegalStateException("解析图片未处理干净");
        }

        Element body = doc.body();
        return body.html();
    }

    /**
     * 从给定URL下载图片数据
     *
     * @param imageUrl 图片的URL
     * @return 图片的字节数组
     * @throws IOException 读取失败时抛出异常
     */
    private static byte[] downloadImage(String imageUrl,String cookie) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(imageUrl);
            httpGet.setHeader("User-Agent", "Mozilla/5.0");
            if (cookie != null && !cookie.isEmpty()) {
                httpGet.setHeader("Cookie", cookie);
            }
            httpGet.setHeader("Referer", imageUrl);
            httpGet.setHeader("Origin", extractDomain(imageUrl));
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        return EntityUtils.toByteArray(entity);
                    }
                } else {
                    throw new IOException("Failed to download image, status code: " + statusCode);
                }
            }
        }
        return null;
    }

    /**
     * 将图片字节数据保存到本地D盘，以便验证下载是否正确
     *
     * @param imageData 图片字节数组
     */
    private static String saveImageToLocal(byte[] imageData,String title) {

        // 拼接文件的绝对路径
        String fileName = DirUtil.getImgDir()+ File.separator +"img_"+ imageCounter.incrementAndGet()+"_"+title+".jpg";

        File outFile = new File(fileName);

        try (FileOutputStream fos = new FileOutputStream(outFile)) {
            fos.write(imageData);
            logger.info("Image saved to " + outFile.getAbsolutePath());
        } catch (IOException e) {
            logger.error("图片下载报错",e);
        }
        return fileName;
    }

    private static boolean isValid690URL(String url) {
        // 正则表达式判断URL是否符合指定模式
        String regex = "http://s\\d+\\.sinaimg\\.cn/mw690/[a-zA-Z0-9&]+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);
        return matcher.matches();
    }

    private static boolean isValidImgURL(String url) {
        // 正则表达式判断URL是否符合指定模式
        String regex = "http://s\\d+\\.sinaimg\\.cn/[a-zA-Z0-9]+/[a-zA-Z0-9&]+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);
        return matcher.matches();
    }

    private static String replace690URL(String url) {
        // 使用replaceAll替换内容，动态替换mw690为orignal
        url = url.replace("http", "https");
        // 替换任意的mw690部分为orignal
        url = url.replaceFirst("mw690", "orignal");
        return url;
    }

    private static String extractDomain(String fullUrl) {
        try {
            // 创建 URL 对象
            URL url = new URL(fullUrl);

            // 获取协议和主机部分
            String domain = url.getProtocol() + "://" + url.getHost();

            return domain;
        } catch (MalformedURLException e) {
            logger.warn("解析域名报错",e);
            return fullUrl;
        }
    }


}
