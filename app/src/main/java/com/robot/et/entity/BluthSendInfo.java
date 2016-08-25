package com.robot.et.entity;

/**
 * Created by houdeming on 2016/8/24.
 * 蓝牙发送数据key值
 */
public class BluthSendInfo {
    private String cG;//category
    private String aT;//action
    private String DIS;//DigitalServoDriver
    private String aG;//angle
    private String VT;//Vertical
    private String HZ;//Horizontal
    private String DP;//display
    private String side;

    public String getcG() {
        return cG;
    }

    public void setcG(String cG) {
        this.cG = cG;
    }

    public String getaT() {
        return aT;
    }

    public void setaT(String aT) {
        this.aT = aT;
    }

    public String getDIS() {
        return DIS;
    }

    public void setDIS(String DIS) {
        this.DIS = DIS;
    }

    public String getaG() {
        return aG;
    }

    public void setaG(String aG) {
        this.aG = aG;
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
