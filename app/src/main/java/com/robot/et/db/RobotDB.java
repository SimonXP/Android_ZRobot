package com.robot.et.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.robot.et.entity.FaceInfo;
import com.robot.et.entity.LearnAnswerInfo;
import com.robot.et.entity.LearnQuestionInfo;
import com.robot.et.entity.RemindInfo;
import com.robot.et.entity.ScriptActionInfo;
import com.robot.et.entity.ScriptInfo;
import com.robot.et.main.CustomApplication;

import java.util.ArrayList;
import java.util.List;

/*
 * 本地数据库,数据库使用完一定要关闭，不然会报异常！
 */
public class RobotDB {
    private RobotDBHelper helper;
    public static RobotDB instance = null;

    private RobotDB() {
        helper = new RobotDBHelper(CustomApplication.getInstance().getApplicationContext());
    }

    public static RobotDB getInstance() {
        if (instance == null) {
            synchronized (RobotDB.class) {
                if (instance == null) {
                    instance = new RobotDB();
                }
            }
        }
        return instance;
    }

    //增加脸部识别的信息
    public void addFaceInfo(FaceInfo info) {
        String sql = "insert into faces(robotNum,authorId,authorName,spareInt,spareContent,spareContent2) values(?,?,?,?,?,?)";
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(sql, new String[]{info.getRobotNum(), info.getAuthorId(), info.getAuthorName(), String.valueOf(info.getSpareInt()), info.getSpareContent(), info.getSpareContent2()});
        db.close();
    }

    //查询所有脸部识别信息
    public ArrayList<FaceInfo> getFaceInfos() {
        String sql = "select * from faces order by id desc";
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor c = db.rawQuery(sql, new String[]{});
        ArrayList<FaceInfo> faceInfos = new ArrayList<FaceInfo>();
        while (c.moveToNext()) {
            FaceInfo info = new FaceInfo();
            info.setRobotNum(c.getString(c.getColumnIndex("robotNum")));
            info.setAuthorId(c.getString(c.getColumnIndex("authorId")));
            info.setAuthorName(c.getString(c.getColumnIndex("authorName")));
            info.setSpareInt(c.getInt(c.getColumnIndex("spareInt")));
            info.setSpareContent(c.getString(c.getColumnIndex("spareContent")));
            info.setSpareContent2(c.getString(c.getColumnIndex("spareContent2")));
            faceInfos.add(info);
        }

        c.close();
        db.close();
        return faceInfos;
    }

    //新增智能学习的问题
    public void addLearnQuestion(LearnQuestionInfo info) {
        String sql = "insert into question(robotNum,question,learnType) values(?,?,?)";
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(sql, new String[]{info.getRobotNum(), info.getQuestion(), String.valueOf(info.getLearnType())});
        db.close();
    }

    //新增智能学习的答案
    public void addLearnAnswer(LearnAnswerInfo info) {
        String sql = "insert into answer(questionId,robotNum,answer,action,learnType) values(?,?,?,?,?)";
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(sql, new String[]{String.valueOf(info.getQuestionId()), info.getRobotNum(), info.getAnswer(), info.getAction(), String.valueOf(info.getLearnType())});
        db.close();
    }

    //更新智能学习的问题
    public void updateLearnQuestion(LearnQuestionInfo info) {
        String sql = "update question set question=? where id=? and learnType=?";
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(sql, new String[]{info.getQuestion(), String.valueOf(info.getId()), String.valueOf(info.getLearnType())});
        db.close();
    }

    //更新智能学习的答案
    public void updateLearnAnswer(LearnAnswerInfo info) {
        String sql = "update answer set answer=?,action=? where questionId=? and learnType=?";
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(sql, new String[]{info.getAnswer(), info.getAction(), String.valueOf(info.getQuestionId()), String.valueOf(info.getLearnType())});
        db.close();
    }

    //获取智能学习问题的答案
    public List<LearnAnswerInfo> getLearnAnswers(int questionId) {
        String sql = "select * from answer where questionId=? order by id desc";
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor c = db.rawQuery(sql, new String[]{String.valueOf(questionId)});
        List<LearnAnswerInfo> infos = new ArrayList<LearnAnswerInfo>();
        while (c.moveToNext()) {
            LearnAnswerInfo info = new LearnAnswerInfo();
            info.setAnswer(c.getString(c.getColumnIndex("answer")));
            info.setAction(c.getString(c.getColumnIndex("action")));
            infos.add(info);
        }
        c.close();
        db.close();
        return infos;
    }

