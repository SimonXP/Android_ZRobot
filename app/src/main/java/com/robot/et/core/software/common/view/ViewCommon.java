package com.robot.et.core.software.common.view;

/**
 * Created by houdeming on 2016/8/26.
 */
public class ViewCommon {

    //初始化界面
    public static void initView() {
        EmotionManager.showEmotionLinearLayout(false);
        OneImgManager.showImgLinearLayout(false);
        TextManager.showTextLinearLayout(false);
        SpectrumManager.showSpectrumLinearLayout(false);
    }

}
