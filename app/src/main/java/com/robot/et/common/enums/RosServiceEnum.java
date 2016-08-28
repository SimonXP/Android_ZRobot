package com.robot.et.common.enums;

//ROS 服务
public enum RosServiceEnum {

	ROAMING("Roaming","漫游"),
	ROAMING1("Roaming","随便走走"),
	ROAMING2("Roaming","自己走"),
	STARTADDSERVICE("AddTWO","开启服务"),
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
