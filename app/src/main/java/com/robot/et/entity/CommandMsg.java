package com.robot.et.entity;

import java.io.Serializable;

//接受netty信息的实体类
public class CommandMsg implements Serializable {

    private static final long serialVersionUID = 1L;

    private String from;
    private String to;
    private String msg;

    public CommandMsg(String from, String to, String msg) {
        super();
        this.from = from;
        this.to = to;
        this.msg = msg;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
