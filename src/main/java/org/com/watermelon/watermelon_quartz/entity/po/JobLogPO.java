package org.com.watermelon.watermelon_quartz.entity.po;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 任务日志详情类
 * @author watermelon
 * @date 2024/08/04
 */
@Data
@Accessors(chain = true)
public class JobLogPO {
    /**
     * 任务名称
     */
    private String jobName;
    /**
     * 任务群组
     */
    private String jobGroup;
    /**
     * 日志信息
     */
    private String logMsg;
    /**
     * 执行状态
     */
    private String executeState;
    /**
     * 执行时间
     */
    private Date executeTime;
}
