package com.celesterspencer.activities;

import java.util.ArrayList;
import java.util.StringTokenizer;

import com.celesterspencer.adapters.ReferenceHorizontalAdapter;
import com.celesterspencer.core.Core;
import com.celesterspencer.core.Word;
import com.celesterspencer.dummyclasses.AddReferenceButton;
import com.celesterspencer.exceptions.HanziPinyinNotMatchException;
import com.celesterspencer.holisticflashcards.R;
import com.celesterspencer.util.Logger;
import com.devsmart.android.ui.HorizontalListView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class EditVocabularyActivity extends HFActivity {

	ArrayList<Word> m__references = new ArrayList<Word>();
	ArrayList<Long> m__referenceNumbers = new ArrayList<Long>();
	ReferenceHorizontalAdapter adapter;
	long wordToDeleteID = 0; 
	Word m__word;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// get view
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.edit_vocabulary_layout, null);
		
		// get EditTexts
		final EditText hanzi_text = (EditText)layout.findViewById(R.id.editText1_edit_vocab);
		final EditText pinyin_text = (EditText)layout.findViewById(R.id.editText2_edit_vocab);
		final EditText translation_text = (EditText)layout.findViewById(R.id.editText3_edit_vocab);
		final EditText mnemonics_text = (EditText)layout.findViewById(R.id.editText4_edit_vocab);
		
		setupVocabulary(hanzi_text, pinyin_text, translation_text, mnemonics_text);
		setupButtons(layout, hanzi_text, pinyin_text, translation_text, mnemonics_text);
		setupAdapter(layout);
		
		setContentView(layout);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (data.getExtras().containsKey("isdeleted")) {
				String result = data.getStringExtra("isdeleted");
				if (result.equals("yes")) {
					Core.getIoManager().displayMessage("Deleted " + m__word.getAllCharacters());
					Core.getVocabularyBox().getActiveVocabularyList().removeWord(m__word);
					finish();
				}
			}	
			if (data.getExtras().containsKey("references")) {
				String result = data.getStringExtra("references");
				Core.getIoManager().displayMessage("Received: " + result);
				Logger.log("Gets resulting refs: " + result, "IOMANAGER");
				StringTokenizer tokenizer = new StringTokenizer(result, ",");
				while (tokenizer.hasMoreTokens()) {
					long id = Long.parseLong(tokenizer.nextToken().trim());
					m__referenceNumbers.add(id);
					Word word = Core.getDictionary().getWordByID(id);
					if (word != null) {
						m__references.add(word);
					}	
				}
				adapter.notifyDataSetChanged();
			}
		}
	}
	
	
	
	//-------------------------------------------------------------------------------------------------------------------
	// PRIVATE METHODS
	//-------------------------------------------------------------------------------------------------------------------
	private void setupVocabulary(final EditText hanzi_text, final EditText pinyin_text, final EditText translation_text, final EditText mnemonics_text) {
		
		// check if vocabid is in calling intent
		Bundle bundle = getIntent().getExtras();
		if (bundle.containsKey("vocabID")) {
			
			// get word of the selected words id
			String wordIDString = bundle.getString("vocabID");
			System.out.println("VocabID is : " + wordIDString);
			wordToDeleteID = (wordIDString != null) ? Long.parseLong(wordIDString) : 0;
			m__word = Core.getDictionary().getWordByID(wordToDeleteID);
			
			// fill edittexts
			hanzi_text.setText(m__word.getAllCharacters());
			pinyin_text.setText(m__word.getAllRomanizations());
			translation_text.setText(m__word.getTranslationsString());
			
			// add references
			String references = m__word.getReferencesString();
			StringTokenizer tokenizer = new StringTokenizer(references, ",");
			while (tokenizer.hasMoreTokens()) {
				long id = Long.parseLong(tokenizer.nextToken().trim());
				m__referenceNumbers.add(id);
				Word word = Core.getDictionary().getWordByID(id);
				if (word != null) {
					m__references.add(word);
				}	
			}
			mnemonics_text.setText(m__word.getMnemonic());
		}
	}
	
	private void setupAdapter(View layout) {
		HorizontalListView view = (HorizontalListView)layout.findViewById(R.id.listview_edit_vocab);
		final Intent intentResult = new Intent(this, SelectReferencesActivity.class);
		view.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id0) {
				if (parent != null){
					Object obj = parent.getItemAtPosition(position);
					if (obj instanceof AddReferenceButton) {
						startActivityForResult(intentResult, 1);
					}else if (obj instanceof Word) {
						m__references.remove(position);
						m__referenceNumbers.remove(position);
						adapter.notifyDataSetChanged();
					}
				}
			}	
		});
		
		// adding an adapter to the gridview
		adapter = new ReferenceHorizontalAdapter(this, m__references);
		view.setAdapter(adapter);
	}
	
	public void setupButtons(View layout, final EditText hanzi_text, final EditText pinyin_text, final EditText translation_text, final EditText mnemonics_text) {
		Button apply_button = (Button)layout.findViewById(R.id.button1_edit_vocab);
		Button delete_button = (Button)layout.findViewById(R.id.button2_edit_vocab);
		
		// create new ChineseWord and fill it with data from the view
		apply_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	String hanzi_string = hanzi_text.getText().toString().trim();
        		String pinyin_string = pinyin_text.getText().toString().trim();
        		String translation_string = translation_text.getText().toString().trim();
        		String mnemonic_string = mnemonics_text.getText().toString().trim();
                if (!hanzi_string.equals("") && !pinyin_string.equals("") && !translation_string.equals("")) {
                	try {
                		//set new word in dictionary and active list
						Word word = Core.getDictionary().getWordByID(wordToDeleteID);
						if(word != null) {
							word.setMeaning(hanzi_string, pinyin_string).setTranslation(translation_string).setMnemonic(mnemonic_string).setReferences(m__referenceNumbers);
							
							//clear fields
							hanzi_text.setText("");
							pinyin_text.setText("");
							translation_text.setText("");
							mnemonics_text.setText("");
							m__references.clear();
							m__referenceNumbers.clear();
							adapter.notifyDataSetChanged();
							
							Core.getIoManager().displayMessage("Word edited (" + Core.getDictionary().getAllWords().size() + ")"); 
							finish();
						}
                	} catch (HanziPinyinNotMatchException e) {
                		Core.getIoManager().displayMessage("Hanzi (" + hanzi_string.length() + ") and Pinyin (" + pinyin_string.length() + ") does not match");
                	}
                }else {
                	Core.getIoManager().displayMessage("Could not add word since some required fields are empty");
                }
            }
		});
		
		final Intent intentResultEditVocab = new Intent(this, DeleteListActivity.class);
		delete_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivityForResult(intentResultEditVocab, 1);
			}
		});
	}
}
