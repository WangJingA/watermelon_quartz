package org.com.watermelon.watermelon_quartz.exception;


import lombok.extern.slf4j.Slf4j;
import org.com.watermelon.watermelon_quartz.response.ResponseEnum;
import org.com.watermelon.watermelon_quartz.response.ServerResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常返回类
 * @author watermelon
 * @date 2024/08/13
 */
@Slf4j
@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(WatermelonBusinessException.class)
    public ServerResponseEntity watermelonBusinessException(WatermelonBusinessException businessException){
        log.error(businessException.getMessage());
        return ServerResponseEntity.fail(ResponseEnum.ERROR,businessException.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ServerResponseEntity runTimeException(RuntimeException e){
        log.error(e.getMessage());
        return ServerResponseEntity.fail(ResponseEnum.ERROR,e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ServerResponseEntity exception(Exception e){
        log.error(e.getMessage());
        return ServerResponseEntity.fail(ResponseEnum.ERROR,e.getMessage());
    }
}
