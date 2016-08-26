package com.robot.et.entity;

/**
 * Created by houdeming on 2016/8/26.
 * 蓝牙接受数据
 * {"rdl":0,"rdm":0,"rdr":0,"xf":1,"xag":20,"hw":1}
 */
public class BluthReceiverInfo {
    private int xF;//讯飞唤醒状态
    private int xAg;//讯飞唤醒角度
    private int hw;//红外状态
    private int rdL;//雷达左边
    private int rdM;//雷达中间
    private int rdR;//雷达右边

    public int getxF() {
        return xF;
    }

    public void setxF(int xF) {
        this.xF = xF;
    }

    public int getxAg() {
        return xAg;
    }

    public void setxAg(int xAg) {
        this.xAg = xAg;
    }

    public int getRdL() {
        return rdL;
    }

    public void setRdL(int rdL) {
        this.rdL = rdL;
    }

    public int getHw() {
        return hw;
    }

    public void setHw(int hw) {
        this.hw = hw;
    }

    public int getRdM() {
        return rdM;
    }

    public void setRdM(int rdM) {
        this.rdM = rdM;
    }

    public int getRdR() {
        return rdR;
    }

    public void setRdR(int rdR) {
        this.rdR = rdR;
    }

    public BluthReceiverInfo() {
        super();
    }
}
