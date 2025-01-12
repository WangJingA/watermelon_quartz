package org.com.watermelon.watermelon_quartz.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * 删除所有任务DTO类
 * @author watermelon
 * @date 2024/08/13
 */
@Data
public class DelJobDTO {


    private List<JobInfo> jobInfoList;


    @Data
   public static class JobInfo {
        /**
         * 任务名称
         */
        private String jobName;
        /**
         * 群组名称
         */
        private String groupName;
    }
}
