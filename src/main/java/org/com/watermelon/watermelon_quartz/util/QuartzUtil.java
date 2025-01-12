package org.com.watermelon.watermelon_quartz.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.com.watermelon.watermelon_quartz.emun.QuartzStateEnum;
import org.com.watermelon.watermelon_quartz.entity.dto.AddQuartzJobDTO;
import org.com.watermelon.watermelon_quartz.entity.dto.DelJobDTO;
import org.com.watermelon.watermelon_quartz.entity.dto.JobDetailDTO;
import org.com.watermelon.watermelon_quartz.job.RequestJavaControllerJob;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 任务工具类
 * @author watermelon
 * @date 2024/07/28
 */
@Slf4j
@Component
public class QuartzUtil {
    @Autowired
    private Scheduler scheduler;

    public List<JobDetailDTO.JobDetailInfoDTO> getAllJobs() {
        List<JobDetailDTO.JobDetailInfoDTO> jobInfoList = new ArrayList<>();
        try{
            List<String> groupList = scheduler.getJobGroupNames();
            for(String group : groupList){
                GroupMatcher<JobKey> groupMatcher = GroupMatcher.groupEquals(group);
                Set<JobKey> jobKeySet = scheduler.getJobKeys(groupMatcher);
                for(JobKey jobKey : jobKeySet){
                    JobDetailDTO.JobDetailInfoDTO jobInfo = new JobDetailDTO.JobDetailInfoDTO();
                    JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                    jobInfo.setJobName(jobKey.getName());
                    jobInfo.setJobGroupName(jobKey.getGroup());
                    jobInfo.setJobClassName(jobDetail.getJobClass().getName());
                    Trigger trigger = scheduler.getTrigger(TriggerKey.triggerKey(jobKey.getName(), jobKey.getGroup()));
                    if(trigger != null){
                        Trigger.TriggerState state = scheduler.getTriggerState(TriggerKey.triggerKey(jobKey.getName(), jobKey.getGroup()));
                        jobInfo.setTriggerName(jobKey.getName());
                        jobInfo.setTriggerGroupName(jobKey.getGroup());
                        try{
                            CronTrigger cronTrigger = (CronTrigger) trigger;
                            jobInfo.setCronExpression(cronTrigger.getCronExpression());
                        } catch (Exception e){
                            log.error(e.getMessage());
                        }
                        if(trigger.getNextFireTime() != null){
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            jobInfo.setNextFireTime(sdf.format(trigger.getNextFireTime()));
                        }
                        jobInfo.setJobDescription(jobDetail.getDescription());
                        jobInfo.setTriggerState(state.name());
                        jobInfoList.add(jobInfo);
                    } else {
                        jobInfo.setTriggerState("OVER");
                        jobInfoList.add(jobInfo);
                    }
                }
            }
        } catch (Exception e){
            log.error(e.getMessage());
        }
        return jobInfoList;
    }

    public String getJobExecuteState(String jobName,String jobGroupName){
        String jobState = "";
        try {
            Trigger.TriggerState triggerState = scheduler.getTriggerState(TriggerKey.triggerKey(jobName, jobGroupName));
            jobState = triggerState.name();
        }catch (Exception e){
            log.error("获取任务状态失败",e);
        }
        return jobState;
    }


    public boolean resumeJob(String jobName, String jobGroup) {
        boolean flag = true;
        try{
            scheduler.resumeJob(JobKey.jobKey(jobName, jobGroup));
        } catch (Exception e){
            flag = false;
            log.error(e.getMessage());
        }
        return flag;
    }

    public boolean resumeAllJob() {
        boolean flag = true;
        try{
            scheduler.resumeAll();
        } catch (Exception e){
            flag = false;
            log.error(e.getMessage());
        }
        return flag;
    }


    public boolean pauseJob(String jobName, String jobGroup) {
        boolean flag = true;
        try{
            scheduler.pauseJob(JobKey.jobKey(jobName, jobGroup));
        } catch (Exception e){
            flag = false;
            log.error(e.getMessage());
        }
        return flag;
    }

    public boolean pauseAllJob() {
        boolean flag = true;
        try{
            scheduler.pauseAll();
        } catch (Exception e){
            flag = false;
            log.error(e.getMessage());
        }
        return flag;
    }


    public boolean reScheduleJob(String requestWay,String requestUrl,String param,String description,
                                 String jobName, String jobGroup, String cronExpression) {
        boolean flag = true;
        try{
            JobDetail jobDetail = scheduler.getJobDetail(JobKey.jobKey(jobName, jobGroup));
            jobDetail.getJobDataMap().put("requestUrl",requestUrl);
            jobDetail.getJobDataMap().put("param",param);
            jobDetail.getJobDataMap().put("requestWay",requestWay);
            Trigger.TriggerState state = scheduler.getTriggerState(TriggerKey.triggerKey(jobName, jobGroup));
            boolean triggerStateIsNormal = QuartzStateEnum.STATE_NORMAL.stateEnMsg.equalsIgnoreCase(state.name()) ?
                    true : false;
            CronTrigger cronTrigger = (CronTrigger) scheduler.getTrigger(TriggerKey.triggerKey(jobName, jobGroup));
            if(cronTrigger != null && StrUtil.isNotBlank(cronExpression)){
                CronTrigger cronTriggerNew = TriggerBuilder.newTrigger()
                        .withDescription(description)
                        .forJob(jobDetail)
                        .withIdentity(jobName, jobGroup)
                        .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                        .build();
                scheduler.rescheduleJob(TriggerKey.triggerKey(jobName, jobGroup), cronTriggerNew);
                if(triggerStateIsNormal){
                    this.resumeJob(jobName, jobGroup);
                } else {
                    this.pauseJob(jobName,jobGroup);
                }
            }
        } catch (Exception e){
            flag = false;
            log.error(e.getMessage());
        }
        return flag;
    }


