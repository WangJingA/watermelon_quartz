package org.com.watermelon.watermelon_quartz.job;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.com.watermelon.watermelon_quartz.entity.bo.VisitJavaControllerBO;
import org.com.watermelon.watermelon_quartz.entity.dto.JobHttpDetailDTO;
import org.com.watermelon.watermelon_quartz.entity.po.JobLogPO;
import org.com.watermelon.watermelon_quartz.mapper.JobDetailOperationMapper;
import org.com.watermelon.watermelon_quartz.resttemplate.VisitJavaControllerTemplate;
import org.quartz.*;
import org.springframework.web.client.RestTemplate;

import java.util.Date;


/**
 * 调度任务执行类
 * 不允许并发执行使用注解：@DisallowConcurrentExecution
 * 允许并发执行使用注解：@PersistJobDataAfterExecution
 * @author watermelon
 * @date 2024/08/04
 */
@Slf4j
public class RequestJavaControllerJob implements Job {
    @Resource
    JobDetailOperationMapper jobDetailOperationMapper;

    @Override
    public void execute(JobExecutionContext context) {
        log.info("任务开始执行");
        JobDetail jobDetail = context.getJobDetail();
        JobHttpDetailDTO jobHttpDetailDTO = jobDetailOperationMapper.queryHttpJobDetail(jobDetail.getKey().getName(),
                jobDetail.getKey().getGroup());
        String requestUrl = jobHttpDetailDTO.getRequestMethod();
        String requestWay = jobHttpDetailDTO.getRequestWay();
        log.info("名字:{}" , jobDetail.getKey().getName());
        log.info("组名:{}" , jobDetail.getKey().getGroup());
        log.info("参数:{}" , jobDetail.getJobDataMap().toString());
        log.info("类名:{}" , jobDetail.getJobClass().getName());
        log.info("开始调用JAVA接口任务:{}",requestUrl);
        log.info("请求方式:{}",requestWay);
        log.info("本次执行的时间为:{}" , context.getFireTime());
        VisitJavaControllerTemplate visitJavaControllerTemplate = new VisitJavaControllerTemplate(new RestTemplate());
        VisitJavaControllerBO visitJavaControllerBO = new VisitJavaControllerBO()
                .setVisitUrl(requestUrl)
                .setRequestWay(requestWay)
                 .setParam(jobHttpDetailDTO.getRequestParam());
        try {
            visitJavaControllerTemplate.sendRequest(visitJavaControllerBO);
            String msg = "【" + jobDetail.getKey().getName() + "】任务执行成功>> 调用的接口是：" + requestUrl + "【" + requestWay + "】" +
                    "请求的方式是：" + requestWay
                    + "调用的参数是：" + jobHttpDetailDTO.getRequestParam();
            log.info(msg);
            jobDetailOperationMapper.addJobLogs(new JobLogPO().setJobName(jobDetail.getKey().getName())
                    .setExecuteState("1").setExecuteTime(new Date()).setJobGroup(jobDetail.getKey().getGroup()).setLogMsg(msg));
        } catch (Exception e){
            // 插入异常信息
            String msg = "【" + jobDetail.getKey().getName() + "】任务执行失败>> 错误信息：";
            log.error(msg,e);
            jobDetailOperationMapper.addJobLogs(new JobLogPO().setJobName(jobDetail.getKey().getName())
                    .setJobGroup(jobDetail.getKey().getGroup()).setExecuteState("0").setExecuteTime(new Date()).setLogMsg(msg + e.getMessage()));
        }
        log.info("下次执行的时间为:{}" , context.getNextFireTime());
        log.info("任务执行完毕");
        log.info("============================");
    }
}
