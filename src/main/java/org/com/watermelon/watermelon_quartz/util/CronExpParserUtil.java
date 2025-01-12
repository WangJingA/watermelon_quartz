package org.com.watermelon.watermelon_quartz.util;


import com.alibaba.cloud.commons.lang.StringUtils;
import org.com.watermelon.watermelon_quartz.emun.WeekEnum;

/**
 * @author watermelon
 * @date 2024/08/27
 */
public class CronExpParserUtil {
    protected final static String[] CRON_TIME_CN = new String[]{"秒", "分", "点", "天", "月", "周", "年"};
    private final static Integer HOURS = 24;
    private final static Integer TIMESCALE = 60;
    private final static Integer TWO = 2;
    private static final String CHAR_STAR = "*";
    private static final String CHAR_QUE = "?";
    private static final String CHAR_ZERO = "0";
    private static final String CHAR_SLASH = "/";
    private static final String CHAR_L = "L";
    private static final String CHAR_W = "W";
    private static final String CHAR_SPILT = "-";
    private static final String CHAR_WELL = "#";

    private static final String CHAR_COMMA = ",";
    private static final Integer SIX = 6;
    private static final Integer SEVEN = 7;
    private static final String WEEK = "星期";

    /**
     * cron转中文.
     *
     * @param cronExp 表达式
     * @return 中文
     */
    public static String translateToChinese(String cronExp) {
        return translateToChinese(cronExp, CRON_TIME_CN);
    }

    /**
     * 主要解析 斜杆 /：表示起始时间开始触发，然后每隔固定时间触发一次。 范围值+开始值 - 间隔值 = 范围内最后执行的值；
     * 例如在Hourss域使用3/4,则意味着从第3小时到24+3-4：23小时范围内，第3小时开始触发第一次，然后每隔4小时，即7，11，15，19，23小时等分别触发一次。
     * 例如在Minutes域使用5/20,则意味着从第5分钟到60+5-20：45分范围内，第5分钟开始触发第一次，然后每隔20分钟，即25，45分钟等分别触发一次。
     * 例如在Seconds域使用8/10,则意味着从第8秒到60+8-10：58秒范围内，第8秒开始触发第一次，然后每隔10秒，即18，28，38，48，58秒等分别触发一次。
     * * 对于 *：表示匹配该域的任意值。例如在Minutes域使用*, 即表示每分钟都会触发事件。 对于问号
     * ?：只能用在DayofMonth和DayofWeek两个域，其作用为不指定 对于
     * -：表示范围。例如在Minutes域使用5-20，表示从5分到20分钟每分钟触发一次。直接进行拼接 对于逗号
     * ,：表示列出枚举值。例如在Minutes域使用5,20 ， 则意味着在5和20分每分钟触发一次。
     * 对于L：表示最后，只能出现在DayofWeek和DayofMonth域。 如果出现在DayofMonth域，只能使用L，表示当月最后一天
     * 如果在DayofWeek域 使用数字（1-7）或L（和7的作用一样表示每周的最后一天周六），比如数字"5"表示每周4， "7"或"L"表示每周六
     * 使用数字（1-7）结合L，表示当月最后一周的周几，比如"5L" 表示在最后的一周的周四； "3L" 表示最后一周的周二 对于#:
     * 用于确定每个月第几个星期几，只能出现在DayofMonth域。 例如 "4#2" 表示某月的第二个星期三（4表示星期三，2表示第二周）;
     * “6#3”表示本月第三周的星期五（6表示星期五，3表示第三周）; “2#1”表示本月第一周的星期一; “4#5”表示第五周的星期三。
     *
     * @param cronExp     表达式
     * @param cronTimeArr 时间中文单位
     * @return cron中文表达式
     */
    public static String translateToChinese(String cronExp, String[] cronTimeArr) {
        if (cronExp == null || cronExp.length() < 1) {
            return "cron表达式为空";
        }

        String[] tmpCorns = cronExp.split(" ");
        StringBuffer sBuffer = new StringBuffer();
        if (tmpCorns.length == SIX || tmpCorns.length == SEVEN) {
            if (tmpCorns.length == SEVEN) {
                // 解析年 Year
                String year = tmpCorns[6];
                if ((!CHAR_STAR.equals(year) && !year.equals(CHAR_QUE))) {
                    sBuffer.append(year).append(cronTimeArr[6]);
                }
            }
            // 解析月 Month 主要解析 /
            String months = tmpCorns[4];
            CronExpParserUtil.parseMonths(sBuffer, months, cronTimeArr);
            // 解析周 DayofWeek 主要解析 , - 1~7/L 1L~7L
            String dayOfWeek = tmpCorns[5];
            CronExpParserUtil.parseDayOfWeek(sBuffer, dayOfWeek, cronTimeArr);
            // 解析日 days -- DayofMonth 需要解析的 / # L W
            // * “6#3”表示本月第三周的星期五（6表示星期五，3表示第三周）;
            // * “2#1”表示本月第一周的星期一;
            // * “4#5”表示第五周的星期三。
            String days = tmpCorns[3];
            CronExpParserUtil.parseDays(sBuffer, days, cronTimeArr, dayOfWeek, months, tmpCorns);
            // 解析时 Hours 主要解析 /
            String hours = tmpCorns[2];
            CronExpParserUtil.parseHours(sBuffer, hours, cronTimeArr);
            // 解析分 Minutes 主要解析 /
            String minutes = tmpCorns[1];
            CronExpParserUtil.parseMin(sBuffer, minutes, cronTimeArr);
            // 解析秒 Seconds 主要解析 /
            String seconds = tmpCorns[0];
            CronExpParserUtil.parseSeconds(sBuffer, seconds, cronTimeArr);
            if (sBuffer.toString().length() > 0) {
                sBuffer.append("执行");
            } else {
                sBuffer.append("表达式中文转换异常");
            }
        }
        return sBuffer.toString();
    }

