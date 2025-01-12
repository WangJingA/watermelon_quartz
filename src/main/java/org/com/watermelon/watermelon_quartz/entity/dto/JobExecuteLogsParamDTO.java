package org.com.watermelon.watermelon_quartz.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 任务执行日志DTO类
 * @author watermelon
 * @date 2024/08/18
 */
@Data
@Accessors(chain = true)
public class JobExecuteLogsParamDTO extends PageDTO{
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
     * 日志详情关键字
     */
    private String jobLogKey;
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 执行开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT + 8")
    private String executeBeginTime;
    /**
     * 执行结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT + 8")
    private String executeEndTime;
}
