package cloud.bytepulse.bp.framework.constant;

/**
 * 正则常量
 *
 * @author jiejiebiezheyang
 * @since 2024-05-15 17:00
 */
public class Regex {

    /**
     * 中国大陆手机号正则
     */
    public static final String PHONE = "^((13[0-9])|(14[0|5|6|7|9])|(15[0|1|2|3|5|6|7|8|9])|(16[2|5|6|7])|(17[0|1|2|3|5|6|7|8])|(18[0-9])|(19[0|1|2|3|5|6|7|8|9]))\\d{8}$";

    /**
     * 邮箱正则
     */
    public static final String EMAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}(\\.[a-zA-Z]{2,})?$";

    /**
     * 6到30位密码正则，至少包含一个字母和数字
     */
    public static final String PASSWORD = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,30}$";

    /**
     * 用户名
     */
    public static final String USERNAME = "^(?=.*[A-Za-z])[A-Za-z\\d]{6,}$";

    /**
     * 校验方法
     */
    public static boolean isMatch(String regex, String input) {
        return input != null && input.matches(regex);
    }
}