    private static void parseSeconds(StringBuffer sBuffer, String seconds, String[] cronTimeArr) {
        if (!seconds.equals(CHAR_STAR)) {
            if (seconds.contains(CHAR_SLASH)) {
                // sBuffer.append("内，从第").append(seconds.split("/")[0]).append("秒开始").append(",每")
                // .append(seconds.split("/")[0]).append(cronTimeArr[0]);
                sBuffer.append(appendGapInfo(seconds, TIMESCALE, 0));
            } else if (!CHAR_ZERO.equals(seconds)) {
                sBuffer.append(seconds).append(cronTimeArr[0]);
            }
        }
    }

    private static void parseMin(StringBuffer sBuffer, String minutes, String[] cronTimeArr) {
        if (!minutes.equals(CHAR_STAR)) {
            if (minutes.contains(CHAR_SLASH)) {
                // sBuffer.append("内，从第").append(minutes.split("/")[0]).append("分开始").append(",每")
                // .append(minutes.split("/")[1]).append(cronTimeArr[1]);
                sBuffer.append(appendGapInfo(minutes, TIMESCALE, 1));
            } else if (CHAR_ZERO.equals(minutes)) {
            } else if (minutes.contains(CHAR_SPILT)) {
                String[] splitMinute = minutes.split(CHAR_SPILT);
                sBuffer.append(splitMinute[0]).append(cronTimeArr[1]).append("到").append(splitMinute[1])
                        .append(cronTimeArr[1]).append("每分钟");
            } else {
                sBuffer.append(minutes).append(cronTimeArr[1]);
            }
        }
    }

    private static void parseHours(StringBuffer sBuffer, String hours, String[] cronTimeArr) {
        if (!hours.equals(CHAR_STAR)) {
            if (hours.contains(CHAR_SLASH)) {
                // sBuffer.append("内，从").append(hours.split("/")[0]).append("时开始").append(",每")
                // .append(hours.split("/")[1]).append(cronTimeArr[2]);
                sBuffer.append(appendGapInfo(hours, HOURS, 2));
            } else {
                if (sBuffer.toString().length() == 0) {
                    // 对于 , 的情况，直接拼接
                    sBuffer.append("每天").append(hours).append(cronTimeArr[2]);
                } else {
                    sBuffer.append(hours).append(cronTimeArr[2]);
                }
            }
        }
    }

