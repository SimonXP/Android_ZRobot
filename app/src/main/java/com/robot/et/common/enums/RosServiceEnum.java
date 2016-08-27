package com.robot.et.common.enums;

//ROS 服务
public enum RosServiceEnum {

	//测试RosClient
	STARTADDSERVICE("AddTWO","开启服务"),

	/*
	* 随便走走模块服务
	* @description Make A Map + Random Walk
	* Created by Tony
	*/
	ROAMING("Roaming","漫游"), //匹配Rapp中的Roaming
	ROAMING1("Roaming","随便走走"),//匹配Rapp中的Roaming
	ROAMING2("Roaming","自己走"),//匹配Rapp中的Roaming

	/*
	* 跟随模块服务
	* @description Load A Map + Save A Map +Control Move
	* Created by Tony
	*/
	WORLDNAVIGATION("World Navigation","地图导航"),//匹配Rapp中的World Navigation

	SAVEAMAP("SaveAMap","保存地图"),
	SAVEAMAP2("SaveAMap","地图保存"),
	FORWARDONEMETER("ForwardOneMeter","走一米"),

	/*
	* 跟随模块服务
	* @description Call the Rapp's Follower
	* Created by Tony
	*/
	FOLLOWER("Follower","跟我走"),//匹配Rapp中的Follower
	FOLLOWER2("Follower","跟我来"),//匹配Rapp中的Follower


	/*
	* 深度学习模块服务
	* @description Call the Rapp's Deep Learning + Call the Service Init/Learn/Rec
	* @mark Learn is not here（CommandHandler Method：isRosService，line number：397）
	* Created by Tony
	*/
	DEEPLEARN("Deep Learning","视觉服务"),//匹配Rapp中的Deep Learning

	DEEPLEARNINIT("DeepLearnInit","启动视觉学习"),
//	VISUALLEARNSERVICE("VisualLearn","视觉学习"),
	DEEPLEARNREC("DeepLearnRec","看看这个是什么"),
	DEEPLEARNREC1("DeepLearnRec","看看这是什么"),
	DEEPLEARNREC2("DeepLearnRec","看看这个是啥"),
	DEEPLEARNREC3("DeepLearnRec","看看这是啥"),

	DEEPLEARNCLOSE("DeepLearnClose","关闭视觉学习"),

	DELETEALLVISUAL("DeleteAllVisual","忘记学习内容"),

	/*
     * ******服务
     * Created by Tony
     */
	MAKEAMAP("MakeAMap","创建地图"),

	/*
	* 停止模块服务
	*/
	STOP("Stop","停下来");


	private String serviceKey;
	private String serviceName;

	private RosServiceEnum(String serviceKey, String serviceName){
		this.serviceKey = serviceKey;
		this.serviceName = serviceName;
	}

	public String getServiceName() {
		return serviceName;
	}

	public String getServiceKey() {
		return serviceKey;
	}

}
