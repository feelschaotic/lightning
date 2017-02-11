package com.ramo.utils;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.ramo.application.MyApplication;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class VoiceUtils {

	public static void beginSpeak(String voiceText) {

		// 1.创建 SpeechSynthesizer 对象, 第二个参数： 本地合成时传 InitListener
		SpeechSynthesizer mTts = SpeechSynthesizer.createSynthesizer(
				MyApplication.getContext(), null);

		// 2.合成参数设置，详见《MSC Reference Manual》 SpeechSynthesizer 类
		// 设置发音人（更多在线发音人，用户可参见 附录13.2
		mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan"); // 设置发音人
		mTts.setParameter(SpeechConstant.SPEED, "50");// 设置语速
		mTts.setParameter(SpeechConstant.VOLUME, "80");// 设置音量，范围 0~100
		mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); // 设置云端
		
		// 设置合成音频保存位置（可自定义保存位置），保存在“./sdcard/iflytek.pcm”
		// 保存在 SD 卡需要在 AndroidManifest.xml 添加写 SD 卡权限
		// 仅支持保存为 pcm 和 wav 格式， 如果不需要保存合成音频，注释该行代码
		//mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, "./sdcard/iflytek.pcm");
		// 3.开始合成
		mTts.startSpeaking(voiceText, null);
	}
	// 语音听写对象
	private SpeechRecognizer mIat;
	// 语音听写UI
	private RecognizerDialog mIatDialog;
	// 用HashMap存储听写结果
	private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();

	public static String voiceToStr(){

		return null;
	}
}
