package org.com.watermelon.watermelon_quartz.entity.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 首页节目数据
 * @author watermelon
 * @date 2024/08/27
 */
@Data
@Accessors(chain = true)
public class DashboardDataDTO {
    /**
     * 执行中的任务数量
     */
    private String executingTaskAmount;
    /**
     * 等待执行的任务数量
     */
    private String waitingTaskAmount;
    /**
     * 阻塞的任务数量
     */
    private String blockedTaskAmount;
    /**
     * 执行错误任务数量
     */
    private String wrongTaskAmount;
    /**
     * 暂停任务数量
     */
    private String pauseTaskAmount;
    /**
     * 执行中的任务百分比
     */
    private String executingTaskPercentage;
    /**
     * 等待执行的任务百分比
     */
    private String waitingTaskPercentage;
    /**
     * 阻塞任务百分比
     */
    private String blockedTaskPercentage;
    /**
     * 暂停任务百分比
     */
    private String pauseTaskPercentage;
    /**
     * 错误任务百分比
     */
    private String wrongTaskPercentage;

    /**
     * 当天执行任务详情数据
     */
    List<NowDayExecuteTask> nowDayExecuteTasks;
    /**
     * 今日执行项详情
     */
    @Data
    public static class NowDayExecuteTask{
        /**
         * 执行的任务名称
         */
       private String executeTaskName;
        /**
         * 执行的日期
         */
       private String executeDate;
        /**
         * 下一次执行的时间
         */
       private String executeNextTime;
        /**
         * 执行时间表达式描述
         */
       private String executeMemo;
    }
}
