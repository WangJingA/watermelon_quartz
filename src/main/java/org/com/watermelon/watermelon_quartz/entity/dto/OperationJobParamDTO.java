package org.com.watermelon.watermelon_quartz.entity.dto;

import lombok.Data;

@Data
public class OperationJobParamDTO {
    /**
     * 任务名称
     */
    private String jobName;
    /**
     * 群组名称
     */
    private String jobGroupName;
}
