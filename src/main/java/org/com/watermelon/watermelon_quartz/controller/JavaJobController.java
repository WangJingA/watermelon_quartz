package org.com.watermelon.watermelon_quartz.controller;

import org.com.watermelon.watermelon_quartz.entity.dto.*;
import org.com.watermelon.watermelon_quartz.response.ResponseEnum;
import org.com.watermelon.watermelon_quartz.response.ServerResponseEntity;
import org.com.watermelon.watermelon_quartz.service.JavaJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/quartz")
public class JavaJobController {
    @Autowired
    JavaJobService javaJobService;
    @PostMapping("/add_java_job")
    public ServerResponseEntity addJavaJob(@RequestBody AddQuartzJobDTO addQuartzJobDTO) throws Exception {
        javaJobService.addJavaJob(addQuartzJobDTO);
        return  ServerResponseEntity.success();
    }

    @PostMapping("/list_job_details")
    public ServerResponseEntity listJobDetails(@RequestBody JobListParamDTO paramDTO){
        return  ServerResponseEntity.success(javaJobService.listJobDetails(paramDTO));
    }

    @PostMapping("/delete_job")
    public ServerResponseEntity deleteJob(@RequestBody OperationJobParamDTO operationJobParamDTO) throws Exception {
        return ServerResponseEntity.responseForState(javaJobService.deleteJob(operationJobParamDTO));
    }

    @PostMapping("/pause_job")
    public ServerResponseEntity pauseJob(@RequestBody OperationJobParamDTO operationJobParamDTO) throws Exception {
        return ServerResponseEntity.responseForState(javaJobService.pauseJob(operationJobParamDTO));
    }

    @PostMapping("/resume_job")
    public ServerResponseEntity resumeJob(@RequestBody OperationJobParamDTO operationJobParamDTO) throws Exception {
        return ServerResponseEntity.responseForState(javaJobService.resumeJob(operationJobParamDTO));
    }

    @PostMapping("/update_job")
    public ServerResponseEntity updateJob(@RequestBody AddQuartzJobDTO operationJobParamDTO) throws Exception {
        return ServerResponseEntity.responseForState(javaJobService.updateJob(operationJobParamDTO));
    }

    @PostMapping("/pause_all_job")
    public ServerResponseEntity pauseAllJob() throws Exception {
        return ServerResponseEntity.responseForState(javaJobService.pauseAllJob());
    }

    @PostMapping("/resume_all_job")
    public ServerResponseEntity resumeAllJob() throws Exception {
        return ServerResponseEntity.responseForState(javaJobService.resumeAllJob());
    }

    @PostMapping("/group_list")
    public ServerResponseEntity groupNameList(){
        return ServerResponseEntity.success(javaJobService.listGroupName());
    }


    @PostMapping("/job_state_list")
    public ServerResponseEntity jobStateList(){
        return ServerResponseEntity.success(javaJobService.listTranState());
    }

    @PostMapping("/delete_all_job")
    public ServerResponseEntity deleteAllJob(@RequestBody DelJobDTO delJobDTO){
        boolean isDel = javaJobService.deleteAllJob(delJobDTO);
        if (isDel){
            return ServerResponseEntity.success();
        }
        return ServerResponseEntity.fail(ResponseEnum.ERROR);
    }

    @PostMapping("/job_logs")
    public ServerResponseEntity jobLogs(@RequestBody JobExecuteLogsParamDTO jobExecuteLogsParamDTO) {
        return ServerResponseEntity.success(javaJobService.listExecuteLogs(jobExecuteLogsParamDTO));
    }

    @PostMapping("/dash_board")
    public ServerResponseEntity dashBoard(){
        return ServerResponseEntity.success(javaJobService.dashBoardData());
    }
}
