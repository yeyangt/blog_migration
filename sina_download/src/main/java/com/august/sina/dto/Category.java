package com.august.sina.dto;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 分类，以及分类的url
 */
public  class Category implements Iterator<String> {
        private final String name;
        /**
         * 分类目录的首页
         */
        private final String defaultUrl;


        List<SinaArticleItem> currentSinaArticleItemList;
        private int pageNumIterator;

        public Category(String name, String defaultUrl) {
            this.name = name;
            this.defaultUrl = defaultUrl;
            this.pageNumIterator =0;//0代表分类的首页
        }

        public String getName() {
            return name;
        }

        private static final  Pattern pattern  = Pattern.compile("https://blog.sina.com.cn/s/articlelist_(\\d+_\\d+_)(\\d+).html");

        @Override
        public String next() {
            if(pageNumIterator ==0){
                pageNumIterator++;
                return this.defaultUrl;
            }

            // 正则表达式，用于匹配范式 URL
            Matcher matcher = pattern.matcher(this.defaultUrl);

            // 如果匹配成功，提取信息并生成下一页的URL
            if (matcher.matches()) {
                String base = matcher.group(1);  // 提取出1504965870_0_部分
                String page = matcher.group(2);  // 提取当前页数
                int nextPage = Integer.parseInt(page) + this.pageNumIterator;
                this.pageNumIterator++;
                return "https://blog.sina.com.cn/s/articlelist_" + base + nextPage + ".html";
            } else {
                throw new IllegalStateException("hasNext决定了不应进入此处的代码");
            }
        }


        @Override
        public boolean hasNext() {
            if(pageNumIterator ==0){
                return true;
            }

            //最多获取1000页
            if(pageNumIterator >1000){
                return false;
            }

            // 正则表达式，用于匹配范式 URL
            Matcher matcher = pattern.matcher(this.defaultUrl);
            if (matcher.matches()) {
                return true;
            }

            return false;
        }

        public void setCurrentArticleItemList(List<SinaArticleItem> currentSinaArticleItemList) {
            if(currentSinaArticleItemList.equals(this.currentSinaArticleItemList) || currentSinaArticleItemList.isEmpty()){
                //证明已经到头了
                this.pageNumIterator=Integer.MAX_VALUE;
            }

            this.currentSinaArticleItemList = currentSinaArticleItemList;
        }

        @Override
        public String toString() {
            return "Category{name='" + name + "', url='" + defaultUrl + "'}";
        }
    }