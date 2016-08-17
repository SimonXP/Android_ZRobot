package com.robot.et.core.software.common.script;

import com.robot.et.entity.ScriptActionInfo;
import com.robot.et.entity.ScriptInfo;

import java.util.List;

/**
 * Created by houdeming on 2016/7/22.
 * 解析剧本回调
 */
public interface ScriptInfoCallBack {

    //获取剧本
    void getScribt(ScriptInfo info, List<ScriptActionInfo> infos);

}
