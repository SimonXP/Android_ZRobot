package com.robot.et.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class RobotDBHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "robot.db";
	private static final int DB_VERSION = 1;

	public RobotDBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}
	
	public RobotDBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//脸部识别表
		String faces = "create table faces"+
				"("+ 
				"id integer primary key autoincrement,robotNum varchar(50),authorId varchar(50),authorName varchar(50),spareInt integer," +
				"spareContent varchar(50),spareContent2 varchar(50)" +
				 ")";

		//智能学习问题表
		String question = "create table question"+
				"("+
				"id integer primary key autoincrement,robotNum varchar(30),question varchar(2000),learnType integer"+
				")";

		//智能学习答案表
		String answer = "create table answer"+
				"("+
				"id integer primary key autoincrement,questionId integer,robotNum varchar(30),answer varchar(2000),action varchar(100),learnType integer"+
				")";

		//提醒表
		String reminds = "create table reminds"+
				"("+
				"id integer primary key autoincrement,robotNum varchar(50),date varchar(50),time varchar(20),content varchar(200),remindInt integer," +
				"frequency integer,originalAlarmTime varchar(50),remindMen varchar(50),requireAnswer varchar(200),spareContent varchar(200),spareType integer"+
				")";

		//剧本表
		String script = "create table script"+
				"("+
				"id integer primary key autoincrement,userPhone varchar(20),robotNum varchar(30),scriptContent varchar(100),scriptType integer,spareContent varchar(500)," +
				"spareContent2 varchar(500),spareContent3 varchar(500),spareType integer"+
				")";

		//剧本动作执行表
		String scriptAction = "create table scriptAction"+
				"("+
				"id integer primary key autoincrement,scriptId integer,actionType integer,content varchar(500),spareContent varchar(500),spareContent2 varchar(500)," +
				"spareContent3 varchar(500),spareType integer"+
				")";


		db.execSQL(faces);
		db.execSQL(question);
		db.execSQL(answer);
		db.execSQL(reminds);
		db.execSQL(script);
		db.execSQL(scriptAction);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}
