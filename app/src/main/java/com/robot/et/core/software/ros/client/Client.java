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

import android.util.Log;

import org.ros.exception.RemoteException;
import org.ros.exception.RosRuntimeException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;

import rosjava_test_msgs.AddTwoIntsRequest;
import rosjava_test_msgs.AddTwoIntsResponse;

/**
 * A simple {@link ServiceClient} {@link NodeMain}.
 * 
 * @author damonkohler@google.com (Damon Kohler)
 */
public class Client extends AbstractNodeMain {




  @Override
  public GraphName getDefaultNodeName() {
    return GraphName.of("rosjava_tutorial_services/client");
  }

  @Override
  public void onStart(final ConnectedNode connectedNode) {
    ServiceClient<AddTwoIntsRequest, AddTwoIntsResponse> serviceClient;
    try {
      serviceClient = connectedNode.newServiceClient("add_two_ints", rosjava_test_msgs.AddTwoInts._TYPE);
    } catch (ServiceNotFoundException e) {
      throw new RosRuntimeException(e);
    }
    final AddTwoIntsRequest request = serviceClient.newMessage();
    request.setA(2);
    request.setB(2);
    serviceClient.call(request, new ServiceResponseListener<AddTwoIntsResponse>() {
      @Override
      public void onSuccess(AddTwoIntsResponse response) {
        Log.e("ROS_Client","onSuccess:"+response.getSum());
        connectedNode.getLog().info(String.format("%d + %d = %d", request.getA(), request.getB(), response.getSum()));
      }

      @Override
      public void onFailure(RemoteException e) {
        Log.e("ROS_Client","onFailure");
        throw new RosRuntimeException(e);
      }
    });
  }
}
