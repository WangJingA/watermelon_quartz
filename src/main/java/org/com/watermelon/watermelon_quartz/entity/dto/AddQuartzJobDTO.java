package org.com.watermelon.watermelon_quartz.entity.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 调度任务参数
 * @author watermelon
 * @date 2024/07/28
 */
@Data
public class AddQuartzJobDTO {
    /**
     * 请求方式，POST请求或者GET请求
     */
    private String requestWay;
    /**
     * 请求的java方法名称
     */
    private String requestMethod;
    /**
     * 任务名称
     */
    private String jobName;
    /**
     * 任务相对路径
     */
    private String jobClassPath;
    /**
     * 执行调度cron表达式
     */
    private String jobCron;
    /**
     * 任务描述
     */
    private String jobDescription;
    /**
     * 任务组名称
     */
    private String jobGroup;
    /**
     * 调度参数
     */
    private String jobDataMap;
}
