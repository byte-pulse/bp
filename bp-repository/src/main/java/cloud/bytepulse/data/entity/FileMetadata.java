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
     * 文件大小
     */
    private Long size;
    /**
     * 访问级别：0=公开，1=需登录
     */
    private Integer accessLevel;
    /**
     * 业务类型
     */
    private String bizType;
    /**
     * 业务实体 ID
     */
    private String bizId;
    /**
     * 文件状态 0=删除,1=存在
     */
    private Integer status;
    /**
     * 上传时间
     */
    private Date createTime;
    /**
     * 删除日期
     */
    private Date deleteTime;

}
