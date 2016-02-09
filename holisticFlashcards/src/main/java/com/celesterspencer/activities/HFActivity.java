package com.celesterspencer.activities;

import com.celesterspencer.core.Core;

import android.app.Activity;
import android.os.Bundle;

public class HFActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!Core.getApplicationContext().isContextSet()) {
			Core.getApplicationContext().setContext(getApplicationContext());
		}	
	}

	
	
	@Override
	protected void onStart() {
		super.onStart();
		Core.getIoManager().loadVocabularyLists();
	}



	@Override
	protected void onPause() {
		super.onPause();
		Core.getIoManager().saveVocabularyLists();
	}

	
	
}
