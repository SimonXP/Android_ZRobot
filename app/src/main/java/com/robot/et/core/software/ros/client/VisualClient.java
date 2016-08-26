/*
 * Copyright (C) 2011 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.robot.et.core.software.ros.client;

import android.text.TextUtils;
import android.util.Log;

import com.robot.et.common.DataConfig;
import com.robot.et.core.software.common.speech.SpeechImpl;
import com.robot.et.core.software.ros.visual.VisualRequest;
import com.robot.et.core.software.ros.visual.VisualResponse;

import org.ros.exception.RemoteException;
import org.ros.exception.RosRuntimeException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;

public class VisualClient extends AbstractNodeMain {

    private int flag;
    private String name;

    public VisualClient(int flag, String name) {
        this.flag = flag;
        this.name = name;
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("rosjava_tutorial_services/visualclient");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        ServiceClient<VisualRequest, VisualResponse> serviceClient;
        try {
            serviceClient = connectedNode.newServiceClient("learn_to_recognize_ros_server", com.robot.et.core.software.ros.visual.Visual._TYPE);
        } catch (ServiceNotFoundException e) {
            SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "视觉出现异常");
            throw new RosRuntimeException(e);
        }
        final VisualRequest request = serviceClient.newMessage();
        request.setId(flag);
        request.setName(name);
        serviceClient.call(request, new ServiceResponseListener<VisualResponse>() {
            @Override
            public void onSuccess(VisualResponse response) {
                Log.e("ROS_Client", "onSuccess:Result:" + response.getResult1() + ",Name:" + response.getName());
                if (flag == 1) {
                    //打开视觉
                    if (response.getResult1() == 0) {
                        SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "视觉已启动");
                    } else {
                        SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "视觉启动失败");
                    }
                } else if (flag == 2) {
                    //视觉学习
                    if (response.getResult1()== -1){
                        SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "视觉学习未开启");
                    }else if (response.getResult1()== 0){
                        SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "好的，记住了");
                    }else if (response.getResult1()==1){
                        SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "距离太近了");
                    }else if (response.getResult1()==2){
                        SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "距离太远了");
                    }else if (response.getResult1()==10){
                        SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "物体太低了");
                    }else if (response.getResult1()==11){
                        SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "物体太高了");
                    }else if (response.getResult1()==12){
                        SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "物体太靠左了");
                    }else if (response.getResult1()==13){
                        SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "物体太靠右了");
                    }else if (response.getResult1()==20){
                        SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "没看见有东西");
                    }else if (response.getResult1()==21){
                        SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "东西太小了，看不清");
                    }
                } else if (flag == 3) {
                    //视觉识别
                    if (response.getResult1()== -1){
                        SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "视觉学习未开启");
                    }else if (response.getResult1()==0){
                        if (response.getResult2()==0){
                            SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "我不认识这个东西，让我学习一下吧！");
                        }else if (response.getResult2()== 1){
                            //可能（40%以上）
                            SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "这可能是："+response.getName());
                        }else if (response.getResult2()== 2){
                            //是（80%以上）
                            SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "这是："+response.getName());
                        }else if (response.getResult2()== 3){
                            //一定（95%以上）
                            SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "这一定是："+response.getName());
                        }else if (response.getResult2()== 10){
                            String[] temp=response.getName().split("|");
                            if (temp.length==2){
                                SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "这个不是："+temp[0]+"就是："+temp[1]);
                            }else {
                                SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "出现异常");
                            }
                        }
                    }else if (response.getResult1()==1){
                        SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "距离太近了");
                    }else if (response.getResult1()==2){
                        SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "距离太远了");
                    }else if (response.getResult1()==10){
                        SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "物体太低了");
                    }else if (response.getResult1()==11){
                        SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "物体太高了");
                    }else if (response.getResult1()==12){
                        SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "物体太靠左了");
                    }else if (response.getResult1()==13){
                        SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "物体太靠右了");
                    }else if (response.getResult1()==20){
                        SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "没看见有东西");
                    }else if (response.getResult1()==21){
                        SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "东西太小了，看不清");
                    }
                }else if (flag == 4){
                    //视觉识别
                    if (response.getResult1()==0){
                        SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "关闭视觉识别模块");
                    }else {
                        SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "关闭视觉识别模块失败");
                    }
                }else if (flag == 5){
                    //删除所有视觉学习内容
                    if (response.getResult1()==0){
                        SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "已删除所学内容");
                    }else {
                        SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "删除学习内容失败");
                    }
                }
            }

            @Override
            public void onFailure(RemoteException e) {
                SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "视觉出现异常");
                SpeechImpl.getInstance().startListen();
                Log.e("ROS_Client", "onFailure");
                throw new RosRuntimeException(e);
            }
        });
    }
}