    public boolean deleteJob(String jobName, String jobGroup) {
        boolean flag = true;
        try{
            List<? extends Trigger> triggerList = scheduler.getTriggersOfJob(JobKey.jobKey(jobName, jobGroup));
            if(triggerList.size() > 0){
                if(!"PAUSED".equals(scheduler.getTriggerState(TriggerKey.triggerKey(jobName, jobGroup)).name())){
                    scheduler.pauseTrigger(TriggerKey.triggerKey(jobName, jobGroup));
                }
                scheduler.unscheduleJob(TriggerKey.triggerKey(jobName, jobGroup));
            }
            scheduler.deleteJob(JobKey.jobKey(jobName, jobGroup));
        } catch (Exception e){
            flag = false;
            log.error(e.getMessage());
        }
        return flag;
    }

    public boolean deleteAllJob(List<DelJobDTO.JobInfo> jobInfoList) {
        AtomicBoolean flag = new AtomicBoolean(true);
        if (CollUtil.isNotEmpty(jobInfoList)) {
            jobInfoList.forEach(job -> {
                try {
                    List<? extends Trigger> triggerList = scheduler.getTriggersOfJob(JobKey.jobKey(job.getJobName(), job.getGroupName()));
                    if (triggerList.size() > 0) {
                        if (!"PAUSED".equals(scheduler.getTriggerState(TriggerKey.triggerKey(job.getJobName(), job.getGroupName())).name())) {
                            scheduler.pauseTrigger(TriggerKey.triggerKey(job.getJobName(), job.getGroupName()));
                        }
                        scheduler.unscheduleJob(TriggerKey.triggerKey(job.getJobName(), job.getGroupName()));
                    }
                    scheduler.deleteJob(JobKey.jobKey(job.getJobName(), job.getGroupName()));
                } catch (Exception e) {
                    flag.set(false);
                    log.error(e.getMessage());
                }
            });
        }
        return flag.get();
    }


    public boolean addJob(AddQuartzJobDTO jobInfo) {
        int isJobExist = this.isJobExist(JobKey.jobKey(jobInfo.getJobName(), jobInfo.getJobGroup()));
        if(isJobExist == 1){
            throw new RuntimeException("任务已存在！");
        }
        try{
            JobDetail jobDetail = null;
            if(isJobExist == 0){
                jobDetail = scheduler.getJobDetail(JobKey.jobKey(jobInfo.getJobName(), jobInfo.getJobGroup()));
                jobDetail.getJobDataMap().put("requestUrl",jobInfo.getRequestMethod());
                jobDetail.getJobDataMap().put("param",jobInfo.getJobDataMap());
                jobDetail.getJobDataMap().put("requestWay",jobInfo.getRequestWay());
            } else if(isJobExist == -1){
                jobDetail = JobBuilder.newJob( RequestJavaControllerJob.class)
                        .requestRecovery(true)
                        .withIdentity(jobInfo.getJobName(), jobInfo.getJobGroup())
                        .withDescription(jobInfo.getJobDescription())
                        .storeDurably().build();
                jobDetail.getJobDataMap().put("requestUrl",jobInfo.getRequestMethod());
                jobDetail.getJobDataMap().put("param",jobInfo.getJobDataMap());
                jobDetail.getJobDataMap().put("requestWay",jobInfo.getRequestWay());
            }
            //如果jobInfo的cron表达式为空，则创建常规任务，反之创建周期任务
            if(jobInfo.getJobCron() != null){
                CronTrigger cronTrigger = TriggerBuilder.newTrigger()
                        .withIdentity(jobInfo.getJobName(), jobInfo.getJobGroup())
                        .forJob(jobDetail)
                        .withDescription(jobInfo.getJobDescription())
                        .withSchedule(CronScheduleBuilder.cronSchedule(jobInfo.getJobCron()))
                        .startNow()
                        .build();
                scheduler.scheduleJob(jobDetail, cronTrigger);
            } else {
                Trigger trigger = TriggerBuilder.newTrigger().withIdentity(jobInfo.getJobName(), jobInfo.getJobGroup())
                        .withSchedule(SimpleScheduleBuilder.simpleSchedule().withRepeatCount(0))
                        .forJob(jobDetail)
                        .startAt(DateBuilder.futureDate(0, DateBuilder.IntervalUnit.SECOND))
                        .build();
                scheduler.scheduleJob(jobDetail, trigger);
            }
        } catch (SchedulerException e){
            log.error(e.getMessage());
        } catch (Exception e){
            log.error(e.getMessage());

        }
        // 立即启动，即使前面已经使用了暂停所有任务的操作还是能立即启动
        resumeJob(jobInfo.getJobName(),jobInfo.getJobGroup());
        return true;
    }


    public int isJobExist(JobKey jobKey) {
        int flag = -1;
        try{
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            List<? extends  Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
            if(jobDetail != null && triggers.size() > 0){
                flag = 1;
            } else if(jobDetail != null && triggers.size() == 0){
                flag = 0;
            }
        } catch (Exception e){
            flag = -1;
            log.error(e.getMessage());
        }
        return flag;
    }
}
