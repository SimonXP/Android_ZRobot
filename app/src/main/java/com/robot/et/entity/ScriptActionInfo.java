package com.robot.et.entity;

//剧本的执行
public class ScriptActionInfo {
    private int id;
    private int scriptId;
    private int actionType;
    private String content;
    private String spareContent;//备用
    private String spareContent2;//备用
    private String spareContent3;//备用
    private int spareType;//备用

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getScriptId() {
        return scriptId;
    }

    public void setScriptId(int scriptId) {
        this.scriptId = scriptId;
    }

    public int getActionType() {
        return actionType;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSpareContent() {
        return spareContent;
    }

    public void setSpareContent(String spareContent) {
        this.spareContent = spareContent;
    }

    public String getSpareContent2() {
        return spareContent2;
    }

    public void setSpareContent2(String spareContent2) {
        this.spareContent2 = spareContent2;
    }

    public String getSpareContent3() {
        return spareContent3;
    }

    public void setSpareContent3(String spareContent3) {
        this.spareContent3 = spareContent3;
    }

    public int getSpareType() {
        return spareType;
    }

    public void setSpareType(int spareType) {
        this.spareType = spareType;
    }

    public ScriptActionInfo() {
        super();
    }

}
