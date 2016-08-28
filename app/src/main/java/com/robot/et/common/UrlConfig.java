package com.robot.et.common;

public class UrlConfig {

    //接口总线  网络：139.196.171.217:8080    本地：192.168.1.28:8089
    private final static String URL_PATH = "http://139.196.171.217:8080/robot-app/";
    //本体机器人初次联网启动进行初始化url
    public final static String GET_ROBOT_INFO_START = URL_PATH + "robot/startUp";
    //获取所有用户信息的url
    public final static String GET_ALLUSER_INFO = URL_PATH + "robot/findBindUsers";
    //获取agora视频时对方的房间号的url
    public final static String GET_AGORA_ROOMNUM = URL_PATH + "jpush/notifyForSomBody";
    //设置机器人免打扰的的url
    public final static String ROBOT_DISTURB_URL = URL_PATH + "robot/changeRobotStatus";
    //根据设备码获取机器人信息的URL
    public final static String GET_ROBOT_INFO_BY_DEVICEID = URL_PATH + "robot/findRobotByDeviceId";
    //本体推送当前媒体状态到App的URL
    public final static String PUSH_MEDIASTATE_TO_APP = URL_PATH + "pushToApp/pushMediaState";
    //本体机器人给App用户推送消息
    public final static String PUSH_MESSAGE_TO_APP = URL_PATH + "pushToApp/pushMsgToApp";
    //单个或多个文件上传
    public final static String UPLOAD_FILE_PATH = URL_PATH + "file/upload";

}
