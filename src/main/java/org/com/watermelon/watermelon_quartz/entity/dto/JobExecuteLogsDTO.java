package org.com.watermelon.watermelon_quartz.entity.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 任务执行日志DTO类
 * @author watermelon
 * @date 2024/08/18
 */
@Data
@Accessors(chain = true)
public class JobExecuteLogsDTO extends PageDTO{

    private Integer total;

    private List<JobExecuteLogsInfoDTO> jobExecuteLogsInfoDTOS;

}
