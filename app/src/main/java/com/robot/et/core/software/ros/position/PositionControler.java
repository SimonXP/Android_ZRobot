package com.robot.et.core.software.ros.position;

import android.util.Log;

import com.robot.et.common.DataConfig;
import com.robot.et.core.software.common.speech.SpeechImpl;

import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

import geometry_msgs.PoseWithCovariance;
import geometry_msgs.PoseWithCovarianceStamped;
import nav_msgs.Odometry;

/**
 * Created by Tony on 2016/8/26.
 */
public class PositionControler extends AbstractNodeMain implements MessageListener<geometry_msgs.PoseWithCovarianceStamped> {

    private Subscriber<geometry_msgs.PoseWithCovarianceStamped> subscriber;
    private boolean isReady = false;
    private double[] pointXY =new double[2];

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("RobotET/positionControler");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        super.onStart(connectedNode);
        subscriber = connectedNode.newSubscriber("amcl_pose", geometry_msgs.PoseWithCovarianceStamped._TYPE);
        subscriber.addMessageListener(this);
    }

    @Override
    public void onNewMessage(geometry_msgs.PoseWithCovarianceStamped message) {
        isReady=true;
//        message.getHeader();
        double x = message.getPose().getPose().getPosition().getX();
        double y = message.getPose().getPose().getPosition().getY();
        double z = message.getPose().getPose().getPosition().getZ();
        pointXY [0] = x;
        pointXY [1] = y;
        Log.e("ROS_Client","获取到坐标X"+x+"坐标Y"+y);
//        SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "获取到坐标X"+x+"坐标Y"+y);
    }

    public double[] getPointXY(){
        if (isReady){
            return pointXY;
        }else {
            return pointXY;
        }
    }
}
