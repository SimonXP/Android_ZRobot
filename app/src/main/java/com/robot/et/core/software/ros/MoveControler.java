package com.robot.et.core.software.ros;

import android.util.Log;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;

import java.util.Timer;
import java.util.TimerTask;

import geometry_msgs.Twist;

public class MoveControler extends AbstractNodeMain{

    private Publisher<Twist> publisher;

    private volatile boolean publishVelocity =false;

    private Twist currentVelocityCommand;

    private Timer publisherTimer;
    //是否向前
    private volatile boolean isForward =false;
    //是否向后
    private volatile boolean isBackWard =false;
    //是否左转
    private volatile boolean isTurnLeft =false;
    //是否右转
    private volatile boolean isTurnRight =false;

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("ZRobot/core_mover");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {

        publisher = connectedNode.newPublisher("/cmd_vel_mux/input/teleop", Twist._TYPE);
        currentVelocityCommand = publisher.newMessage();
        publisherTimer = new Timer();
        publisherTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (publishVelocity) {
                    if (isForward){
                        Log.i("ROS_MOVE","前进");
                        publishVelocity(1,0,0);
                    }else if (isBackWard){
                        Log.i("ROS_MOVE","后退");
                        publishVelocity(-1,0,0);
                    }else if (isTurnLeft){
                        Log.i("ROS_MOVE","向左");
                        publishVelocity(0,0,1);
                    }else if (isTurnRight){
                        Log.i("ROS_MOVE","向右");
                        publishVelocity(0,0,-1);
                    }else {
                        Log.i("ROS_MOVE","停止");
                        publishVelocity(0,0,0);
                    }
                    publisher.publish(currentVelocityCommand);
                }
            }
        }, 0, 80);
    }

    private void publishVelocity(double linearVelocityX, double linearVelocityY, double angularVelocityZ) {
        currentVelocityCommand.getLinear().setX(linearVelocityX);
        currentVelocityCommand.getLinear().setY(-linearVelocityY);
        currentVelocityCommand.getLinear().setZ(0);
        currentVelocityCommand.getAngular().setX(0);
        currentVelocityCommand.getAngular().setY(0);
        currentVelocityCommand.getAngular().setZ(angularVelocityZ);
    }

    public void isPublishVelocity(boolean publishVelocity){
        this.publishVelocity=publishVelocity;
    }

    public void execMoveForword(){
        this.isForward=true;
        this.isBackWard=false;
        this.isTurnLeft=false;
        this.isTurnRight=false;
    }
    public void execMoveBackForward(){
        this.isForward=false;
        this.isBackWard=true;
        this.isTurnLeft=false;
        this.isTurnRight=false;
    }
    public void execTurnLeft(){
        this.isForward=false;
        this.isBackWard=false;
        this.isTurnLeft=true;
        this.isTurnRight=false;
    }
    public void execTurnRight(){
        this.isForward=false;
        this.isBackWard=false;
        this.isTurnLeft=false;
        this.isTurnRight=true;
    }
    public void execStop(){
        this.isForward=false;
        this.isBackWard=false;
        this.isTurnLeft=false;
        this.isTurnRight=false;
    }

    @Override
    public void onShutdown(Node node) {
    }

    @Override
    public void onShutdownComplete(Node node) {
        publisherTimer.cancel();
        publisherTimer.purge();
    }

    @Override
    public void onError(Node node, Throwable throwable) {
    }
}