    //获取问题id
    public int getQuesstionId(String quesstion) {
        String sql = "select id from question where question like ?";
        String[] selectionArgs = new String[]{"%" + quesstion + "%"};
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor c = db.rawQuery(sql, selectionArgs);
        int questionId = -1;
        if (c.moveToNext()) {
            questionId = c.getInt(c.getColumnIndex("id"));
        }
        c.close();
        db.close();
        return questionId;
    }

    //增加提醒
    public void addRemindInfo(RemindInfo info) {
        String sql = "insert into reminds(robotNum,date,time,content,remindInt,frequency,originalAlarmTime,remindMen,requireAnswer,spareContent,spareType) " +
                "values(?,?,?,?,?,?,?,?,?,?,?)";
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(sql, new String[]{info.getRobotNum(), info.getDate(), info.getTime(), info.getContent(), String.valueOf(info.getRemindInt()),
                String.valueOf(info.getFrequency()), info.getOriginalAlarmTime(), info.getRemindMen(), info.getRequireAnswer(), info.getSpareContent(), String.valueOf(info.getSpareType())});
        db.close();
    }

    //根据日期时间查找提醒
    public List<RemindInfo> getRemindInfos(String date, String time, int remindInt) {
        String sql = "select * from reminds where date=? and time=? and remindInt=? order by id desc";
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor c = db.rawQuery(sql, new String[]{date, time, String.valueOf(remindInt)});
        List<RemindInfo> mRemindInfos = new ArrayList<RemindInfo>();
        while (c.moveToNext()) {
            RemindInfo info = new RemindInfo();
            info.setRobotNum(c.getString(c.getColumnIndex("robotNum")));
            info.setDate(c.getString(c.getColumnIndex("date")));
            info.setTime(c.getString(c.getColumnIndex("time")));
            info.setContent(c.getString(c.getColumnIndex("content")));
            info.setRemindInt(c.getInt(c.getColumnIndex("remindInt")));
            info.setFrequency(c.getInt(c.getColumnIndex("frequency")));
            info.setOriginalAlarmTime(c.getString(c.getColumnIndex("originalAlarmTime")));
            info.setRemindMen(c.getString(c.getColumnIndex("remindMen")));
            info.setRequireAnswer(c.getString(c.getColumnIndex("requireAnswer")));
            info.setSpareContent(c.getString(c.getColumnIndex("spareContent")));
            info.setSpareType(c.getInt(c.getColumnIndex("spareType")));
            mRemindInfos.add(info);
        }

        c.close();
        db.close();
        return mRemindInfos;
    }

    //根据某一个提醒查找内容
    public RemindInfo getRemindInfo(RemindInfo remindInfo) {
        String sql = "select * from reminds where date=? and time=? and content=? and remindInt=? and frequency=?";
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor c = db.rawQuery(sql, new String[]{remindInfo.getDate(), remindInfo.getTime(), remindInfo.getContent(), String.valueOf(remindInfo.getRemindInt()),
                String.valueOf(remindInfo.getFrequency())});
        RemindInfo info = null;
        if (c.moveToNext()) {
            info = new RemindInfo();
            info.setRobotNum(c.getString(c.getColumnIndex("robotNum")));
            info.setDate(c.getString(c.getColumnIndex("date")));
            info.setTime(c.getString(c.getColumnIndex("time")));
            info.setContent(c.getString(c.getColumnIndex("content")));
            info.setRemindInt(c.getInt(c.getColumnIndex("remindInt")));
            info.setFrequency(c.getInt(c.getColumnIndex("frequency")));
            info.setOriginalAlarmTime(c.getString(c.getColumnIndex("originalAlarmTime")));
            info.setRemindMen(c.getString(c.getColumnIndex("remindMen")));
            info.setRequireAnswer(c.getString(c.getColumnIndex("requireAnswer")));
            info.setSpareContent(c.getString(c.getColumnIndex("spareContent")));
            info.setSpareType(c.getInt(c.getColumnIndex("spareType")));
        }
        c.close();
        db.close();
        return info;
    }

