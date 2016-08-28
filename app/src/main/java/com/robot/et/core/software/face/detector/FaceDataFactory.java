package com.robot.et.core.software.face.detector;

import android.content.Context;

import com.robot.et.db.RobotDB;
import com.robot.et.entity.FaceInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by houdeming on 2016/7/29.
 */
public class FaceDataFactory {
    private static List<FaceInfo> infos = new ArrayList<FaceInfo>();
    private static String authorId;
    private static String authorName;

    public static void setNewFaceInfo(List<FaceInfo> faceInfos) {
        if (faceInfos != null && faceInfos.size() > 0) {
            faceInfos.remove(0);
            infos = faceInfos;
        } else {
            infos = null;
        }
    }

    public static List<FaceInfo> getFaceInfos() {
        return infos;
    }

    public static void addFaceInfo(Context context, String faceName) {
        FaceInfo info = new FaceInfo();
        info.setAuthorId(getAuthorId());
        info.setAuthorName(faceName);
        RobotDB.getInstance(context).addFaceInfo(info);
    }

    public static String getAuthorId() {
        return authorId;
    }

    public static void setAuthorId(String authorId) {
        FaceDataFactory.authorId = authorId;
    }

    public static String getAuthorName() {
        return authorName;
    }

    public static void setAuthorName(String authorName) {
        FaceDataFactory.authorName = authorName;
    }

}
