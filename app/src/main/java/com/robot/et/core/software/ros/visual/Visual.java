package com.robot.et.core.software.ros.visual;

import org.ros.internal.message.Message;

/**
 * Created by Tony on 2016/8/23.
 */
public interface Visual extends Message {
    String _TYPE = "com.robot.et.core.software.ros.visual/Visual";
    String _DEFINITION = "int64 id\nstring name\n---\nint64 result1\nint64 result2\nstring name\n";
}
