package com.august.jianshu.util;

import com.august.jianshu.dto.JianshuArticleDetail;
import com.august.jianshu.dto.JianshuArticleItem;
import org.apache.commons.collections4.MapUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 定时发布时间设置
 */
public abstract class ArticleTimingPublishUtil {

    public static Map<JianshuArticleItem, Long> processArticles(Map<JianshuArticleDetail,JianshuArticleItem> detailWithItem) {

        Map<JianshuArticleItem,JianshuArticleDetail> itemWithDetail=MapUtils.invertMap(detailWithItem);

        // 获取今天的日期
        LocalDateTime today = LocalDateTime.now().toLocalDate().atStartOfDay();

        // 过滤出 isShared 为 false 的文章，并按 getPublishTime 排序
        List<JianshuArticleItem> filteredAndSortedArticles = itemWithDetail.keySet().stream()
                .filter(article -> !article.isShared()) // 过滤掉 isShared 为 true 的文章
                .sorted(Comparator.comparing(jianshuArticleItem->itemWithDetail.get(jianshuArticleItem).getPublishTime())) // 按时间排序
                .collect(Collectors.toList());

        // 生成结果 Map
        Map<JianshuArticleItem, Long> result = new LinkedHashMap<>();
        int index = 0;
        for (JianshuArticleItem article : filteredAndSortedArticles) {
            // 计算新时间戳
            long newPublishTime = calculateNewPublishTime(today, index);

            // 添加到结果 Map 中
            result.put(article, newPublishTime);

            // 每天的两篇文章，index + 1 用于在新的日期上生成新的时间戳
            index++;
        }

        return result;
    }

    private static long calculateNewPublishTime(LocalDateTime today, int index) {
        // 每天两篇文章，分别是 10:00 PM 和 11:00 PM
        LocalDateTime publishTime = today.plusDays(index / 2) // 每两篇文章换一天
                .withHour((index % 2 == 0) ? 22 : 23) // 10 PM 或 11 PM
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        // 转换为基于秒的时间戳
        return publishTime.toEpochSecond(ZoneOffset.ofHours(8)); // 使用新加坡时区
    }


    public static String formattedTimestamp(long timestamp) {
        // 将时间戳转换为 LocalDateTime
        LocalDateTime dateTime = Instant.ofEpochSecond(timestamp).atOffset(ZoneOffset.ofHours(8)).toLocalDateTime();

        // 定义格式化器，格式为：yyyy-MM-dd HH:mm:ss
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 格式化并打印
        String formattedDateTime = dateTime.format(formatter);

        return formattedDateTime;
    }


}
