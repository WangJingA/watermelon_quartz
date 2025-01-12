package org.com.watermelon.watermelon_quartz.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.com.watermelon.watermelon_quartz.emun.QuartzStateEnum;
import org.com.watermelon.watermelon_quartz.entity.dto.*;
import org.com.watermelon.watermelon_quartz.entity.po.JobDetailPO;
import org.com.watermelon.watermelon_quartz.entity.po.JobTranStatePO;
import org.com.watermelon.watermelon_quartz.entity.po.UserJobPO;
import org.com.watermelon.watermelon_quartz.mapper.JobDetailOperationMapper;
import org.com.watermelon.watermelon_quartz.service.JavaJobService;
import org.com.watermelon.watermelon_quartz.util.CronExpParserUtil;
import org.com.watermelon.watermelon_quartz.util.QuartzUtil;
import org.quartz.CronExpression;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 调度中心调用java服务控制器服务实现类
 * @author watermelon
 * @date 2024/07/28
 */
@Slf4j
@Service
public class JavaJobServiceImpl implements JavaJobService {
    @Autowired
    JobDetailOperationMapper jobDetailOperationMapper;
    @Autowired
    QuartzUtil quartzUtil;
    /**
     * 新增调度任务
     * @param addQuartzJobDTO 调度任务参数
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addJavaJob(AddQuartzJobDTO addQuartzJobDTO) throws Exception {
        UserJobPO userJobPO = new UserJobPO();
        userJobPO.setJobName(addQuartzJobDTO.getJobName());
        userJobPO.setJobGroup(addQuartzJobDTO.getJobGroup());
        // TODO:用户先写死为1，接入网关后替换为sso单点登录用户
        userJobPO.setUserId("1");
        // 判断是否存在相同的任务
       List<UserJobPO> userJobPOList =  jobDetailOperationMapper.isExistJob(new UserJobPO()
               .setJobName(addQuartzJobDTO.getJobName()).setJobGroup(addQuartzJobDTO.getJobGroup()));
       if (CollUtil.isNotEmpty(userJobPOList)){
           throw new Exception("已经存在相同任务");
       }
        jobDetailOperationMapper.addJobUser(userJobPO);
        jobDetailOperationMapper.addJobDetail(addQuartzJobDTO);
        quartzUtil.addJob(addQuartzJobDTO);
    }

    @Override
    public JobDetailDTO listJobDetails(JobListParamDTO paramDTO) {
        List<JobDetailPO> jobDetailPOS = jobDetailOperationMapper.listJobDetail(paramDTO);
        List<JobDetailDTO.JobDetailInfoDTO> jobDetailDTOList = new ArrayList<>();
        JobDetailDTO jobDetailDTO = new JobDetailDTO();
        if (CollUtil.isNotEmpty(jobDetailPOS)){
            jobDetailPOS.forEach(item->{
                JobDetailDTO.JobDetailInfoDTO jobDetailInfoDTO = new JobDetailDTO.JobDetailInfoDTO();
                BeanUtils.copyProperties(item,jobDetailInfoDTO);
                jobDetailInfoDTO.setTriggerState(quartzUtil.getJobExecuteState(item.getJobName(),item.getJobGroupName()));
                jobDetailInfoDTO.setTriggerStateCnName(QuartzStateEnum.stateEnTranCn(quartzUtil.getJobExecuteState(item.getJobName(),item.getJobGroupName()))
                        .getStateCnMsg());
                jobDetailInfoDTO.setCornExpressionCn(CronExpParserUtil.translateToChinese(item.getCronExpression()));
                jobDetailDTOList.add(jobDetailInfoDTO);
            });
        }
        jobDetailDTO.setJobDetailInfoDTOList(jobDetailDTOList);
        jobDetailDTO.setTotal(CollUtil.isNotEmpty(jobDetailDTOList) ? jobDetailDTOList.size() : 0);
        // 任务状态查询
        List<JobDetailDTO.JobDetailInfoDTO> tempList = jobDetailDTOList;
        if (StrUtil.isNotBlank(paramDTO.getExecuteState()) && CollUtil.isNotEmpty(jobDetailDTOList)){
            tempList = jobDetailDTOList.stream().filter(item->paramDTO.getExecuteState().equalsIgnoreCase(item.getTriggerState()))
                    .collect(Collectors.toList());
        }
        // 进行分页
        if (CollUtil.isNotEmpty(tempList)){
            tempList = tempList.stream().skip(((paramDTO.getPage() > 0 ? paramDTO.getPage().intValue() -1 : 0) * paramDTO.getSize()))
                    .limit(paramDTO.getSize()).collect(Collectors.toList());
        }
        // 构建http请求详情
        tempList = buildHttpDetail(tempList);
        jobDetailDTO.setJobDetailInfoDTOList(tempList);
        return jobDetailDTO;
    }

    /**
     * 构建任务详情的http请求详情
     * @param jobDetailDTOList 任务详情
     * @return List<JobDetailDTO>
     */
    private List<JobDetailDTO.JobDetailInfoDTO> buildHttpDetail(List<JobDetailDTO.JobDetailInfoDTO> jobDetailDTOList){
        List<JobHttpDetailDTO> jobHttpDetailDTOS = jobDetailOperationMapper.listHttpJobDetail();
        if (CollUtil.isEmpty(jobHttpDetailDTOS)){
            return jobDetailDTOList;
        }
        Map<String, JobHttpDetailDTO> httpJobMap = jobHttpDetailDTOS.stream()
                .collect(Collectors.toMap(item -> item.getJobName() + "-" + item.getJobGroupName(),
                        Function.identity(),
                        (k,v)->v));
        if (CollUtil.isNotEmpty(jobDetailDTOList)) {
            jobDetailDTOList.forEach(item->{
                item.setJobHttpDetailDTO(httpJobMap.get(item.getJobName() + "-" + item.getJobGroupName()));
            });
        }
        return jobDetailDTOList;
    }

