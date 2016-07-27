package com.robot.et.common.enums;

import android.text.TextUtils;

public class EnumManager {

	//获取科大讯飞提供的场景service
	public static SceneServiceEnum getIflyService(String str){
		for(SceneServiceEnum serviceEnum : SceneServiceEnum.values()){
			if(TextUtils.equals(str, serviceEnum.getServiceKey())){
				return serviceEnum;
			}
		}
		return null;
	}

}
