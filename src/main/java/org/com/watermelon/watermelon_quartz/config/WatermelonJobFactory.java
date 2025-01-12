package org.com.watermelon.watermelon_quartz.config;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.AdaptableJobFactory;
import org.springframework.stereotype.Component;

/**
 * 将Spring的对象注入到Quartz Job 2,将Task注册为Bean
 * @author watermelon
 * @date 2024/07/28
 */
@Component
public class WatermelonJobFactory extends AdaptableJobFactory {
    @Autowired
    private AutowireCapableBeanFactory capableBeanFactory;

    @Override
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        //调用父类的方法
        Object jobInstance = super.createJobInstance(bundle);
        // 使我们的JOB注入到Spring容器
        capableBeanFactory.autowireBean(jobInstance);
        return jobInstance;
    }
}

