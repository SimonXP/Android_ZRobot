package com.robot.et.entity;

public class LearnAnswerInfo {

	private int questionId;
	private String robotNum;
	private String answer;
	private String action;
	private int learnType;
	public int getQuestionId() {
		return questionId;
	}
	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}
	public String getRobotNum() {
		return robotNum;
	}
	public void setRobotNum(String robotNum) {
		this.robotNum = robotNum;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public int getLearnType() {
		return learnType;
	}
	public void setLearnType(int learnType) {
		this.learnType = learnType;
	}
	public LearnAnswerInfo() {
		super();
	}
	
}
