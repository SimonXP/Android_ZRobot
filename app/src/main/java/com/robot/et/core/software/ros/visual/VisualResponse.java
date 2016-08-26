package com.robot.et.core.software.ros.visual;

import org.ros.internal.message.Message;

/**
 * Created by Tony on 2016/8/23.
 */

public interface VisualResponse extends Message {
    String _TYPE = "com.robot.et.core.software.ros.visual/VisualResponse";
    String _DEFINITION = "int64 result1\nint64 result2\nstring name\n";

    long getResult1();

    void setResult1(long var1);

    long getResult2();

    void setResult2(long var1);

    String getName();

    void setName(String var1);
}
