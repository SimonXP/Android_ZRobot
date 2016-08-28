package com.robot.et.entity;

import android.os.Parcel;
import android.os.Parcelable;

//极光推送的消息类
public class JpushInfo implements Parcelable {

	private int id;
	private String alias;//定点推送的标志
	private String content;//推送的内容
	private int extra;//推送的类型
	private String roomNum;//房间号
	private String alarmTime;//闹铃的时间
	private String alarmContent;//闹铃的内容
	private int remindNum;//闹铃提醒的次数
	private int remindInteval;//每次闹铃提醒的时间间隔
	private int frequency;//闹钟频次
	private String question;//智能问答的问题
	private String answer;//智能问答的答案
	private String musicContent;//音乐名字
	private String direction;//方向

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getExtra() {
		return extra;
	}
	public void setExtra(int extra) {
		this.extra = extra;
	}
	public String getRoomNum() {
		return roomNum;
	}
	public void setRoomNum(String roomNum) {
		this.roomNum = roomNum;
	}
	public String getAlarmTime() {
		return alarmTime;
	}
	public void setAlarmTime(String alarmTime) {
		this.alarmTime = alarmTime;
	}
	public String getAlarmContent() {
		return alarmContent;
	}
	public void setAlarmContent(String alarmContent) {
		this.alarmContent = alarmContent;
	}
	public int getRemindNum() {
		return remindNum;
	}
	public void setRemindNum(int remindNum) {
		this.remindNum = remindNum;
	}
	public int getRemindInteval() {
		return remindInteval;
	}
	public void setRemindInteval(int remindInteval) {
		this.remindInteval = remindInteval;
	}
	public int getFrequency() {
		return frequency;
	}
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public String getMusicContent() {
		return musicContent;
	}
	public void setMusicContent(String musicContent) {
		this.musicContent = musicContent;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}

	public JpushInfo() {
		super();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(this.id);
		out.writeString(alias);
        out.writeString(content);
        out.writeInt(this.extra);
        out.writeString(roomNum);
        out.writeString(alarmTime);
        out.writeString(alarmContent);
        out.writeInt(this.remindNum);
        out.writeInt(this.remindInteval);
        out.writeInt(this.frequency);
        out.writeString(question);
        out.writeString(answer);
        out.writeString(musicContent);
        out.writeString(direction);
	}
	
	public static final Creator<JpushInfo> CREATOR = new Creator<JpushInfo>() {
		public JpushInfo createFromParcel(Parcel in) {
			return new JpushInfo(in);
		}

		public JpushInfo[] newArray(int size) {
			return new JpushInfo[size];
		}
	};

	private JpushInfo(Parcel in) {
		id = in.readInt();
		alias = in.readString();
		content = in.readString();
		extra = in.readInt();
		roomNum = in.readString();
		alarmTime = in.readString();
		alarmContent = in.readString();
		remindNum = in.readInt();
		remindInteval = in.readInt();
		frequency = in.readInt();
		question = in.readString();
		answer = in.readString();
		musicContent = in.readString();
		direction = in.readString();
	}
	
}
