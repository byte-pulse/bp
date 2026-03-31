package cloud.bytepulse.bp.common.enums.errorcode;

/**
 * @author jiejiebiezheyang
 * @since 2026-03-31 20:31
 */
public enum FileErrorCode implements ErrorCode {
    FILE_NOT_EXIST(20000, "文件不存在"),
    FIle_UPLOAD_FAIL(20001, "文件上传失败"),
    FILE_DELETE_FAIL(20002, "文件删除失败"),
    FILE_DOWNLOAD_FAIL(20003, "文件下载失败"),
    DIR_DELETE_FAIL(20004, "目录删除失败"),
    DIR_LIST_FAIL(20005, "列出目录失败");

    private final int code;

    private final String message;

    FileErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }
}