    @Override
    public boolean deleteJob(OperationJobParamDTO operationJobParamDTO) {
        jobDetailOperationMapper.delUserJob(operationJobParamDTO.getJobName(), operationJobParamDTO.getJobGroupName());
        return  quartzUtil.deleteJob(operationJobParamDTO.getJobName(),operationJobParamDTO.getJobGroupName());
    }

    @Override
    public boolean pauseJob(OperationJobParamDTO operationJobParamDTO) {
        return quartzUtil.pauseJob(operationJobParamDTO.getJobName(),operationJobParamDTO.getJobGroupName());
    }

    @Override
    public boolean resumeJob(OperationJobParamDTO operationJobParamDTO) {
       return quartzUtil.resumeJob(operationJobParamDTO.getJobName(),operationJobParamDTO.getJobGroupName());
    }

    @Override
    public boolean updateJob(AddQuartzJobDTO operationJobParamDTO) {
        List<UserJobPO> existJob = jobDetailOperationMapper.isExistJob(new UserJobPO().setJobName(operationJobParamDTO.getJobName())
                .setJobGroup(operationJobParamDTO.getJobGroup()));
        if (CollUtil.isEmpty(existJob)){
            throw new RuntimeException("任务不存在");
        }
        jobDetailOperationMapper.updateJobDetail(operationJobParamDTO);
         return quartzUtil.reScheduleJob(
                 operationJobParamDTO.getRequestWay(),
                 operationJobParamDTO.getRequestMethod(),
                 operationJobParamDTO.getJobDataMap(),
                 operationJobParamDTO.getJobDescription(),
                 operationJobParamDTO.getJobName(),
                 operationJobParamDTO.getJobGroup(),
                 operationJobParamDTO.getJobCron());
    }

    @Override
    public boolean resumeAllJob() {
       return quartzUtil.resumeAllJob();
    }

    @Override
    public boolean pauseAllJob() {
        return quartzUtil.pauseAllJob();
    }

    @Override
    public List<String> listGroupName() {
        return jobDetailOperationMapper.listJobGroup();
    }

    @Override
    public List<JobExecuteStateDTO> listExecuteState() {
        List<JobExecuteStateDTO> executeStateList = new ArrayList<>();
        for (QuartzStateEnum quartzStateEnum : QuartzStateEnum.values()){
            JobExecuteStateDTO executeStateDTO = new JobExecuteStateDTO();
            executeStateDTO.setStateEnStr(quartzStateEnum.getStateEnMsg())
                    .setStateCnStr(quartzStateEnum.getStateCnMsg());
            executeStateList.add(executeStateDTO);
        }
        return executeStateList;
    }

