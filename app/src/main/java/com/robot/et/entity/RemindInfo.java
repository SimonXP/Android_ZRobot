package com.robot.et.entity;

/*
 * 提醒的实体类
 * robotNum 机器人编号
 * date   日期 格式：2016-05-05
 * time   时间 格式：14:00:00
 * content  提醒的内容   例如：吃饭
 * remindInt 是否已经提醒 0：未提醒  1：已提醒
 * frequency 提醒的频次  0:每天,1:一天 ,7:本周
 * originalAlarmTime 原始的闹铃时间   用于删除指定闹铃使用
 * remindMen  提醒的人
 * requireAnswer  要求回答内容
 * spareContent   备用内容
 * spareType   备用int型值
 */
public class RemindInfo {
	private String robotNum;
	private String date;
	private String time;
	private String content;
	private int remindInt;
	private int frequency;
	private String originalAlarmTime;
	private String remindMen;
	private String requireAnswer;
	private String spareContent;
	private int spareType;
	public String getRobotNum() {
		return robotNum;
	}
	public void setRobotNum(String robotNum) {
		this.robotNum = robotNum;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getRemindInt() {
		return remindInt;
	}
	public void setRemindInt(int remindInt) {
		this.remindInt = remindInt;
	}
	public int getFrequency() {
		return frequency;
	}
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	public String getOriginalAlarmTime() {
		return originalAlarmTime;
	}
	public void setOriginalAlarmTime(String originalAlarmTime) {
		this.originalAlarmTime = originalAlarmTime;
	}
	public String getRemindMen() {
		return remindMen;
	}
	public void setRemindMen(String remindMen) {
		this.remindMen = remindMen;
	}
	public String getRequireAnswer() {
		return requireAnswer;
	}
	public void setRequireAnswer(String requireAnswer) {
		this.requireAnswer = requireAnswer;
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

	public RemindInfo() {
		super();
	}
	
}
