package com.robot.et.entity;

import java.util.List;

public class LearnQuestionInfo {

	private int id;
	private String robotNum;
	private String question;
	private List<LearnAnswerInfo> infos;
	private int learnType;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getRobotNum() {
		return robotNum;
	}
	public void setRobotNum(String robotNum) {
		this.robotNum = robotNum;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public List<LearnAnswerInfo> getInfos() {
		return infos;
	}
	public void setInfos(List<LearnAnswerInfo> infos) {
		this.infos = infos;
	}
	public int getLearnType() {
		return learnType;
	}
	public void setLearnType(int learnType) {
		this.learnType = learnType;
	}
	public LearnQuestionInfo() {
		super();
	}
	
}
