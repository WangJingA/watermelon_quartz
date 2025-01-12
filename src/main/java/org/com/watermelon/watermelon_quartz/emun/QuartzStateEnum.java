package org.com.watermelon.watermelon_quartz.emun;


public enum QuartzStateEnum {
    STATE_WAITING("00001","WAITING","任务等待执行中") ,
    STATE_PAUSED("00002","PAUSED","任务暂停") ,
    STATE_ACQUIRED("00003","ACQUIRED","正常执行中"),
    STATE_BLOCKED("00004","BLOCKED","任务阻塞") ,
    STATE_ERROR("00005","ERROR","任务执行错误"),
    STATE_DEFAULT("00006","",""),
    STATE_NONE("00007","NONE","未执行"),
    STATE_NORMAL("00008","NORMAL","正常执行中"),
    STATE_COMPLETE("00009","COMPLETE","执行完成")
    ;
    public String stateEnMsg;
    public String stateCnMsg;
    public String stateCode;

    QuartzStateEnum(String stateCode, String stateEnMsg, String stateCnMsg) {
        this.stateEnMsg = stateEnMsg;
        this.stateCnMsg = stateCnMsg;
        this.stateCode = stateCode;
    }

    public String getStateCnMsg() {
        return stateCnMsg;
    }

    public String getStateEnMsg() {
        return stateEnMsg;
    }

    public String getStateCode() {
        return stateCode;
    }

    public static QuartzStateEnum stateEnTranCn(String stateEn){
        for (QuartzStateEnum quartzStateEnum : values()){
            if (quartzStateEnum.getStateEnMsg().equalsIgnoreCase(stateEn)){
                return quartzStateEnum;
            }
        }
        return QuartzStateEnum.STATE_DEFAULT;
    }

}
