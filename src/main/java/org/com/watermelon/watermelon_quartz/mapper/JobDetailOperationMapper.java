package org.com.watermelon.watermelon_quartz.mapper;


import org.apache.ibatis.annotations.Param;
import org.com.watermelon.watermelon_quartz.entity.dto.*;
import org.com.watermelon.watermelon_quartz.entity.po.JobDetailPO;
import org.com.watermelon.watermelon_quartz.entity.po.JobLogPO;
import org.com.watermelon.watermelon_quartz.entity.po.JobTranStatePO;
import org.com.watermelon.watermelon_quartz.entity.po.UserJobPO;

import java.util.List;

/**
 * 任务操作Mapper类
 * @author watermelon
 * @date 2024/08/01
 */
public interface JobDetailOperationMapper {
    /**
     * 查询所有任务详情
     * @return List<JobDetailPO>
     */
    List<JobDetailPO> listJobDetail(@Param("paramDTO") JobListParamDTO paramDTO);

    /**
     * 添加用户任务关联信息
     * @param userJobPO 任务类
     * @return boolean
     */
    boolean addJobUser(UserJobPO userJobPO);

    /**
     * 添加http任务详情
     * @param addQuartzJobDTO 新增任务DTO类
     * @return boolean
     */
    boolean addJobDetail(AddQuartzJobDTO addQuartzJobDTO);


    /**
     * 更新http任务详情
     * @param addQuartzJobDTO
     * @return boolean
     */
    boolean updateJobDetail(AddQuartzJobDTO addQuartzJobDTO);

    /**
     * 查询所有的http任务详情
     * @return List<JobHttpDetailDTO>
     */
    List<JobHttpDetailDTO> listHttpJobDetail();

    /**
     * 查询任务请求接口详情
     * @param jobName
     * @param jobGroupName
     * @return
     */
    JobHttpDetailDTO queryHttpJobDetail(@Param("jobName") String jobName,@Param("jobGroupName") String jobGroupName);

    /**
     * 判断是否存在调度任务
     * @param userJobPO 任务类
     * @return int
     */
    List<UserJobPO> isExistJob(UserJobPO userJobPO);

    /**
     * 删除用户任务
     * @param jobName 任务名称
     * @param groupName 群组名称
     * @return
     */
    boolean delUserJob(@Param("jobName") String jobName,@Param("groupName") String groupName);

    /**
     * 添加任务执行日志
     * @param jobLogPO 日志类
     * @return boolean
     */
    boolean addJobLogs(JobLogPO jobLogPO);

    /**
     * 任务群组下拉列表
     * @return List<String>
     */
    List<String> listJobGroup();

    /**
     * 任务状态中英文数据列表
     * @return List<JobTranStatePO>
     */
    List<JobTranStatePO> jobTranState();

    /**
     * 任务执行日志详情
     * @param paramDTO 查询参数
     * @return List<JobExecuteLogsDTO>
     */
    List<JobExecuteLogsInfoDTO> jobExecuteLogs(@Param("paramDTO") JobExecuteLogsParamDTO paramDTO);
}
