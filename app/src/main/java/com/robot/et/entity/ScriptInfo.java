package com.robot.et.entity;

//剧本
public class ScriptInfo {
    private int scriptId;
    private String userPhone;
    private String robotNum;
    private String scriptContent;
    private int scriptType;
    private String spareContent;//备用
    private String spareContent2;//备用
    private String spareContent3;//备用
    private int spareType;//备用

    public int getScriptId() {
        return scriptId;
    }

    public void setScriptId(int scriptId) {
        this.scriptId = scriptId;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getRobotNum() {
        return robotNum;
    }

    public void setRobotNum(String robotNum) {
        this.robotNum = robotNum;
    }

    public String getScriptContent() {
        return scriptContent;
    }

    public void setScriptContent(String scriptContent) {
        this.scriptContent = scriptContent;
    }

    public int getScriptType() {
        return scriptType;
    }

    public void setScriptType(int scriptType) {
        this.scriptType = scriptType;
    }

    public String getSpareContent2() {
        return spareContent2;
    }

    public void setSpareContent2(String spareContent2) {
        this.spareContent2 = spareContent2;
    }

    public String getSpareContent() {
        return spareContent;
    }

    public void setSpareContent(String spareContent) {
        this.spareContent = spareContent;
    }

    public int getSpareType() {
        return spareType;
    }

    public void setSpareType(int spareType) {
        this.spareType = spareType;
    }

    public String getSpareContent3() {
        return spareContent3;
    }

    public void setSpareContent3(String spareContent3) {
        this.spareContent3 = spareContent3;
    }

    public ScriptInfo() {
        super();
    }

}
