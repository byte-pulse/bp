package cloud.bytepulse.data.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


import java.util.Date;

/**
 * 文件信息表 <p> file_metadata
 *
 * @author jiejiebiezheyang
 * @since 2026-09-08 17:29:49
 */
@Data
@TableName(value = "file_metadata")
public class FileMetadata {

    /**
     * 主键
     */
    private Long id;
    /**
     * 原始文件名
     */
    private String fileName;
    /**
     * MinIO 对象名
     */
    private String objectName;
    /**
     * MIME 类型
     */
    private String contentType;
    /**
}
