package com.celesterspencer.activities;

import java.util.ArrayList;

import com.celesterspencer.core.Core;
import com.celesterspencer.core.Language;
import com.celesterspencer.core.WordList;
import com.celesterspencer.holisticflashcards.R;
import com.celesterspencer.util.Logger;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class CreateVocabularyListActivity extends Activity {
	
	Language m__selectedLanguage;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_create_vocablist);
		
		m__selectedLanguage = Language.CHINESE;
		Spinner spinner = (Spinner)findViewById(R.id.spinner1_create_vocablist);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				String languageString = parent.getItemAtPosition(pos).toString();
				if (languageString.contains("Chinese")) {
					m__selectedLanguage = Language.CHINESE;
				}else if(languageString.contains("Korean")) {
					m__selectedLanguage = Language.KOREAN;
				}else {
					Logger.log("No language in Spinner selected. Select Chinese by default", "CREATEVOCABULARYLIST");
					m__selectedLanguage = Language.CHINESE;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		final EditText nameEdittext = (EditText)findViewById(R.id.editText1_create_vocablist);
		Button createButton = (Button)findViewById(R.id.button1_create_vocablist);
		createButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String vocablistName = nameEdittext.getText().toString();
				vocablistName = vocablistName.trim();
				if (vocablistName.equals("")) {
					Toast.makeText(getApplicationContext(),"Vocabularyist name cannot be empty!", Toast.LENGTH_SHORT).show();
				}else {
					ArrayList<WordList> allLists = Core.getVocabularyBox().getAllLists();
					for (int ind = 0; ind < allLists.size(); ind++) {
						if (vocablistName.equals(allLists.get(ind).getListName().trim())) {
							Toast.makeText(getApplicationContext(),"Vocabularyist name already exists!", Toast.LENGTH_SHORT).show();
							return;
						}
					}
					WordList vocablist = new WordList(vocablistName, m__selectedLanguage);
					Core.getVocabularyBox().addVocabularyList(vocablist);
					finish();
				}
			}
		});
		
	}
}