    private static void parseDays(StringBuffer sBuffer, String days, String[] cronTimeArr, String dayOfWeek, String months, String[] tmpCorns) {
        if (!days.equals(CHAR_QUE) && !days.equals(CHAR_STAR)) {
            if (days.contains(CHAR_SLASH)) {
                sBuffer.append("每周从第").append(days.split(CHAR_SLASH)[0]).append("天开始").append(",每")
                        .append(days.split(CHAR_SLASH)[1]).append(cronTimeArr[3]);
            } else if (CHAR_L.equals(days)) {
                // 处理L 每月的最后一天
                sBuffer.append("每月最后一天");
            } else if (CHAR_W.equals(days)) {
                // 处理W 暂时不懂怎么处理
                sBuffer.append(days);
            } else if (days.contains(CHAR_WELL)) {
                String[] splitDays = days.split(CHAR_WELL);
                // 前面数字表示周几
                String weekNum = splitDays[0];
                // 后面的数字表示第几周 范围1-4 一个月最多4周
                String weekOfMonth = splitDays[1];
                String weekName = judgeWeek(weekNum);
                sBuffer.append("每月第").append(weekOfMonth).append(cronTimeArr[5]).append(weekName);
            } else {
//					sBuffer.append("每月").append(days).append(cronTimeArr[3]);
//                     sBuffer.append("每月").append(days).append("号");
                if (dayOfWeek.equals(CHAR_QUE) && !months.equals(CHAR_STAR)) {
                    sBuffer.append(days).append("号");
                } else {
                    sBuffer.append("每月").append(days).append("号");
                }
            }
        } else {
            boolean flag = sBuffer.toString().length() == 0 || tmpCorns.length == SEVEN;
            if (flag && !sBuffer.toString().contains(WEEK)) {
                // 前面没有内容的话，拼接下
                sBuffer.append("每").append(cronTimeArr[3]);
            }
        }
    }

    private static void parseDayOfWeek(StringBuffer sBuffer, String dayofWeek, String[] cronTimeArr) {
        if (!dayofWeek.equals(CHAR_STAR) && !dayofWeek.equals(CHAR_QUE)) {
            if (dayofWeek.contains(CHAR_COMMA)) {
                // 多个数字，逗号隔开
                sBuffer.append("每周的第").append(dayofWeek).append(cronTimeArr[3]);
            } else if (dayofWeek.contains(CHAR_L) && dayofWeek.length() > 1) {
                // 1L-7L
                String weekNum = dayofWeek.split(CHAR_L)[0];
                String weekName = judgeWeek(weekNum);
                sBuffer.append("每月的最后一周的");
                sBuffer.append(weekName);
            } else if (dayofWeek.contains(CHAR_SPILT)) {
                String[] splitWeek = dayofWeek.split(CHAR_SPILT);
                String weekOne = judgeWeek(splitWeek[0]);
                String weekTwo = judgeWeek(splitWeek[1]);
                sBuffer.append("每周的").append(weekOne).append("到").append(weekTwo);
            } else { // 1-7/L
                if (CHAR_L.equals(dayofWeek)) {
                    // L 转换为7，便于识别
                    dayofWeek = "7";
//						dayofWeek = "SAT";
                }
//					int weekNums = Integer.parseInt(dayofWeek);
//					if (weekNums < 0 || weekNums > 7) {
//						return "cron表达式有误，dayofWeek数字应为1-7";
//					}
                sBuffer.append("每周的");
                String weekName = judgeWeek(dayofWeek);
                sBuffer.append(weekName);
            }
        }
    }

    private static void parseMonths(StringBuffer sBuffer, String months, String[] cronTimeArr) {
        if (!months.equals(CHAR_STAR) && !months.equals(CHAR_QUE)) {
            if (months.contains(CHAR_SLASH)) {
                sBuffer.append("从").append(months.split(CHAR_SLASH)[0]).append("号开始").append(",每")
                        .append(months.split(CHAR_SLASH)[1]).append(cronTimeArr[4]);
            } else {
                if (sBuffer.toString().contains(CHAR_SPILT)) {
                    sBuffer.append("每年");
                }
                sBuffer.append(months).append(cronTimeArr[4]);
            }
        }
    }

    public static String judgeWeek(String weekNum) {
        String weekName = WeekEnum.matchNameCn(String.valueOf(weekNum));
        return StringUtils.isNotEmpty(weekName) ? weekName : String.valueOf(weekNum);
    }

    private static String appendGapInfo(String time, int rangeNum, int index) {
        StringBuffer sBufferTemp = new StringBuffer();
        String[] splitTime = time.split(CHAR_SLASH);
        String startNum = splitTime[0];
        String gapNum = splitTime[1];
        int endNum = rangeNum + Integer.parseInt(startNum) - Integer.parseInt(gapNum);
        String endStr = String.valueOf(endNum);
        String timeUnit = CRON_TIME_CN[index];
        String splitTimeUnit = CRON_TIME_CN[index];
        if (index == 1) {
            splitTimeUnit = "分钟";
        } else if (index == TWO) {
            splitTimeUnit = "小时";
        }
        sBufferTemp.append("从").append(startNum).append(timeUnit).append("开始").append("到").append(endStr)
                .append(timeUnit).append("范围内").append(",每隔").append(gapNum).append(splitTimeUnit);
        return sBufferTemp.toString();
    }

