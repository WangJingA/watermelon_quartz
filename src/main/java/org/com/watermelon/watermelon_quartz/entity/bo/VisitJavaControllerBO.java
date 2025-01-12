package org.com.watermelon.watermelon_quartz.entity.bo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * 任务中心调度java接口参数
 * @author watermelon
 * @date 2024/07/28
 */
@Data
@Accessors(chain = true)
public class VisitJavaControllerBO {
    /**
     * 调度地址
     */
    private String visitUrl;
    /**
     * 请求方式：POST请求或者GET请求
     */
    private String requestWay;
    /**
     * 请求参数
     */
    private String param;
}
