package org.com.watermelon.watermelon_quartz.emun;


/**
 * 请求方式枚举类
 * @author watermelon
 * @date 2024/07/28
 */
public enum RequestWayEnum {
    /**
     * POST请求
     */
    POST_REQUEST("POST","POST"),
    /**
     * GET请求
     */
    GET_REQUEST("GET","GET");

    public String getRequestCode() {
        return requestCode;
    }

    public String getRequestWay() {
        return requestWay;
    }

    final String requestCode;

    final String requestWay;
     RequestWayEnum(String requestCode,String requestWay){
        this.requestCode = requestCode;
        this.requestWay = requestWay;
    }
}