    @Override
    public List<JobTranStateDTO> listTranState() {
        List<JobTranStatePO> jobTranStatePOS = jobDetailOperationMapper.jobTranState();
        List<JobTranStateDTO> jobTranStateDTOList = new ArrayList<>();
        if (CollUtil.isNotEmpty(jobTranStatePOS)){
            jobTranStatePOS.forEach(statePO->{
                JobTranStateDTO jobTranStateDTO = new JobTranStateDTO();
                BeanUtils.copyProperties(statePO,jobTranStateDTO);
                jobTranStateDTOList.add(jobTranStateDTO);
            });
        }
        return jobTranStateDTOList;
    }

    @Override
    public boolean deleteAllJob(DelJobDTO delJobDTO) {
        List<DelJobDTO.JobInfo> jobInfoList = delJobDTO.getJobInfoList();
        return quartzUtil.deleteAllJob(jobInfoList);
    }

    @Override
    public JobExecuteLogsDTO listExecuteLogs(JobExecuteLogsParamDTO executeLogsParamDTO) {
        List<JobExecuteLogsInfoDTO> jobExecuteLogsDTOS = jobDetailOperationMapper.jobExecuteLogs(executeLogsParamDTO);
        JobExecuteLogsDTO jobExecuteLogsDTO = new JobExecuteLogsDTO();
        if (CollUtil.isNotEmpty(jobExecuteLogsDTOS)){
            jobExecuteLogsDTO.setTotal(jobExecuteLogsDTOS.size());
            jobExecuteLogsDTOS = jobExecuteLogsDTOS.stream().skip(((executeLogsParamDTO.getPage() > 0 ? executeLogsParamDTO.getPage().intValue() -1 : 0) * executeLogsParamDTO.getSize()))
                    .limit(executeLogsParamDTO.getSize()).collect(Collectors.toList());
            jobExecuteLogsDTO.setJobExecuteLogsInfoDTOS(jobExecuteLogsDTOS);
        }
        return jobExecuteLogsDTO;
    }


    /**
     * 首页数据详情
     * @return DashboardDataDTO
     */
    @Override
    public DashboardDataDTO dashBoardData() {
        // 初始化数据
        DashboardDataDTO dashboardDataDTO = initDashboardData();
        // 查询数据库任务数据
        List<JobDetailPO> jobDetailPOS = jobDetailOperationMapper.listJobDetail(new JobListParamDTO());
        if (CollUtil.isEmpty(jobDetailPOS)){
            return dashboardDataDTO;
        }
        // 查询当天任务详情
        List<DashboardDataDTO.NowDayExecuteTask> nowDayExecuteTasks = buildNowDayExecuteTasks(jobDetailPOS);
        dashboardDataDTO.setNowDayExecuteTasks(nowDayExecuteTasks);
        // 构建任务占比和数量
        buildTaskAmountAndPercent(dashboardDataDTO,jobDetailPOS);
        return dashboardDataDTO;
    }

    /**
     * 构建节目任务百分比和任务数量
     * @param dashboardDataDTO
     * @param jobDetailPOS
     */
    private void buildTaskAmountAndPercent(DashboardDataDTO dashboardDataDTO,List<JobDetailPO> jobDetailPOS){
        AtomicInteger executingTaskAmount = new AtomicInteger(0);
        AtomicInteger waitingTaskAmount = new AtomicInteger(0);
        AtomicInteger blockedTaskAmount = new AtomicInteger(0);
        AtomicInteger wrongTaskAmount = new AtomicInteger(0);
        AtomicInteger pauseTaskAmount = new AtomicInteger(0);
        double executingTaskPercent = (double) 0;
        double waitingTaskPercent = (double) 0;
        double blockedTaskPercent = (double) 0;
        double wrongTaskPercent = (double) 0;
        double pauseTaskPercent = (double) 0;
        jobDetailPOS.forEach(item->{
            switch (quartzUtil.getJobExecuteState(item.getJobName(),item.getJobGroupName())){
                case "NORMAL" :
                    executingTaskAmount.addAndGet(1);
                    break;
                case "WAITING" :
                    waitingTaskAmount.addAndGet(1);
                    break;
                case "BLOCKED" :
                    blockedTaskAmount.addAndGet(1);
                    break;
                case "ERROR" :
                    wrongTaskAmount.addAndGet(1);
                    break;
                case "PAUSED" :
                    pauseTaskAmount.addAndGet(1);
                    break;
            }
        });
        Integer totalTaskPercent = (executingTaskAmount.get() + wrongTaskAmount.get() +
                blockedTaskAmount.get() + wrongTaskAmount.get() + pauseTaskAmount.get());
        executingTaskPercent = executingTaskAmount.get() == 0 ? 0 : ((double) executingTaskAmount.get() / totalTaskPercent) * 100;
        waitingTaskPercent = wrongTaskAmount.get() == 0 ? 0 : ((double) wrongTaskAmount.get() / totalTaskPercent) * 100;
        blockedTaskPercent = blockedTaskAmount.get() == 0 ? 0 : ((double) blockedTaskAmount.get() / totalTaskPercent)* 100;
        wrongTaskPercent = wrongTaskAmount.get() == 0 ? 0 : ((double) wrongTaskAmount.get() / totalTaskPercent) * 100;
        pauseTaskPercent = pauseTaskAmount.get() == 0 ? 0 : ((double) pauseTaskAmount.get() / totalTaskPercent) * 100;
        dashboardDataDTO.setExecutingTaskAmount(executingTaskAmount.toString())
                .setWrongTaskAmount(waitingTaskAmount.toString())
                .setBlockedTaskAmount(blockedTaskAmount.toString())
                .setWaitingTaskAmount(waitingTaskAmount.toString())
                .setPauseTaskAmount(pauseTaskAmount.toString())
                .setWaitingTaskPercentage(Double.toString(waitingTaskPercent))
                .setExecutingTaskPercentage(Double.toString(executingTaskPercent))
                .setBlockedTaskPercentage(Double.toString(blockedTaskPercent))
                .setWrongTaskPercentage(Double.toString(wrongTaskPercent))
                .setPauseTaskPercentage(Double.toString(pauseTaskPercent));
    }