    //根据日期时间删除提醒  不带内容，防止同一时间内有多个提醒
    public void deleteRemindInfo(String date, String time, int remindInt) {
        String sql = "delete from reminds where date=? and time=? and remindInt=?";
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(sql, new String[]{date, time, String.valueOf(remindInt)});
        db.close();
    }

    //根据APP原始的闹铃时间删除提醒
    public void deleteAppRemindInfo(String originalTime) {
        String sql = "delete from reminds where originalAlarmTime=?";
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(sql, new String[]{originalTime});
        db.close();
    }

    //更改提醒
    public void updateRemindInfo(RemindInfo info, String data, int frequency) {
        String sql = "update reminds set date=?,frequency=? where date=? and time=?";
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(sql, new String[]{data, String.valueOf(frequency), info.getDate(), info.getTime()});
        db.close();
    }

    //增加剧本
    public void addScript(ScriptInfo info) {
        String sql = "insert into script(userPhone,robotNum,scriptContent,scriptType,spareContent,spareContent2,spareContent3,spareType) values(?,?,?,?,?,?,?,?)";
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(sql, new String[]{info.getUserPhone(), info.getRobotNum(), info.getScriptContent(), String.valueOf(info.getScriptType()), info.getSpareContent(),
                info.getSpareContent2(), info.getSpareContent3(), String.valueOf(info.getSpareType())});
        db.close();
    }

    //根据名字获取剧本ID
    public int getScriptId(String content) {
        String sql = "select * from script where scriptContent=?";
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor c = db.rawQuery(sql, new String[]{content});
        int scriptId = -1;
        if (c.moveToNext()) {
            scriptId = c.getInt(c.getColumnIndex("id"));
        }
        c.close();
        db.close();
        return scriptId;
    }

    //增加剧本执行的命令
    public void addScriptAction(ScriptActionInfo info) {
        String sql = "insert into scriptAction(scriptId,actionType,content,spareContent,spareContent2,spareContent3,spareType) values(?,?,?,?,?,?,?)";
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(sql, new String[]{String.valueOf(info.getScriptId()), String.valueOf(info.getActionType()), info.getContent(), info.getSpareContent(),
                info.getSpareContent2(), info.getSpareContent3(), String.valueOf(info.getSpareType())});
        db.close();
    }

    //根据剧本ID获取执行的动作
    public List<ScriptActionInfo> getScriptActionInfos(int scriptId) {
        String sql = "select * from scriptAction where scriptId=?";
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor c = db.rawQuery(sql, new String[]{String.valueOf(scriptId)});
        List<ScriptActionInfo> infos = new ArrayList<ScriptActionInfo>();
        while (c.moveToNext()) {
            ScriptActionInfo info = new ScriptActionInfo();
            info.setScriptId(c.getInt(c.getColumnIndex("scriptId")));
            info.setActionType(c.getInt(c.getColumnIndex("actionType")));
            info.setContent(c.getString(c.getColumnIndex("content")));
            info.setSpareContent(c.getString(c.getColumnIndex("spareContent")));
            info.setSpareContent2(c.getString(c.getColumnIndex("spareContent2")));
            info.setSpareContent3(c.getString(c.getColumnIndex("spareContent3")));
            info.setSpareType(c.getInt(c.getColumnIndex("spareType")));
            infos.add(info);
        }
        c.close();
        db.close();
        return infos;
    }

    //更新剧本的名字
    public void updateScriptName(int scriptId, String scriptName) {
        String sql = "update script set scriptContent=? where scriptId=?";
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(sql, new String[]{scriptName, String.valueOf(scriptId)});
        db.close();
    }

    //根据剧本id删除剧本
    public void deleteScript(int scriptId) {
        String sql = "delete from script where id=?";
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(sql, new String[]{String.valueOf(scriptId)});
        db.close();
    }

    //根据剧本ID删除剧本动作
    public void deleteScriptAction(int scriptId) {
        String sql = "delete from scriptAction where scriptId=?";
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(sql, new String[]{String.valueOf(scriptId)});
        db.close();
    }

}
