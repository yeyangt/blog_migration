package com.august.jianshu.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class PublishTimeUtil {

    /**
     * 两个时间字符串：2012-09-05 09:18、2012-09-05 09.18.00
     * 替换为：2012-09-05 09.18.00
     *
     * 即：如果时前者就替换，如果时后者就保留
     */
    /**
     * 标准化时间字符串为 "YYYY-MM-DD HH.MM.SS" 格式
     *
     * @param timeString 原始时间字符串
     * @return 格式化后的时间字符串
     */
    public static String standardizeTime(String timeString) {
        // 定义正则表达式，匹配日期和时间部分
        // 日期部分: \d{4}-\d{2}-\d{2}
        // 时间部分: \d{2}[:.]\d{2}([:.]\d{2})?
        String regex = "^(\\d{4}-\\d{2}-\\d{2})\\s+(\\d{2})[:.](\\d{2})(?:[:.](\\d{2}))?$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(timeString);

        if (matcher.find()) {
            String date = matcher.group(1); // "2012-09-05"
            String hour = matcher.group(2); // "09"
            String minute = matcher.group(3); // "18"
            String second = matcher.group(4); // "00" 或 null

            if (second == null) {
                second = "00"; // 如果秒数不存在，补全为 "00"
            }

            // 构建标准化的时间字符串，使用 "." 作为分隔符
            return String.format("%s %s:%s:%s", date, hour, minute, second);
        } else {
            // 如果不匹配预期的格式，可以选择抛出异常或返回原字符串
            // 这里选择返回原字符串
            return timeString;
        }
    }


}
