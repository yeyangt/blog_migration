package com.august.sina;

import com.august.blog.util.DirUtil;
import com.august.sina.dto.Category;
import com.august.sina.dto.SinaArticleDetail;
import com.august.sina.dto.SinaArticleItem;
import com.august.sina.scraper.ArticleDetailScraper;
import com.august.sina.scraper.BlogCategoryScraper;
import com.august.sina.scraper.BlogListScraper;
import com.august.sina.scraper.BlogMainScraper;
import com.august.sina.util.HtmlListGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SinaBlogSpiderStarter {

    private static final Logger logger = LoggerFactory.getLogger(SinaBlogSpiderStarter.class);

    //跳过哪些模块的文章："草稿箱"、"定时发布"和"回收站"是废弃文章；"博文收藏"一般是收藏别人文章；"365"是日期模块。
    private static final List<String> skipCategoyList= Arrays.asList("草稿箱","定时发布","回收站","博文收藏","365");


    public static void main(String[] args) throws IOException, InterruptedException {

        // 博客的主页面。（PS:需要替换为目标博客的URL）
        String blogUrl = "https://blog.sina.com.cn/s/articlelist_1658071617_0_1.html";
        //登录后访问页面的cookie。（PS:需要替换为在页面F12后看到的Cookie的值）可以设置为null，此时仅抓取未登录时可访问的博客页面
        String cookie="XXX:YYYY";


        //抓取博文
        spider(blogUrl,cookie);

        //生成文章汇总页面
        String listHtmlPath=HtmlListGenerator.genListHtml(DirUtil.getArticleDir(),DirUtil.getErrorArticleDir(),DirUtil.getListDir());
        //本地预览
        HtmlListGenerator.priviewHtml(listHtmlPath);

    }

    /**
     * 从主页面开始，抓取该博客的所有博文
     */
    public static void spider(String blogUrl,String cookie) throws IOException, InterruptedException {

        //一、找到"博文目录"页面的url
        String mainHtml =BlogMainScraper.fetchHtml(blogUrl,cookie);
        String  categoryUrl= BlogMainScraper.parseCategoryUrl(mainHtml);

        //二、得到分类
        String categoryListHtml = BlogCategoryScraper.fetchHtml(categoryUrl,cookie);
        List<Category> categories = BlogCategoryScraper.parseCategories(categoryListHtml);

        Set<SinaArticleItem> allSinaArticleItems =new LinkedHashSet<>();
        // 三、得到不同分类下的文章列表
        for (Category category : categories) {
            if(skipCategoyList.contains(category.getName())){
                continue;
            }
            //每个目录下都需要翻页
            while (category.hasNext()){
                String listHtml = BlogListScraper.fetchHtml(category.next(), cookie);
                List<SinaArticleItem> sinaArticleItems = BlogListScraper.parseArticles(listHtml);
                category.setCurrentArticleItemList(sinaArticleItems);
                for (SinaArticleItem sinaArticleItem : sinaArticleItems) {
                    sinaArticleItem.setCategoryName(category.getName());
                }
                allSinaArticleItems.addAll(sinaArticleItems);
            }
        }

        logger.info("文章总条数：{}", allSinaArticleItems.size());

        // 四、抓取每一篇文章
        for (SinaArticleItem sinaArticleItem : allSinaArticleItems) {
            String articleHtml = ArticleDetailScraper.fetchHtml(sinaArticleItem.getUrl(), cookie);
            SinaArticleDetail articleDetail = ArticleDetailScraper.parseArticleDetail(sinaArticleItem.getShortTitle(), sinaArticleItem.getShortPublishTime(),articleHtml,cookie);
            articleDetail.setCategoryName(sinaArticleItem.getCategoryName());

            //保存文章到本地
            if(articleDetail.getArticleType()== SinaArticleDetail.ArticleType.ErrorParseArticle){
                articleDetail.saveArticle(DirUtil.getErrorArticleDir());
                logger.info("文章内容解析错误，保存在error目录。title-----{}", articleDetail.getTitle());
            }else{
                articleDetail.saveArticle(DirUtil.getArticleDir());
                logger.info("已保存文章，title-----{}", articleDetail.getTitle());
            }

        }

    }

}
