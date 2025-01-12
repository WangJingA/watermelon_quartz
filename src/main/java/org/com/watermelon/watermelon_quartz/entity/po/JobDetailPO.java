package org.com.watermelon.watermelon_quartz.entity.po;

import lombok.Data;

/**
 * 任务详情
 * @author watermelon
 * @date 2024/08/01
 */
@Data
public class JobDetailPO {
    /**
     * 任务名称
     */
    private String jobName;
    /**
     * 任务描述
     */
    private String jobDescription;
    /**
     * 任务群组名称
     */
    private String jobGroupName;
    /**
     * 任务类名
     */
    private String jobClassName;
    /**
     * 触发器名称
     */
    private String triggerName;
    /**
     * 触发器群组名称
     */
    private String triggerGroupName;
    /**
     * 首次执行时间
     */
    private String prevFireTime;
    /**
     * 下一次执行时间
     */
    private String nextFireTime;
    /**
     * 定时cron表达式
     */
    private String cronExpression;
    /**
     * 触发器状态
     */
    private String triggerState;

    /**
     * 用户ID
     */
    private String userId;
}
