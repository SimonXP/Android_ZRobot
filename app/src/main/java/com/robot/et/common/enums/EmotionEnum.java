package com.robot.et.common.enums;

//控制表情的指令
public enum EmotionEnum {

	EMOTION_NORMAL(6000, "正常", ""),

	EMOTION_SMILE(6001, "微笑", "嘻嘻"),
	EMOTION_SMILE2(6001, "笑一个", "嘿嘿"),
	EMOTION_SMILE3(6001, "笑一笑", "嘻嘻"),
	EMOTION_SMILE4(6001, "高兴", "哈哈"),
	EMOTION_SMILE5(6001, "傻笑", "嘿嘿"),
	EMOTION_SMILE6(6001, "兴奋", "哈哈"),

	EMOTION_ANGER(6002, "愤怒", "啊"),
	EMOTION_ANGER2(6002, "怒一个", "啊"),

	EMOTION_SAD(6003, "悲伤", "呜呜呜"),
	EMOTION_SAD2(6003, "哀愁", "欸"),
	EMOTION_SAD3(6003, "哀伤", "欸"),
	EMOTION_SAD4(6003, "忧愁", "欸"),
	EMOTION_SAD5(6003, "伤心", "呜呜呜"),
	EMOTION_SAD6(6003, "哭泣", "呜呜呜"),

	EMOTION_HAPPY(6004, "快乐", "哈哈"),
	EMOTION_HAPPY2(6004, "乐一个", "哈哈"),
	EMOTION_LOVELY(6004, "可爱", "嘻嘻"),

	EMOTION_LOOK_LEFT(6005, "左看", ""),

	EMOTION_LOOK_RIGHT(6006, "右看", ""),

	EMOTION_TEAR(6007, "流泪", ""),

	EMOTION_SLEEP(6008, "睡觉", ""),
	EMOTION_SLEEP2(6008, "睡觉表情", ""),

	EMOTION_SURPRISED(6000, "惊讶", ""),
	EMOTION_BLINK_LEFT(6000, "眨左眼", ""),
	EMOTION_BLINK_RIGHT(6000, "眨右眼", ""),
	EMOTION_BLINK_TWO(6000, "眨眼", ""),
	EMOTION_BLINK_TWO2(6000, "眨双眼", ""),
	EMOTION_SWEAT(6000, "流汗", ""),
	EMOTION_EYES_WHITE(6000, "白眼", ""),
	EMOTION_DOUBT(6000, "疑问", "");

	private int emotionKey;
	private String emotionName;
	private String requireAnswer;

	EmotionEnum(int emotionKey, String emotionName, String requireAnswer) {
		this.emotionKey = emotionKey;
		this.emotionName = emotionName;
		this.requireAnswer = requireAnswer;
	}

	public int getEmotionKey() {
		return emotionKey;
	}

	public String getEmotionName() {
		return emotionName;
	}

	public String getRequireAnswer() {
		return requireAnswer;
	}

}
