package com.robot.et.util;

import android.text.TextUtils;

//字符串匹配
public class MatchStringUtil {

    private static String baseRegex = "[\u4E00-\u9FA5A-Za-z0-9_]";
    // 间接增加声音
    public static String voiceBiggerIndirectRegex = "^" + baseRegex + "*((声音)|(音量))+" + baseRegex + "*(小|低)+" + baseRegex + "*(大|高|增|升|响|强)+" + baseRegex + "*$";
    // 直接增加声音
    public static String voiceBiggerRegex = "^" + baseRegex + "*((((声音)|(音量))+" + baseRegex + "*(大|高)+)|((再大)|(再高)|(再增)|(再升))+)" + baseRegex + "*$";
    // 声音最大
    public static String voiceBigestRegex = "^" + baseRegex + "*((声音)|(音量))+" + baseRegex + "*((最大)|(最高))+" + baseRegex + "*$";
    // 间接降低声音
    public static String voiceLitterIndirectRegex = "^" + baseRegex + "*((声音)|(音量))+" + baseRegex + "*(大|高)+" + baseRegex + "*(小|低|减|降|弱)+" + baseRegex + "*$";
    // 直接降低声音
    public static String voiceLitterRegex = "^" + baseRegex + "*((((声音)|(音量))+" + baseRegex + "*(小|低)+)|((再小)|(再低)|(再减)|(再降))+)" + baseRegex + "*$";
    // 声音最小
    public static String voiceLittestRegex = "^" + baseRegex + "*((声音)|(音量))+" + baseRegex + "*((最小)|(最低))+" + baseRegex + "*$";
    //学习的问题与答案
    public static String questionAndAnswerRegex = "^" + baseRegex + "*我+" + baseRegex + "*((问)|(说))+" + baseRegex + "*你+" + baseRegex + "+((说)|(回答))+" + baseRegex + "+$";
    //免打扰开
    public static String disturbOpenRegex = "^" + baseRegex + "*(((免打扰)+" + baseRegex + "*开+)|(开+" + baseRegex + "*(免打扰)+))" + baseRegex + "*$";
    //免打扰关
    public static String disturbCloseRegex = "^" + baseRegex + "*(((免打扰)+" + baseRegex + "*关+)|(关+" + baseRegex + "*(免打扰)+))" + baseRegex + "*$";
    //闭嘴
    public static String shutUpRegex = "^" + baseRegex + "*((嘴+" + baseRegex + "*闭+)|(闭+" + baseRegex + "*嘴+)|((休息)+))" + baseRegex + "*$";
    //做动作
    public static String doActionRegex = "^" + baseRegex + "*我+" + baseRegex + "*((问)|(说))+" + baseRegex + "*你+" + baseRegex + "+(萌|(卖个萌))+" + baseRegex + "*$";
    //控制机器人周围的玩具车走
    public static String controlToyCarRegex = "^" + baseRegex + "+号+" + baseRegex + "*(车|(小车)|(玩具车)|(汽车))+" + baseRegex + "*$";
    // 抬手举手
    public static String raiseHandRegex = "^" + baseRegex + "*(抬|举)+" + baseRegex + "*手+" + baseRegex + "*$";
    // 摆手
    public static String wavingRegex = "^" + baseRegex + "*摆+" + baseRegex + "*手+" + baseRegex + "*$";
    // 打开家电
    public static String openHouseholdRegex = "^" + baseRegex + "*开+" + baseRegex + "*(灯|(插座))+" + baseRegex + "*$";
    // 关闭家电
    public static String closeHouseholdRegex = "^" + baseRegex + "*关+" + baseRegex + "*(灯|(插座))+" + baseRegex + "*$";
    // 脸检测
    public static String faceTestRegex = "^" + baseRegex + "*((猜猜)|(看看))+" + baseRegex + "*(我是谁)+" + baseRegex + "*$";
    // 识别问名字
    public static String faceNameRegex = "^" + baseRegex + "*我+" + baseRegex + "*(叫|是)+" + baseRegex + "+$";


    //匹配场景字符串
    public static boolean matchString(String str, String strRegex) {
        return str.matches(strRegex);
    }

    /**
     * @return String
     * @method getAnswerStr(获取答案)
     * @author ZHY
     * @date 2016年6月13日 下午8:00:11
     */
    public static String getAnswer(String questAndAnser) {
        int beginIndex = 0;
        int endIndex = 0;
        char[] str = questAndAnser.toCharArray();
        endIndex = str.length;
        for (int i = 0; i < str.length; i++) {
            if ((str[i] + "").equals("说")) {
                beginIndex = i + 1;
            }
            if ((str[i] + "").equals("回") && (str[i + 1] + "").equals("答")) {
                beginIndex = i + 2;
            }
        }
        return questAndAnser.substring(beginIndex, endIndex);
    }

    /**
     * @return String
     * @method getQuestionStr(获取问题)
     * @author ZHY
     * @date 2016年6月13日 下午8:00:06
     */
    public static String getQuestion(String questAndAnser) {
        int beginIndex = 0;
        int endIndex = 0;
        char[] str = questAndAnser.toCharArray();
        for (int i = 0; i < str.length; i++) {
            if ((str[i] + "").equals("说") || ((str[i] + "").equals("问"))) {
                beginIndex = i;
                break;
            }
        }
        for (int i = 0; i < str.length; i++) {
            if ((str[i] + "").equals("你")) {
                endIndex = i;
            }
        }
        String tempResult = questAndAnser.substring(++beginIndex, endIndex);
        char[] datas = tempResult.toCharArray();
        if (datas != null && datas.length > 0) {
            if (TextUtils.equals((datas[0] + ""), "你") && TextUtils.equals((datas[1] + ""), "你")) {
                tempResult = tempResult.substring(1);
            }
        }
        return tempResult;
    }

    //获取控制机器人周围小车的号码
    public static int getToyCarNum(String result) {
        int carNum = 0;
        if (!TextUtils.isEmpty(result)) {
            String sign = "号";
            if (result.contains(sign)) {
                int end = result.indexOf(sign);
                String data = result.substring(0, end);
                StringBuffer buffer = new StringBuffer();
                if (!TextUtils.isEmpty(data)) {
                    for (int i = 0; i < data.length(); i++) {
                        char tempChar = data.charAt(i);
                        if (Character.isDigit(tempChar)) {
                            buffer.append(tempChar);
                        } else if (TextUtils.equals(tempChar + "", "一")) {
                            buffer.append("1");
                        }
                    }
                    String num = buffer.toString();
                    if (!TextUtils.isEmpty(num)) {
                        if (TextUtils.isDigitsOnly(num)) {
                            carNum = Integer.parseInt(num);
                        }
                    }
                }
            }
        }
        return carNum;
    }

    public static String getFaceName(String str) {
        String content = "";
        if (!TextUtils.isEmpty(str)) {
            if (str.contains("是")) {
                int lastIndes = str.lastIndexOf("是");
                content = str.substring(lastIndes + 1, str.length());
            } else if (str.contains("叫")) {
                int lastIndes = str.lastIndexOf("叫");
                content = str.substring(lastIndes + 1, str.length());
            }
        }
        return content;
    }

}
