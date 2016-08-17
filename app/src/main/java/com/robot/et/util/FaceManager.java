package com.robot.et.util;

import com.robot.et.db.RobotDB;
import com.robot.et.entity.FaceInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by houdeming on 2016/7/29.
 */
public class FaceManager {
    private static List<FaceInfo> infos = new ArrayList<FaceInfo>();
    private static String authorId;
    private static String authorName;

    public static void setNewFaceInfo(List<FaceInfo> faceInfos) {
        int size = faceInfos.size();
        if (faceInfos != null && size > 0) {
            faceInfos.remove(0);
            infos = faceInfos;
        } else {
            infos = null;
        }
    }

    public static List<FaceInfo> getFaceInfos() {
        return infos;
    }

    public static void addFaceInfo(String faceName) {
        FaceInfo info = new FaceInfo();
        info.setAuthorId(getAuthorId());
        info.setAuthorName(faceName);
        RobotDB.getInstance().addFaceInfo(info);
    }

    public static String getAuthorId() {
        return authorId;
    }

    public static void setAuthorId(String authorId) {
        FaceManager.authorId = authorId;
    }

    public static String getAuthorName() {
        return authorName;
    }

    public static void setAuthorName(String authorName) {
        FaceManager.authorName = authorName;
    }

}
