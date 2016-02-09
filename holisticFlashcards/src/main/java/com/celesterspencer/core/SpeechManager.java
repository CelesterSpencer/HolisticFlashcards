package com.celesterspencer.core;

import java.io.CharConversionException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.StringTokenizer;

import com.celesterspencer.util.Logger;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import android.speech.*;
import android.speech.tts.TextToSpeech;

public class SpeechManager implements TextToSpeech.OnInitListener{

	private AssetFileDescriptor descriptor;
	private String subfolder = "pth/";
	
	final String INITIAL = "(B|P|M|F|D|T|N|L|G|K|H|J|Q|X|Zh?|Ch?|R|Sh?|Y|W)?";
	final String FINAL = 	"(a|o|e|ai|ei|ao|ou|an(g)?|en(g)?|er|" // a finals
							+ "i(a|o|e|ai|ao|u|an(g)?|n(g)?)?|" // i finals
							+ "u(a|o|ai|i|an(g)?|n)?|ong|" // u finals
							+ "v|ue|uan|un|iong)"; // ü finals
	final String TONE = "[1|2|3|4|5]";

    MediaPlayer m__mp;
    
    private TextToSpeech tts;
    
    public SpeechManager () {
		m__mp = new MediaPlayer();
		m__mp.setVolume(3, 3);
    }
	
    //---------------------------------------------------------------------------------------------
    //natural speech methods
    //---------------------------------------------------------------------------------------------
    
    public void pronounceWords(String words, Language language) {
    	
    	// initialize tts if necessarily
    	if (tts == null) {
    		tts = new TextToSpeech(Core.getApplicationContext().getContext(), this);
    		tts.setSpeechRate(0.85f);
    	}
    	
    	Logger.log("TTS receives string" + words, "TTS");
    	if (language.equals(Language.CHINESE)) {
    		pronounceChinese(words);
    	}else if (language.equals(Language.KOREAN)) {
    		pronounceKorean(words);
    	}
    }
    
    private void pronounceKorean(String words) {
    	tts.speak(words, TextToSpeech.QUEUE_FLUSH, null);
    }
    
	private void pronounceChinese(String words) {
		final ArrayList<String> pinyins = new ArrayList<String>();
		try {
			StringTokenizer tokenizer = new StringTokenizer(words);
			while (tokenizer.hasMoreTokens()) {
				String pinyin = tokenizer.nextToken();

				pinyin = parsePinyin(pinyin);
				pinyins.add(pinyin);
			}
		} catch (CharConversionException e) {
			System.out.println("Could not parse String in " + words);
		}

		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				for (String pinyin : pinyins) {
					playAudio(pinyin);
				}
			}
		});
		thread.start();
	}

	private String parsePinyin(String pinyin) throws CharConversionException {
		System.out.println("Speechmanager trys to interpret: " + pinyin);
		pinyin.trim();
		if (pinyin.matches(INITIAL + FINAL + TONE)) {
			return pinyin;
		}else if (pinyin.matches(INITIAL + FINAL)) {
			pinyin += "5"; // add 5th tone if no tone is present
			return pinyin;
		}
		System.out.println("Speechmanager found no match for input pinyin");
		return "a1";
	}
	
    //Play Audio
    private void playAudio(String pinyin) {
    
    	//create final string
    	if (pinyin.length() == 1) {
    		pinyin = pinyin.substring(0, 1).toLowerCase();
    	}else if (pinyin.length() > 1) {
    		pinyin = pinyin.substring(0, 1).toLowerCase() + pinyin.substring(1);
    	}	
    	String fileName = subfolder + pinyin + ".mp3";
    	
    	// try to seek for a mp3 file with the name fileName and play it
        try {
        	while(m__mp.isPlaying()) {}
        	m__mp.reset();
            descriptor = Core.getApplicationContext().getContext().getAssets().openFd(fileName);
            m__mp.setDataSource(descriptor.getFileDescriptor(),descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();
            m__mp.prepare();
            
            m__mp.start();
        
        //error
        } catch (Exception e) {
        	System.out.println("MusicPlayer Error: \"" + fileName + "\"");
        	e.getStackTrace();
        }
    }

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			 
            int result1 = tts.setLanguage(Locale.KOREAN);
            int result2 = tts.setLanguage(Locale.KOREA);
 
            if (result1 == TextToSpeech.LANG_MISSING_DATA
                    || result1 == TextToSpeech.LANG_NOT_SUPPORTED) {
                Logger.log("This Language is not supported", "TTS");
                if (result2 == TextToSpeech.LANG_MISSING_DATA
                        || result2 == TextToSpeech.LANG_NOT_SUPPORTED) {
                	Logger.log("Neither Korean nor Korea is supported", "TTS");
                }else {
                	tts.setLanguage(Locale.KOREA);
                	Logger.log("Korea is supported", "TTS");
                }
            }else {
            	tts.setLanguage(Locale.KOREAN);
            	Logger.log("Korean is supported", "TTS");
            }
        } else {
            Logger.log("Initilization Failed!", "TTS");
        }
	}

}
