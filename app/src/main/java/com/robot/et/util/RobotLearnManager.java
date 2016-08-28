package com.robot.et.util;

import android.text.TextUtils;
import android.util.Log;

import com.robot.et.db.RobotDB;
import com.robot.et.entity.LearnAnswerInfo;
import com.robot.et.entity.LearnQuestionInfo;

import java.util.List;
import java.util.Random;

public class RobotLearnManager {

    // 增加智能学习的问题与答案
    public static void insertLeanInfo(String question, String answer, String action, int learnType) {
        String robotNum = SharedPreferencesUtils.getInstance().getString(SharedPreferencesKeys.ROBOT_NUM, "");
        RobotDB mDb = RobotDB.getInstance();
        LearnQuestionInfo info = new LearnQuestionInfo();
        info.setRobotNum(robotNum);
        info.setQuestion(question);
        info.setLearnType(learnType);
        LearnAnswerInfo aInfo = new LearnAnswerInfo();
        aInfo.setRobotNum(robotNum);
        aInfo.setLearnType(learnType);
        if (!TextUtils.isEmpty(answer)) {
            aInfo.setAnswer(answer);
        }
        if (!TextUtils.isEmpty(action)) {
            aInfo.setAction(action);
        }
        int questionId = mDb.getQuesstionId(question);
        if (questionId != -1) {//已存在
            info.setId(questionId);
            mDb.updateLearnQuestion(info);
            aInfo.setQuestionId(questionId);
            mDb.updateLearnAnswer(aInfo);
        } else {//不存在
            mDb.addLearnQuestion(info);
            aInfo.setQuestionId(mDb.getQuesstionId(question));

            mDb.addLearnAnswer(aInfo);
        }
    }

    // 机器人通过人说固定的话来学习要回答的话语
    public static String learnBySpeak(int learnType, String questionAndAnswer) {
        String content = "";
        if (!TextUtils.isEmpty(questionAndAnswer)) {
            String question = MatchStringUtil.getQuestion(questionAndAnswer);
            String answer = MatchStringUtil.getAnswer(questionAndAnswer);
            Log.i("ifly", "quesstion====" + question);
            Log.i("ifly", "answer====" + answer);
            if (!TextUtils.isEmpty(question) && !TextUtils.isEmpty(answer)) {
                insertLeanInfo(question, answer, "", learnType);
                content = "好的，我记住了";
            } else {
                content = "我好像没学会，再教我一次吧 ";
            }

        }
        return content;
    }

    //机器人做自己学习的内容
    public static LearnAnswerInfo getRobotLearnInfo(String result) {
        LearnAnswerInfo mInfo = new LearnAnswerInfo();
        if (!TextUtils.isEmpty(result)) {
            RobotDB mDb = RobotDB.getInstance();
            int questionId = mDb.getQuesstionId(result);
            Log.i("ifly", "学习库里面问题questionId====" + questionId);
            if (questionId != -1) {
                List<LearnAnswerInfo> mInfos = mDb.getLearnAnswers(questionId);
                Log.i("ifly", "学习库里面答案mInfos.size()====" + mInfos.size());
                int size = mInfos.size();
                if (mInfos != null && size > 0) {
                    Random random = new Random();
                    int randNum = random.nextInt(size);
                    mInfo = mInfos.get(randNum);
                }
            }
        }
        return mInfo;
    }

    //机器人通过APP学习，（机器人学习库，通过说话学习）
    public static void learnByAppSpeak(int learnType, String questionAndAnswer) {
        if (!TextUtils.isEmpty(questionAndAnswer)) {
            String question = MatchStringUtil.getQuestion(questionAndAnswer);
            String answer = MatchStringUtil.getAnswer(questionAndAnswer);
            Log.i("ifly", "quesstion===" + question);
            Log.i("ifly", "answer===" + answer);
            if (!TextUtils.isEmpty(question) && !TextUtils.isEmpty(answer)) {
                insertLeanInfo(question, answer, "", learnType);
            }
        }
    }

}
