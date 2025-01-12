package org.com.watermelon.watermelon_quartz.entity.dto;

import lombok.Data;

@Data
public class JobExecuteLogsInfoDTO {
    /**
     * 任务名称
     */
    private String jobName;
    /**
     * 任务群组
     */
    private String jobGroupName;
    /**
     * 执行状态
     */
    private String executeState;
    /**
     * 任务执行状态名称
     */
    private String executeStateName;
    /**
     * 日志详情
     */
    private String logMsg;
    /**
     * 执行结束时间
     */
    private String executeTime;
}
