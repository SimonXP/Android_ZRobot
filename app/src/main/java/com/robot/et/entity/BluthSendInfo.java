package com.robot.et.entity;

/**
 * Created by houdeming on 2016/8/24.
 * 蓝牙发送数据key值
 */
public class BluthSendInfo {
    private String CG;//category
    private String AT;//action
    private String DIS;//DigitalServoDriver
    private String AG;//angle
    private String VT;//Vertical
    private String HZ;//Horizontal
    private String DP;//display
    private String side;

    public String getCG() {
        return CG;
    }

    public void setCG(String CG) {
        this.CG = CG;
    }

    public String getAT() {
        return AT;
    }

    public void setAT(String AT) {
        this.AT = AT;
    }

    public String getDIS() {
        return DIS;
    }

    public void setDIS(String DIS) {
        this.DIS = DIS;
    }

    public String getAG() {
        return AG;
    }

    public void setAG(String AG) {
        this.AG = AG;
    }

    public String getVT() {
        return VT;
    }

    public void setVT(String VT) {
        this.VT = VT;
    }

    public String getHZ() {
        return HZ;
    }

    public void setHZ(String HZ) {
        this.HZ = HZ;
    }

    public String getDP() {
        return DP;
    }

    public void setDP(String DP) {
        this.DP = DP;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public BluthSendInfo() {
        super();
    }
}
