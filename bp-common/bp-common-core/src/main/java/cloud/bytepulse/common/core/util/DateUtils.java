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
}
