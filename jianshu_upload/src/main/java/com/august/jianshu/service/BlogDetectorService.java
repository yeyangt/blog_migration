package com.august.jianshu.service;

import com.august.jianshu.dto.*;
import com.august.jianshu.util.ArticleTimingPublishUtil;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.august.jianshu.util.ArticleTimingPublishUtil.formattedTimestamp;

public class BlogDetectorService {

    private static final Logger logger = LoggerFactory.getLogger(BlogDetectorService.class);

    /**
     * 简书文章上传
     */
    private final JianshuHttpClient jianshuHttpClient;

    /**
     * 简书图片上传
     */
    private final JianshuImgUploadClient jianshuImgUploadClient;

    /**
     * 定时发布
     */
    private final JianshPublishClient jianshPublishClient;

    public BlogDetectorService(String cookieValue){
        this.jianshuHttpClient =new JianshuHttpClient(cookieValue);
        this.jianshuImgUploadClient =new JianshuImgUploadClient(cookieValue);
        this.jianshPublishClient=new JianshPublishClient(cookieValue);
    }


    /**
     * 新建分类
     * @param categoryNameSet
     * @return
     */
    public List<JianshuCategory> createCategoryIfNotExits(Set<String> categoryNameSet) {
        List<JianshuCategory> jianshuCategoryList = jianshuHttpClient.getNotebookItems();
        for (String caName : categoryNameSet) {
            JianshuCategory item=acquireCategoryName(caName, jianshuCategoryList);
            if(item==null){
                CreateNotebookResponse notebookResp = jianshuHttpClient.createNotebook(caName);
                logger.info("新建分类成功：" + notebookResp);
                item=new JianshuCategory(notebookResp.getId(),notebookResp.getName(),notebookResp.getSeq());
                jianshuCategoryList.add(item);
            }
        }
        return jianshuCategoryList;
    }

    private static JianshuCategory acquireCategoryName(String categoryName, List<JianshuCategory> jianshuCategoryList) {
        for (JianshuCategory jianshuCategory : jianshuCategoryList) {
            if(categoryName.equals(jianshuCategory.getName())){
                return jianshuCategory;
            }
        }
        return null;
    }

    /**
     * 将文章都标记上分类
     */
    public  Map<JianshuArticleDetail, JianshuCategory> relationNoteWtihItem(List<JianshuArticleDetail> articleDetailList, List<JianshuCategory> jianshuCategoryList) {

        //标记分类
        Map<JianshuArticleDetail, JianshuCategory> relation=new LinkedHashMap<>();
        for (JianshuArticleDetail articleDetail : articleDetailList) {
            JianshuCategory item=acquireCategoryName(articleDetail.getCategoryName(), jianshuCategoryList);
            if(item==null){
                throw new IllegalStateException("文章找不到分类,文章标题："+articleDetail.getTitle());
            }
            //为每个文章选好简书的分类
            relation.put(articleDetail,item);
        }
        return relation;
    }


    /**
     * 如果文章不存在，则创建条目，否则直接返回条目
     */
    public JianshuArticleItem createArticleIfNotExits(String jianshuTitle, long categoryId, Set<JianshuArticleItemResponse> jianshuArticleItemResponseList) throws InterruptedException {
        JianshuArticleItem itemIfNotExists=null;
        for (JianshuArticleItemResponse item : jianshuArticleItemResponseList) {
            String jianshuTitleTrim=jianshuTitle.replace(" ","");
            String itemItemTrim=item.getTitle().replace(" ","");
            if(jianshuTitleTrim.equals(itemItemTrim)){
                itemIfNotExists= item;
            }
        }
        if(itemIfNotExists==null){
            CreateArticleResponse articleResp = jianshuHttpClient.createArticle
                    (categoryId, jianshuTitle, true);
            itemIfNotExists=JianshuArticleItem.createJianshuArticleItem(articleResp.getId(),articleResp.getCategoryId(),articleResp.getTitle(),articleResp.isShared(),articleResp.getAutosaveControl());
            logger.info("新建文章成功，文章信息：" + articleResp);
            Thread.sleep(1000);
        }
        return itemIfNotExists;
    }

    /**
     * 获取所有文章条目
     */
    public Set<JianshuArticleItemResponse> acquireArticleItemList(List<JianshuCategory> jianshuCategoryList) throws InterruptedException {
        Set<JianshuArticleItemResponse> jianshuArticleItemResponseList =new LinkedHashSet<>();
        for (JianshuCategory jianshuCategory : jianshuCategoryList) {
            List<JianshuArticleItemResponse> itemList=jianshuHttpClient.getArticleItemList(jianshuCategory.getId());
            logger.info("获取到条目[{}]下共有{}篇文章",jianshuCategory.getName(),itemList.size());
            Thread.sleep(500);
            jianshuArticleItemResponseList.addAll(itemList);
        }
        return jianshuArticleItemResponseList;
    }

