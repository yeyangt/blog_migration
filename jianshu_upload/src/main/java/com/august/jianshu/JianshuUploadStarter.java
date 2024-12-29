package com.august.jianshu;

import com.august.blog.util.DirUtil;
import com.august.jianshu.dto.*;
import com.august.jianshu.service.BlogDetectorService;
import com.august.jianshu.service.SinaBlogLocalReader;
import com.august.jianshu.util.JianshuHtmlListGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class JianshuUploadStarter {

    private static final Logger logger = LoggerFactory.getLogger(JianshuUploadStarter.class);


    public static void main(String[] args) throws InterruptedException {

        //请修改值，登录简书后F12获取到cookie
        final String cookie="XXX:YYYY";

        //上传文章到简书
        upload(cookie);


    }

    public static void upload(String cookie) throws InterruptedException {

        BlogDetectorService blogDetectorService =new BlogDetectorService(cookie);

        //1、解析所有文章与分类
        List<JianshuArticleDetail> articleDetailList = SinaBlogLocalReader.parseLocalHtmlFiles(DirUtil.getArticleDir(),DirUtil.getErrorArticleDir());
        //放一篇汇总文章在最后
        articleDetailList.add(JianshuHtmlListGenerator.genHtmlListContet(DirUtil.getArticleDir(),DirUtil.getErrorArticleDir()));
        Set<String> categoryNameSet=SinaBlogLocalReader.collectCategoryName(articleDetailList);

        // 2. 如果分类不存在则在简书上创建
        List<JianshuCategory> jianshuCategoryList = blogDetectorService.createCategoryIfNotExits(categoryNameSet);
        //为每个文章关联上分类
        Map<JianshuArticleDetail, JianshuCategory> detailWithCategory=blogDetectorService.relationNoteWtihItem(articleDetailList, jianshuCategoryList);

        // 3. 为每一篇文章创建ArticleItem（文章条目）
        Map<JianshuArticleDetail,JianshuArticleItem> detailWithItem=new LinkedHashMap<>();
        //3.1、获取得到所有的ArticleItem
        Set<JianshuArticleItemResponse> itemList=blogDetectorService.acquireArticleItemList(jianshuCategoryList);
        //3.2、如果文章不在条目中，则创建一个文章条目
        for (JianshuArticleDetail detail : articleDetailList) {
            JianshuArticleItem articleItem=blogDetectorService.
                    createArticleIfNotExits(detail.getJianshuTitle(),detailWithCategory.get(detail).getId(),itemList);
            detailWithItem.put(detail,articleItem);
        }

        //4、非发布状态的文章直接更新
        for (Map.Entry<JianshuArticleDetail, JianshuArticleItem> articleEntry : detailWithItem.entrySet()) {
            JianshuArticleDetail detail=articleEntry.getKey();
            JianshuArticleItem jianshuArticleItem=articleEntry.getValue();

            if(detail instanceof JianshuArtilceListDetail) {
                //汇总文章
                String afterUploadContent=((JianshuArtilceListDetail)detail).getJianshuAfterUploadContent();
                blogDetectorService.updateArticleIfNoShare(jianshuArticleItem,afterUploadContent);
            }else{
                //博客文章
                blogDetectorService.updateArticleIfNoShare(articleEntry.getValue(),articleEntry.getKey().getContentHtml());
            }
        }

        //5、如果是会员，设置晚上定时发布
        if(blogDetectorService.isMember()){
           blogDetectorService.timingPublish(detailWithItem);
        }

    }



}
