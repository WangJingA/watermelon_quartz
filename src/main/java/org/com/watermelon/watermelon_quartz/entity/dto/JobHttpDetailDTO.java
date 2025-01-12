package org.com.watermelon.watermelon_quartz.entity.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * http请求任务详情类
 * @author watermelon
 * @date 2024/08/17
 */
@Data
@Accessors(chain = true)
public class JobHttpDetailDTO {
    /**
     * 任务名称
     */
    private String jobName;
    /**
     * 任务组名
     */
    private String jobGroupName;
    /**
     * 请求方式
     */
    private String requestWay;
    /**
     * 请求地址
     */
    private String requestMethod;
    /**
     * 请求参数
     */
    private String requestParam;
    /**
     * 描述
     */
    private String description;
}
