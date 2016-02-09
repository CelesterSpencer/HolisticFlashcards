package com.celesterspencer.core;

import java.util.ArrayList;
import java.util.concurrent.SynchronousQueue;

import android.content.Context;
import android.widget.Toast;

public class Core {

	private static Dictionary m__dictionary = new Dictionary();
	private static VocabularyBox m__vocabularyBox = new VocabularyBox();
	private static IOManager m__ioManager = new IOManager();
	private static ApplicationContext m__applicationContext = new ApplicationContext();
	private static SpeechManager m__speechManager = new SpeechManager();
	private static boolean m__isDebug = true;
	
	
	//-------------------------------------------------------------------------------------------------------------------
	// GETTERS AND SETTERS
	//-------------------------------------------------------------------------------------------------------------------	
	public static Dictionary getDictionary() {
		return m__dictionary;
	}
	
	public static VocabularyBox getVocabularyBox() {
		return m__vocabularyBox;
	}

	public static IOManager getIoManager() {
		return m__ioManager;
	}
	
	public static ApplicationContext getApplicationContext() {
		return m__applicationContext;
	}
	
	public static SpeechManager getSpeechManager() {
		return m__speechManager;
	}
	
	public static boolean getDebugState() {
		return m__isDebug;
	}
	
}
