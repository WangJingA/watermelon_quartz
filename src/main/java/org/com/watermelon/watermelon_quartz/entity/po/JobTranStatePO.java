package org.com.watermelon.watermelon_quartz.entity.po;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class JobTranStatePO {
    /**
     * 调度任务状态英文名称
     */
    private String scheduleStateEn;
    /**
     * 调度任务中文名称
     */
    private String scheduleStateCn;
}
