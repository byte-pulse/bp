package cloud.bytepulse.common.core.util;

import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * 时间工具类
 *
 * @author jiejiebiezheyang
 * @since 2024-03-22 14:00
 */
public class DateUtils {

    /**
     * 1 秒
     */
    public static final long ONE_SECOND = 1000;
    /**
     * 1 分钟
     */
    public static final long ONE_MINUTE = 60 * ONE_SECOND;
    /**
     * 半小时
     */
    public static final long HALF_HOUR = 30 * ONE_MINUTE;
    /**
     * 1 小时
     */
    public static final long ONE_HOUR = 60 * ONE_MINUTE;
    /**
     * 1 天
     */
    public static final long ONE_DAY = 24 * ONE_HOUR;
    /**
     * 1 周
     */
    public static final long ONE_WEEK = 7 * ONE_DAY;
    /**
     * 1 月
     */
    public static final long ONE_MONTH = 30 * ONE_DAY;
    /**
     * 1 年
     */
    public static final long ONE_YEAR = 12 * ONE_MONTH;

    /**
     * 容易读的时间
     */
    public static String easyReadable(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        if (dateTime.isAfter(now)) {
            return "时间错误";
        }

        Duration duration = Duration.between(dateTime, now);
        long seconds = duration.getSeconds();

        if (seconds >= ChronoUnit.YEARS.getDuration().getSeconds()) {
            long years = ChronoUnit.YEARS.between(dateTime, now);
            return years + "年前";
        } else if (seconds >= ChronoUnit.MONTHS.getDuration().getSeconds()) {
            long months = ChronoUnit.MONTHS.between(dateTime, now);
            return months + "个月前";
        } else if (seconds >= ChronoUnit.WEEKS.getDuration().getSeconds()) {
            long weeks = ChronoUnit.WEEKS.between(dateTime, now);
            return weeks + "周前";
        } else if (seconds >= ChronoUnit.DAYS.getDuration().getSeconds()) {
            long days = ChronoUnit.DAYS.between(dateTime, now);
            return days + "天前";
        } else if (seconds >= ChronoUnit.HOURS.getDuration().getSeconds()) {
            long hours = duration.toHours();
            return hours + "小时前";
        } else if (seconds >= ChronoUnit.MINUTES.getDuration().getSeconds() * 30) {
            return "半小时前";
        } else if (seconds >= ChronoUnit.MINUTES.getDuration().getSeconds()) {
            long minutes = duration.toMinutes();
            return minutes + "分钟前";
        } else {
            return "刚刚";
        }
    }

    /**
     * 兼容旧版Date的辅助方法
     */
    public static String easyReadable(Date date) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(
                date.toInstant(), ZoneId.systemDefault()
        );
        return easyReadable(dateTime);
    }


    /**
     * x天x时x分x秒格式
     */
    public static String getTimeStr(long millisecond) {
        long seconds = millisecond % 60;
        long minutes = (millisecond / 60) % 60;
        long hours = (millisecond / 3600) % 24;
        long days = millisecond / (3600 * 24);
        return String.format("%d天%d时%d分%d秒", days, hours, minutes, seconds);
    }

    /**
     * 字符串转指定格式日期
     *
     * @param pattern 日期格式
     * @param dateStr 日期字符串
     * @return {@link Date}
     * @throws ParseException 日期解析失败
     */
    public static LocalDateTime parse(String pattern, String dateStr) throws ParseException {
        // 定义日期格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        // 解析日期
        return LocalDateTime.parse(dateStr, formatter);
    }

    /**
     * 日期转指定格式字符串
     *
     * @param pattern 日期格式
     * @param date    日期
     * @return {@link Date}
     */
    public static String parseDate(String pattern, LocalDateTime date) {
        // 定义日期格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        // 获取当前时间并格式化
        return date.format(formatter);
    }

    /**
     * 当前时间
     */
    public static String now() {
        return parseDate("yyyy-MM-dd HH:mm:ss", LocalDateTime.now());
    }

    /**
     * 当前日期
     */
    public static String today() {
        return parseDate("yyyy-MM-dd", LocalDateTime.now());
    }
}