    /**
     * 初始化数据
     * @return
     */
    private DashboardDataDTO initDashboardData(){
        DashboardDataDTO dashboardDataDTO = new DashboardDataDTO();
        String initStr = "0";
        dashboardDataDTO.setBlockedTaskAmount(initStr)
                .setExecutingTaskAmount(initStr)
                .setWaitingTaskAmount(initStr)
                .setWaitingTaskPercentage(initStr)
                .setPauseTaskPercentage(initStr)
                .setWrongTaskPercentage(initStr)
                .setBlockedTaskPercentage(initStr)
                .setExecutingTaskPercentage(initStr);
        return dashboardDataDTO;
    }

    /**
     * 构建当天执行任务详情
     * @param jobDetailPOS
     * @return
     */
    private List<DashboardDataDTO.NowDayExecuteTask> buildNowDayExecuteTasks(List<JobDetailPO> jobDetailPOS){
        List<JobDetailPO> nowDateCronList = jobDetailPOS.stream().filter(item -> {
            try {
                return judgeCronExpressionIsCurrentDate(item.getCronExpression());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
        if (CollUtil.isEmpty(nowDateCronList)){
            return new ArrayList<>();
        }
        // 构建当天执行任务详情
        List<DashboardDataDTO.NowDayExecuteTask> nowDayExecuteTasks = new ArrayList<>();
        nowDateCronList.forEach(item->{
            DashboardDataDTO.NowDayExecuteTask nowDayExecuteTask = new DashboardDataDTO.NowDayExecuteTask();
            nowDayExecuteTask.setExecuteDate(item.getNextFireTime())
                    .setExecuteTaskName(item.getJobName())
                    .setExecuteNextTime(item.getNextFireTime())
                    .setExecuteMemo(CronExpParserUtil.translateToChinese(item.getCronExpression()));
            nowDayExecuteTasks.add(nowDayExecuteTask);
        });
        return nowDayExecuteTasks;
    }

    /**
     * 判断当前日期是否在cron表达式时间区间内
     * @param cronExpression
     * @return
     * @throws ParseException
     */
    private boolean judgeCronExpressionIsCurrentDate(String cronExpression) throws ParseException {
        CronExpression cronExp = new CronExpression(cronExpression);
        Calendar nowDate = Calendar.getInstance();
        Calendar cronDate = Calendar.getInstance();
        Date cronNextValidTime = cronExp.getNextValidTimeAfter(new Date());
        nowDate.setTime(new Date());
        cronDate.setTime(cronNextValidTime);
        return nowDate.get(Calendar.YEAR) == cronDate.get(Calendar.YEAR) &&
                nowDate.get(Calendar.DAY_OF_MONTH) == cronDate.get(Calendar.DAY_OF_MONTH) &&
                cronDate.get(Calendar.MONTH) == cronDate.get(Calendar.MONTH);
    }
}
