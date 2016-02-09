package com.celesterspencer.activities;

import com.celesterspencer.core.Core;
import com.celesterspencer.core.WordList;
import com.celesterspencer.holisticflashcards.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class VocabularylistSettingsActivity extends Activity{
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.vocablist_settings_layout, null);
		
		// fill checkboxes
		setupEditTexts(layout);
		setupCheckboxes(layout);
		setupButtons(layout);

		setContentView(layout);
	}	
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (data.getExtras().containsKey("isdeleted")) {
				String result = data.getStringExtra("isdeleted");
				if (result.equals("yes")) {
					WordList list = Core.getVocabularyBox().getActiveVocabularyList();
					if (list != null) {
						Toast.makeText(getApplicationContext(), 
								"Deleted " + list.getListName(), 
								Toast.LENGTH_SHORT).show();
						Core.getVocabularyBox().removeVocabularyList(list);
						Core.getIoManager().deleteFile(list);
					}	
				}
			}	
		}
		finish();
	}
	
	
	
	//-------------------------------------------------------------------------------------------------------------------
	// PRIVATE METHODS
	//-------------------------------------------------------------------------------------------------------------------
	private void setupEditTexts(View layout) {
		WordList list = Core.getVocabularyBox().getActiveVocabularyList();
//		EditText editTextNewVocabPerDay = (EditText)findViewById(R.id.editText1_settings);
//		editTextNewVocabPerDay.setText("" + list.getNewVocabPerDay());
//		EditText editTextOldVocabPerDay = (EditText)findViewById(R.id.editText2_settings);
//		editTextOldVocabPerDay.setText("" + list.getOldVocabPerDay());
//		EditText editTextNumberOfWordsForBuildingSentences = (EditText)findViewById(R.id.editText3_settings);
//		editTextNumberOfWordsForBuildingSentences.setText("" + list.getNumberOfWordsForBuildingSentences());
	}
	
	private void setupCheckboxes(View layout) {
		
		// create checkboxes
		CheckBox checkIsEnglishQuestion = (CheckBox)layout.findViewById(R.id.check_settings);
		CheckBox checkisHanziColored = (CheckBox)layout.findViewById(R.id.check2_settings);
		
		// set checked states
//		if (Settings.isHanziAnswer()) {
//			checkIsEnglishQuestion.setChecked(true);
//		}else {
//			checkIsEnglishQuestion.setChecked(false);
//		}
//		if (Settings.isHanziColored()) {
//			checkisHanziColored.setChecked(true);
//		}else {
//			checkisHanziColored.setChecked(false);
//		}
		
		// set listener
//		checkIsEnglishQuestion.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//			
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				// if box is checked then English is the question and therefore Hanzi is the answer
//				boolean hanziIsAnswer = buttonView.isChecked();
//				Settings.setIsHanziAnswer(hanziIsAnswer);
//				Settings.setAppliedChanges(true);
//			}
//		});	
//		checkisHanziColored.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				boolean hanziIsColored = buttonView.isChecked();
//				Settings.setIsHanziColored(hanziIsColored);
//				Settings.setAppliedChanges(true);
//			}
//		});
	}
	
	private void setupButtons(View layout) {
		ImageButton backbutton = (ImageButton)layout.findViewById(R.id.imageButton_right);
		backbutton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		// set export button
		Button delete_button = (Button)layout.findViewById(R.id.button1_settings);
		final Intent intentResult = new Intent(this, DeleteListActivity.class);
		delete_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivityForResult(intentResult, 1);
			}
		});
	}
}
