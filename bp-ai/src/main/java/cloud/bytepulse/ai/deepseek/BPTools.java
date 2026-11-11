package cloud.bytepulse.ai.deepseek;

import cloud.bytepulse.common.core.util.ReqUtils;
import cloud.bytepulse.data.entity.SysUser;
import cloud.bytepulse.service.user.UserService;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.management.*;
import java.util.Base64;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author jiejiebiezheyang
 * @since 2026-11-09 16:15
 */
@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class BPTools {

    private final UserService userService;

    @Tool(description = "查询 bp 系统当前用户的账号状态和所属角色。只读操作。")
    public String queryCurrentUserStatus(ToolContext toolContext) {
        long userId = (long) toolContext.getContext().get("userId");
        SysUser user = userService.getUserStatus(userId);
        return String.format("当前用户名: %s, 用户ID: %s, 昵称: %s, 简介: %s",
                user.getUsername(),
                user.getId(),
                user.getNickname(),
                user.getIntroduction());
    }

    @Tool(description = "查询 bp 系统当前所运行的系统的信息。只读操作。")
    public String getSystemInfoString() throws InterruptedException {
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        OperatingSystem os = si.getOperatingSystem();

        StringBuilder sb = new StringBuilder();

        // 1. 操作系统信息
        sb.append("=== 操作系统 ===\n");
        sb.append("系统: ").append(os).append("\n");
        sb.append("版本: ").append(os.getVersionInfo()).append("\n");
        sb.append("架构: ").append(os.getBitness()).append("位\n");
        sb.append("运行时间: ").append(os.getSystemUptime()).append(" 秒\n\n");

        // 2. JDK / JVM 信息（新增）
        appendJvmInfo(sb);

        // 3. CPU 信息
        CentralProcessor processor = hal.getProcessor();
        sb.append("=== CPU ===\n");
        sb.append("型号: ").append(processor.getProcessorIdentifier().getName()).append("\n");
        sb.append("物理核心: ").append(processor.getPhysicalProcessorCount()).append("\n");
        sb.append("逻辑核心: ").append(processor.getLogicalProcessorCount()).append("\n");

        // 计算 CPU 负载（需要间隔 1 秒采样）
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        TimeUnit.MILLISECONDS.sleep(1000);
        double cpuLoad = processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100;
        sb.append(String.format("使用率: %.2f%%\n\n", cpuLoad));

        // 4. 内存信息
        GlobalMemory memory = hal.getMemory();
        long totalMem = memory.getTotal();
        long availMem = memory.getAvailable();
        long usedMem = totalMem - availMem;
        sb.append("=== 内存 ===\n");
        sb.append("总内存: ").append(bytesToMB(totalMem)).append(" MB\n");
        sb.append("已用: ").append(bytesToMB(usedMem)).append(" MB\n");
        sb.append("可用: ").append(bytesToMB(availMem)).append(" MB\n\n");

        // 5. 磁盘信息（汇总）
        List<HWDiskStore> disks = hal.getDiskStores();
        long totalDiskSize = disks.stream().mapToLong(HWDiskStore::getSize).sum();
        sb.append("=== 磁盘 ===\n");
        sb.append("磁盘数量: ").append(disks.size()).append("\n");
        sb.append("总容量: ").append(bytesToGB(totalDiskSize)).append(" GB\n");

        return sb.toString();
    }

    /**
     * 追加当前 JDK / JVM 运行时信息
     */
    private void appendJvmInfo(StringBuilder sb) {
        sb.append("=== JDK / JVM ===\n");

        // Java 基本信息
        sb.append("Java 版本: ").append(System.getProperty("java.version"))
                .append(" (").append(System.getProperty("java.vendor")).append(")\n");
        sb.append("Java Home: ").append(System.getProperty("java.home")).append("\n");
        sb.append("JVM: ").append(System.getProperty("java.vm.name")).append(" ")
                .append(System.getProperty("java.vm.version")).append("\n");
        sb.append("JVM 供应商: ").append(System.getProperty("java.vm.vendor")).append("\n");

        // 运行时信息
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        sb.append("JVM 启动时间: ").append(new Date(runtime.getStartTime())).append("\n");
        sb.append("JVM 运行时间: ")
                .append(formatDuration(runtime.getUptime() / 1000))
                .append(" (").append(runtime.getUptime() / 1000).append(" 秒)\n");
        sb.append("JVM 启动参数: ").append(runtime.getInputArguments()).append("\n");

        // 内存（JVM 堆 / 非堆）
        MemoryMXBean memBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heap = memBean.getHeapMemoryUsage();
        MemoryUsage nonHeap = memBean.getNonHeapMemoryUsage();
        String heapMax = heap.getMax() > 0 ? bytesToMB(heap.getMax()) + " MB" : "未定义";
        sb.append("堆内存: 已用 ").append(bytesToMB(heap.getUsed())).append(" MB")
                .append(" / 已提交 ").append(bytesToMB(heap.getCommitted())).append(" MB")
                .append(" / 最大 ").append(heapMax).append("\n");
        sb.append("非堆内存: 已用 ").append(bytesToMB(nonHeap.getUsed())).append(" MB")
                .append(" / 已提交 ").append(bytesToMB(nonHeap.getCommitted())).append(" MB\n");

        // 线程
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        sb.append("线程数: ").append(threadBean.getThreadCount())
                .append(" (守护线程: ").append(threadBean.getDaemonThreadCount())
                .append(", 峰值: ").append(threadBean.getPeakThreadCount())
                .append(", 累计创建: ").append(threadBean.getTotalStartedThreadCount())
                .append(")\n");

        // 类加载
        ClassLoadingMXBean classBean = ManagementFactory.getClassLoadingMXBean();
        sb.append("已加载类: ").append(classBean.getLoadedClassCount())
                .append(" (累计: ").append(classBean.getTotalLoadedClassCount())
                .append(", 卸载: ").append(classBean.getUnloadedClassCount()).append(")\n");

        // 垃圾回收器
        sb.append("垃圾回收器: ");
        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            sb.append(gc.getName())
                    .append(" [次数=").append(gc.getCollectionCount())
                    .append(", 耗时=").append(gc.getCollectionTime()).append("ms] ");
        }
        sb.append("\n\n");
    }

    /**
     * 把秒数格式化为 "x 天 HH:mm:ss" 或 "HH:mm:ss"
     */
    private static String formatDuration(long seconds) {
        long d = seconds / 86400;
        long h = (seconds % 86400) / 3600;
        long m = (seconds % 3600) / 60;
        long s = seconds % 60;
        return d > 0
                ? String.format("%d 天 %02d:%02d:%02d", d, h, m, s)
                : String.format("%02d:%02d:%02d", h, m, s);
    }

    private static long bytesToMB(long bytes) {
        return bytes / 1024 / 1024;
    }

    private static long bytesToGB(long bytes) {
        return bytes / 1024 / 1024 / 1024;
    }

    @Tool(description = "查天气, 不填参数查询 bp 系统当前用户所在地天气,填参数可以查指定城市。(数据来源:https://wttr.in/城市?format=j1)")
    public String queryCurrentUserWeather(@ToolParam(required = false, description = "城市名称, 可填空字符串") String city,
                                          ToolContext toolContext) {
        if (StringUtils.hasText(city)) {
            HttpResponse<String> resp = Unirest.get("https://wttr.in/" + city + "?format=j1")
                    .asString();
            if (resp.getStatus() == 200) {
                return resp.getBody();
            } else {
                return "无法查询";
            }
        } else {
            String ip = (String) toolContext.getContext().get("ip");
            ReqUtils.IpGeoInfo ipFeoInfo = ReqUtils.getIpFeoInfo(ip);
            String isp = ipFeoInfo.getIsp();
            if ("内网".equals(isp) || "本机".equals(isp)) {
                // 获取本机的互联网ip
                HttpResponse<String> resp = Unirest.get("https://api.ipify.org/")
                        .asString();
                if (resp.getStatus() == 200) {
                    ip = resp.getBody();
                    // 重新获取ip信息
                    ipFeoInfo = ReqUtils.getIpFeoInfo(ip);
                } else {
                    return "无法查询";
                }
            }
            String ipCity = ipFeoInfo.getCity();
            HttpResponse<String> resp = Unirest.get("https://wttr.in/" + ipCity + "?format=j1")
                    .asString();
            if (resp.getStatus() == 200) {
                return resp.getBody();
            } else {
                return "无法查询";
            }
        }
    }
}
