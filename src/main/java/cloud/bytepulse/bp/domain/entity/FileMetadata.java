package cloud.bytepulse.bp.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 文件信息表
 *
 * @author jiejiebiezheyang
 * @since 2026-01-26 13:00
 */
@TableName(value = "file_metadata")
@Data
public class FileMetadata {
    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
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
     * 访问级别 0=公开 1=需登录
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
     * 文件状态
     */
    @TableLogic(value = "1", delval = "0")
    private Integer status;

    /**
     * 上传时间
     */
    private Date createTime;

    /**
     * 删除时间
     */
    private Date deleteTime;
}