    // 测试方法

    public static void main(String[] args) {
        testCronExpression();
    }

    private static void testCronExpression() {
        String cronExpression = "";
        //1 41 11 21 04 ? 2023 表示2023年04月21号11点41分1秒执行
        cronExpression = "1 41 11 21 04 ? 2023";
        System.out.println(cronExpression);
        System.out.println(translateToChinese(cronExpression, CRON_TIME_CN));

        // 0 0 2 1 * ? * 表示在每月的1日的凌晨2点
        cronExpression = "0 0 2 1 * ? *";
        System.out.println(cronExpression);
        System.out.println(translateToChinese(cronExpression, CRON_TIME_CN));

        // 0 15 10 ? * 2-6 表示周一到周五每天上午10:15执行作业
        cronExpression = "0 15 10 ? * 2-6";
        System.out.println(cronExpression);
        System.out.println(translateToChinese(cronExpression, CRON_TIME_CN));

        // 每天上午10点，下午2点，4点
        cronExpression = "0 0 10,14,16 * * ?";
        System.out.println(cronExpression);
        System.out.println(translateToChinese(cronExpression, CRON_TIME_CN));

        // 在每天下午2点到下午2:05期间的每1分钟触发

        cronExpression = "0 5 14-16 * * ?";
        System.out.println(cronExpression);
        System.out.println(translateToChinese(cronExpression, CRON_TIME_CN));

        // 每月最后一日的上午10:15触发
        cronExpression = "0 15 10 L * ? ";
        System.out.println(cronExpression);
        System.out.println(translateToChinese(cronExpression, CRON_TIME_CN));

        // 每月的第三个星期五上午10:15触发
        cronExpression = "0 15 10 ? * 6";
        System.out.println(cronExpression);
        System.out.println(translateToChinese(cronExpression, CRON_TIME_CN));

        // 2002 每天中午12点触发
        cronExpression = "0 0 12 * * ? 2002";
        System.out.println(cronExpression);
        System.out.println(translateToChinese(cronExpression, CRON_TIME_CN));

        // 2002-2005年 每天中午12点触发
        cronExpression = "0 15 10 ? * ? 2002-2005";
        System.out.println(cronExpression);
        System.out.println(translateToChinese(cronExpression, CRON_TIME_CN));

        // 0 15 10 ? * 6L 每月的最后一个星期五上午10:15触发
        cronExpression = "0 15 10 ? * 6L";
        System.out.println(cronExpression);
        System.out.println(translateToChinese(cronExpression, CRON_TIME_CN));

        cronExpression = "0 15 10 6#4 * ?";
        System.out.println(cronExpression);
        System.out.println(translateToChinese(cronExpression, CRON_TIME_CN));

        // 0 15 10 * * ? * 每天上午10:15触发
        cronExpression = "0 15 10 * * ? *";
        System.out.println(cronExpression);
        System.out.println(translateToChinese(cronExpression, CRON_TIME_CN));

        // 0 15 10 * * ? 2005 2005年的每天上午10:15触发
        cronExpression = "0 15 10 * * ? 2005";
        System.out.println(cronExpression);
        System.out.println(translateToChinese(cronExpression, CRON_TIME_CN));

        // 0 * 14 * * ? 在每天下午2点到下午2:59期间的每1分钟触发
        cronExpression = "0 * 14 * * ?";
        System.out.println(cronExpression);
        System.out.println(translateToChinese(cronExpression, CRON_TIME_CN));

        // 0 0/5 14 * * ? 在每天下午2点到下午2:55期间的每5分钟触发
        cronExpression = "0 0/5 14 * * ?";
        System.out.println(cronExpression);
        System.out.println(translateToChinese(cronExpression, CRON_TIME_CN));

        // "0 0/5 14,18 * * ?" 在每天下午2点到2:55期间和下午6点到6:55期间的每5分钟触发
        cronExpression = "0 0/5 14,18 * * ?";
        System.out.println(cronExpression);
        System.out.println(translateToChinese(cronExpression, CRON_TIME_CN));

        cronExpression = "8/10 0/5 3/4 * * ?";
        System.out.println(cronExpression);
        System.out.println(translateToChinese(cronExpression, CRON_TIME_CN));

        cronExpression = "0 0 7 ? 1-5 WED 2021";
        System.out.println(cronExpression);
        System.out.println(translateToChinese(cronExpression, CRON_TIME_CN));
    }
}
