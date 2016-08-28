package com.robot.et.common.enums;

import com.robot.et.util.MatchStringUtil;

//场景枚举类
public enum MatchSceneEnum {

	VOICE_BIGGEST_SCENE {// 声音最大
		public boolean isScene(String str) {
			return MatchStringUtil.matchString(str, MatchStringUtil.voiceBigestRegex);
		}
	},
	VOICE_LITTEST_SCENE {// 声音最小
		public boolean isScene(String str) {
			return MatchStringUtil.matchString(str, MatchStringUtil.voiceLittestRegex);
		}
	},
	VOICE_BIGGER_INDIRECT_SCENE {// 间接增加声音
		public boolean isScene(String str) {
			return MatchStringUtil.matchString(str, MatchStringUtil.voiceBiggerIndirectRegex);
		}
	},
	VOICE_LITTER_INDIRECT_SCENE {// 间接降低声音
		public boolean isScene(String str) {
			return MatchStringUtil.matchString(str, MatchStringUtil.voiceLitterIndirectRegex);
		}
	},
	VOICE_BIGGER_SCENE {// 直接增加声音
		public boolean isScene(String str) {
			return MatchStringUtil.matchString(str, MatchStringUtil.voiceBiggerRegex);
		}
	},
	VOICE_LITTER_SCENE {// 直接降低声音
		public boolean isScene(String str) {
			return MatchStringUtil.matchString(str, MatchStringUtil.voiceLitterRegex);
		}
	},
	QUESTION_ANSWER_SCENE {// 智能学习回答话语
		public boolean isScene(String str) {
			return MatchStringUtil.matchString(str, MatchStringUtil.questionAndAnswerRegex);
		}
	},
	DISTURB_OPEN_SCENE {// 免打扰开
		public boolean isScene(String str) {
			return MatchStringUtil.matchString(str, MatchStringUtil.disturbOpenRegex);
		}
	},
	DISTURB_CLOSE_SCENE {// 免打扰关
		public boolean isScene(String str) {
			return MatchStringUtil.matchString(str, MatchStringUtil.disturbCloseRegex);
		}
	},
	SHUT_UP_SCENE {// 闭嘴
		public boolean isScene(String str) {
			return MatchStringUtil.matchString(str, MatchStringUtil.shutUpRegex);
		}
	},
	DO_ACTION_SCENE {// 智能学习做动作
		public boolean isScene(String str) {
			return MatchStringUtil.matchString(str, MatchStringUtil.doActionRegex);
		}
	},
	CONTROL_TOYCAR_SCENE {// 控制玩具车
		public boolean isScene(String str) {
			return MatchStringUtil.matchString(str, MatchStringUtil.controlToyCarRegex);
		}
	},
	RAISE_HAND_SCENE {// 抬手
		public boolean isScene(String str) {
			return MatchStringUtil.matchString(str, MatchStringUtil.raiseHandRegex);
		}
	},
	WAVING_SCENE {// 摆手
		public boolean isScene(String str) {
			return MatchStringUtil.matchString(str, MatchStringUtil.wavingRegex);
		}
	},
	FACE_TEST_SCENE {// 脸部检测
		public boolean isScene(String str) {
			return MatchStringUtil.matchString(str, MatchStringUtil.faceTestRegex);
		}
	},
	FACE_NAME_SCENE {// 脸部名称
		public boolean isScene(String str) {
			return MatchStringUtil.matchString(str, MatchStringUtil.faceNameRegex);
		}
	},
	OPEN_HOUSEHOLD_SCENE {// 打开家电
		public boolean isScene(String str) {
			return MatchStringUtil.matchString(str, MatchStringUtil.openHouseholdRegex);
		}
	},
	CLOSE_HOUSEHOLD_SCENE {// 关闭家电
		public boolean isScene(String str) {
			return MatchStringUtil.matchString(str, MatchStringUtil.closeHouseholdRegex);
		}
	};

	public abstract boolean isScene(String str);
	
}