    public void updateArticleIfNoShare(JianshuArticleItem item, String contentHtml) throws InterruptedException {
        if(!item.isShared()) {
            String replaceHtml=replaceImg(contentHtml);
            UpdateArticleResponse updateArticleResponse=jianshuHttpClient.updateArticle(item.getId(),item.getTitle(),item.getAutosaveControl()+1,replaceHtml);
            if(updateArticleResponse.getContent_updated_at()>0){
                logger.info("文章更新成功: "+item.getTitle());
                Thread.sleep(200);
            }else{
                logger.warn("文章更新失败，请检查原因: "+item.getTitle());
            }
        }
    }

    /**
     * 将图片上传到简书，并替换文章中的图片链接为简书链接
     */
    private String replaceImg(String html) {

        // 解析原始 HTML
        Document doc = Jsoup.parse(html);

        // 找到 img 标签并向上找到A标签
        Elements imgElements = doc.select("img");
        for (Element imgElement : imgElements) {
            String src=imgElement.attr("src");
            if(StringUtils.isBlank(src)){
                continue;
            }
            String finalChineseTitle="";
            String alt=imgElement.attr("alt");
            String title=imgElement.attr("title");
            if(StringUtils.length(alt) > StringUtils.length(title)){
                finalChineseTitle=alt;
            }else{
                finalChineseTitle=title;
            }

            Element replaceElement;
            Element aElement=null;

            // 获取 img 标签的父节点
            Element parent=imgElement.parent();
            if(parent==null){
                throw new IllegalStateException("图片找不到parent");
            }
            if(parent.is("a")){
                aElement=parent;
            }
            Element parentOfParent=parent.parent();
            if(parentOfParent!=null && parentOfParent.is("a")){
                aElement=parentOfParent;
            }

            //如果有a标签则替换a标签，否则替换图片本身
            if(aElement!=null){
                replaceElement=aElement;
            }else{
                replaceElement =imgElement;
            }

            //如果没有找到图片说明，再次尝试从a标签的临近元素得到图片说明
            if(StringUtils.isBlank(finalChineseTitle) && aElement!=null){
                // 找到a标签的下一个兄弟元素span
                Element span = aElement.nextElementSibling();
                if (span != null && span.tagName().equals("span") && StringUtils.length(span.attr("style"))>5) {
                    // 获取span中的文本，并去除HTML实体
                    String description = span.text();
                    finalChineseTitle=description;
                    span.remove();
                }
            }

            String localPath=imgElement.attr("src");

            UploadImageResponse uploadImageResponse=jianshuImgUploadClient.uploadImage(localPath);
            logger.info("图片上传成功:{}",uploadImageResponse.getUrl());


            String titleSpan="";
            if(StringUtils.isNotBlank(finalChineseTitle)){
                titleSpan="<div class=\"image-caption\">"+finalChineseTitle+"</div>\n";
            }

            // 创建新的 HTML 结构
            String newHtml = "<div class=\"image-package\">\n" +
                    "    <img class=\"uploaded-img\" src=\"" + uploadImageResponse.getUrl() + "\" width=\"auto\" height=\"auto\">\n" +
                    "    <br>\n" +
                    titleSpan +
                    "</div>";

            // 用新的 HTML 替换原始的节点
            replaceElement.replaceWith(Jsoup.parse(newHtml).body().child(0));

        }

        // 输出修改后的 HTML
        Element body = doc.body();
        return body.html();

    }

    /**
     * 是否会员
     */
    public  boolean isMember() {
        MemberInfoResponse memberInfoResponse=jianshPublishClient.getMemberInfo();
        return memberInfoResponse.isMember();
    }

    public void timingPublish(Map<JianshuArticleDetail,JianshuArticleItem> detailWithItem) throws InterruptedException {
        Map<JianshuArticleItem, Long> itemWithSetTime=ArticleTimingPublishUtil.processArticles(detailWithItem);
        for (Map.Entry<JianshuArticleItem, Long> jianshuArticleItemLongEntry : itemWithSetTime.entrySet()) {
            jianshPublishClient.setPublishTime(jianshuArticleItemLongEntry.getKey().getId(),jianshuArticleItemLongEntry.getValue());
            logger.info("{}---设置了定时发布时间为: {}",jianshuArticleItemLongEntry.getKey().getTitle(),formattedTimestamp(jianshuArticleItemLongEntry.getValue()));
            Thread.sleep(500);
        }
    }
}
