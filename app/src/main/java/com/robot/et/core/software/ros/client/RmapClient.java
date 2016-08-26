package com.robot.et.core.software.ros.client;

import android.util.Log;

import com.robot.et.common.DataConfig;
import com.robot.et.core.software.common.speech.SpeechImpl;
import com.robot.et.core.software.ros.map.RmapRequest;
import com.robot.et.core.software.ros.map.RmapResponse;

import org.ros.exception.RemoteException;
import org.ros.exception.RosRuntimeException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;


/**
 * Created by Tony on 2016/8/25.
 */
public class RmapClient extends AbstractNodeMain {

    private String mapName;

    public RmapClient(String mapName){
        this.mapName = mapName;
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("rosjava_tutorial_services/rmapClient");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        ServiceClient<RmapRequest, RmapResponse> serviceClient;
        try {
            serviceClient = connectedNode.newServiceClient("/turtlebot/save_only_map", com.robot.et.core.software.ros.map.Rmap._TYPE);
        } catch (ServiceNotFoundException e) {
            throw new RosRuntimeException(e);
        }
        final RmapRequest request = serviceClient.newMessage();
        request.setMapName(mapName);
        serviceClient.call(request, new ServiceResponseListener<RmapResponse>() {
            @Override
            public void onSuccess(RmapResponse response) {
                Log.e("ROS_Client","onSuccess:Save A Map");
                SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "保存地图成功");
            }

            @Override
            public void onFailure(RemoteException e) {
                Log.e("ROS_Client","onFailure");
                SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "保存地图失败");
                throw new RosRuntimeException(e);
            }
        });
    }
}

