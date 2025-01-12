package org.com.watermelon.watermelon_quartz.service;

import org.com.watermelon.watermelon_quartz.entity.bo.VisitJavaControllerBO;
import org.com.watermelon.watermelon_quartz.entity.dto.*;

import java.util.List;

/**
 * 调度中心调用java服务控制器Service
 * @author watermelon
 * @date 2024/07/28
 */
public interface JavaJobService {
    void addJavaJob(AddQuartzJobDTO addQuartzJobDTO) throws Exception;

    /**
     * 查询所有的任务详情
     * @return List<JobDetailDTO>
     */
    JobDetailDTO listJobDetails(JobListParamDTO paramDTO);

    /**
     * 删除任务
     * @param operationJobParamDTO
     * @return boolean
     */
    boolean deleteJob(OperationJobParamDTO operationJobParamDTO) throws Exception;

    /**
     * 暂停任务
     * @param operationJobParamDTO
     * @return boolean
     */
    boolean pauseJob(OperationJobParamDTO operationJobParamDTO) throws Exception;

    /**
     * 唤醒任务
     * @param operationJobParamDTO
     * @return
     */
    boolean resumeJob(OperationJobParamDTO operationJobParamDTO) throws Exception;

    /**
     * 修改任务
     * @param operationJobParamDTO
     * @return
     */
    boolean updateJob(AddQuartzJobDTO operationJobParamDTO) throws Exception;

    /**
     * 唤醒所有任务
     * @param
     * @return
     */
    boolean resumeAllJob() throws Exception;

    /**
     * 暂停所有任务
     * @param
     * @return
     */
    boolean pauseAllJob() throws Exception;

    /**
     * 任务群组名称
     * @return
     */
    List<String> listGroupName();

    /**
     * 执行状态列表数据
     * @return
     */
    List<JobExecuteStateDTO> listExecuteState();

    /**
     * 调度任务状态列表数据
     * @return List<JobTranStateDTO>
     */
    List<JobTranStateDTO> listTranState();

    /**
     * 删除所有的任务
     * @param delJobDTO
     * @return boolean
     */
    boolean deleteAllJob(DelJobDTO delJobDTO);

    /**
     * 任务执行日志查询
     * @param executeLogsParamDTO 查询条件
     * @return List<JobExecuteLogsDTO>
     */
    JobExecuteLogsDTO listExecuteLogs(JobExecuteLogsParamDTO executeLogsParamDTO);

    /**
     * 首页数据详情
     * @return DashboardDataDTO
     */
    DashboardDataDTO dashBoardData();
}
