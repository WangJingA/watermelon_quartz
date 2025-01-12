package org.com.watermelon.watermelon_quartz.entity.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 任务列表查询参数类
 * @author watermelon
 * @date 2024/08/04
 */
@Data
@Accessors(chain = true)
public class JobListParamDTO extends PageDTO{
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 执行状态
     */
    private String executeState;
    /**
     * 任务名称
     */
    private String jobName;
    /**
     * 任务群组名称
     */
    private String jobGroupName;
}
