package com.robot.et.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by houdeming on 2016/7/29.
 * 脸部信息
 */
public class FaceInfo implements Parcelable {
    private String robotNum;//机器编号
    private String authorId;//脸部注册ID
    private String authorName;//用户姓名
    private int spareInt;//备用int字段
    private String spareContent;//备用字段1
    private String spareContent2;//备用字段2

    public String getRobotNum() {
        return robotNum;
    }

    public void setRobotNum(String robotNum) {
        this.robotNum = robotNum;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public int getSpareInt() {
        return spareInt;
    }

    public void setSpareInt(int spareInt) {
        this.spareInt = spareInt;
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

    public FaceInfo() {
        super();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(robotNum);
        parcel.writeString(authorId);
        parcel.writeString(authorName);
        parcel.writeInt(this.spareInt);
        parcel.writeString(spareContent);
        parcel.writeString(spareContent2);
    }

    public static final Parcelable.Creator<FaceInfo> CREATOR = new Parcelable.Creator<FaceInfo>() {
        public FaceInfo createFromParcel(Parcel in) {
            return new FaceInfo(in);
        }

        public FaceInfo[] newArray(int size) {
            return new FaceInfo[size];
        }
    };

    private FaceInfo(Parcel in) {
        robotNum = in.readString();
        authorId = in.readString();
        authorName = in.readString();
        spareInt = in.readInt();
        spareContent = in.readString();
        spareContent2 = in.readString();
    }

}
