package org.watermelon.watermelon_quartz;

import org.com.watermelon.watermelon_quartz.entity.dto.JobDetailDTO;
import org.com.watermelon.watermelon_quartz.util.QuartzUtil;
import org.junit.jupiter.api.Test;
import org.quartz.CronExpression;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


class WatermelonQuartzApplicationTests {
    @Test
    public void test() throws ParseException {
        CronExpression cronExp = new CronExpression("0/4 * * * * ?");
        Calendar nowDate = Calendar.getInstance();
        Calendar cronDate = Calendar.getInstance();
        Date cronNextValidTime = cronExp.getNextValidTimeAfter(new Date());
        nowDate.setTime(new Date());
        cronDate.setTime(cronNextValidTime);
        System.out.println( nowDate.get(Calendar.YEAR) == cronDate.get(Calendar.YEAR) &&
                nowDate.get(Calendar.DAY_OF_MONTH) == cronDate.get(Calendar.DAY_OF_MONTH) &&
                cronDate.get(Calendar.MONTH) == cronDate.get(Calendar.MONTH));
    }
}
