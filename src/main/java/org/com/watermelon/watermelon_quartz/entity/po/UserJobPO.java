package org.com.watermelon.watermelon_quartz.entity.po;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用户任务实体类
 * @author watermelon
 * @date 2024/08/04
 */
@Data
@Accessors(chain = true)
public class UserJobPO {
    /**
     * 任务名称
     */
    private String jobName;
    /**
     * 任务群组
     */
    private String jobGroup;
    /**
     * 用户ID
     */
    private String userId;
}
