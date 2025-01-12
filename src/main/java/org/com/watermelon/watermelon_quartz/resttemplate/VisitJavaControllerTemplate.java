package org.com.watermelon.watermelon_quartz.resttemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.com.watermelon.watermelon_quartz.emun.RequestWayEnum;
import org.com.watermelon.watermelon_quartz.entity.bo.VisitJavaControllerBO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.util.Objects;

@Slf4j
public class VisitJavaControllerTemplate {

    private final RestTemplate restTemplate;
    // 注入实例
    public VisitJavaControllerTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * RestTemplate访问java接口
     * @param visitJavaControllerBO 参数类
     */
    public <T> void sendRequest(VisitJavaControllerBO visitJavaControllerBO) throws JsonProcessingException {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        ObjectMapper objectMapper = new ObjectMapper();
        Object jsonObject = objectMapper.readValue(visitJavaControllerBO.getParam().replaceAll("\\\\",""), Object.class);
        HttpEntity<T> requestEntity = (HttpEntity<T>) new HttpEntity<>(jsonObject,httpHeaders);
        // POST请求
        ResponseEntity<String> stringResponseEntity = null;
            if (RequestWayEnum.POST_REQUEST.getRequestWay().equalsIgnoreCase(visitJavaControllerBO.getRequestWay())) {
                stringResponseEntity = restTemplate.postForEntity(visitJavaControllerBO.getVisitUrl(), requestEntity, String.class);
            }
            if (RequestWayEnum.GET_REQUEST.getRequestWay().equalsIgnoreCase(visitJavaControllerBO.getRequestWay())) {
                stringResponseEntity = restTemplate.getForEntity(visitJavaControllerBO.getVisitUrl(), String.class);
            }
        log.info("请求java接口:{}结束",visitJavaControllerBO.getVisitUrl());
        if (Objects.nonNull(stringResponseEntity)) {
            log.info("请求返回信息:{}", stringResponseEntity.getBody());
        }
    }
}
