package com.celesterspencer.core;

import android.content.Context;

public class ApplicationContext {

	Context m__applicationContext = null;
	
	public void setContext(Context applicationContext) {
		m__applicationContext = applicationContext;
	}
	
	public Context getContext() {
		return m__applicationContext;
	}
	
	public boolean isContextSet() {
		return m__applicationContext != null;
	}
	
}
