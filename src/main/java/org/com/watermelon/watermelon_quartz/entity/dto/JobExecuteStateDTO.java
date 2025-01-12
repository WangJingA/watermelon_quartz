package org.com.watermelon.watermelon_quartz.entity.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 任务执行状态返回类
 * @author watermelon
 * @date 2024/08/04
 */
@Data
@Accessors(chain = true)
public class JobExecuteStateDTO {
    /**
     * 任务状态英文状态码
     */
    private String stateEnStr;
    /**
     * 任务状态中文状态码
     */
    private String stateCnStr;
}
