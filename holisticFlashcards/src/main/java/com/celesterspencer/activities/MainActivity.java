package com.celesterspencer.activities;


import com.celesterspencer.holisticflashcards.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

//TODO VocabularylistActivity Searchbar makes all words to lowercase in order to ignore case sensitivity

public class MainActivity extends HFActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.layout_dummy);
		
		Intent intent = new Intent("android.intent.action.VOCABULARYLISTACTIVITY");
		startActivity(intent);
	}

	
}
