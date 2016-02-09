package com.celesterspencer.activities;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.StringTokenizer;

import com.celesterspencer.core.Core;
import com.celesterspencer.core.Language;
import com.celesterspencer.core.Word;
import com.celesterspencer.dummyclasses.AddReferenceButton;
import com.celesterspencer.exceptions.HanziPinyinNotMatchException;
import com.celesterspencer.holisticflashcards.R;
import com.celesterspencer.util.Logger;
import com.devsmart.android.ui.HorizontalListView;
import com.celesterspencer.adapters.ReferenceHorizontalAdapter;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

public class VocabInputActivity extends HFActivity {

	ArrayList<Word> m__references = new ArrayList<Word>();
	ArrayList<Long> m__referenceNumbers = new ArrayList<Long>();
	ReferenceHorizontalAdapter adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// get layout of activity
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View vocab_input_layout = inflater.inflate(R.layout.layout_vocab_input, null);
		
		// setup elements of the layout
		setupHorizontalAdapter(vocab_input_layout);
		setupButtons(vocab_input_layout);
		
		// set edittext 
		setEdittextHints(vocab_input_layout);
		
		// set the content view
		setContentView(vocab_input_layout);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			// check if references were transfered
			if (data.getExtras().containsKey("references")) {
				String result = data.getStringExtra("references");
				Core.getIoManager().displayMessage("Received: " + result);
				Logger.log("Gets resulting refs: " + result, "VOCABINPUT");
				
				// split references string into its parts
				// get all referenced words
				StringTokenizer tokenizer = new StringTokenizer(result, ",");
				while (tokenizer.hasMoreTokens()) {
					long id = Long.parseLong(tokenizer.nextToken().trim());
					Logger.log("Got referenced number " + id, "VOCABINPUT");
					m__referenceNumbers.add(id);
					Word word = Core.getDictionary().getWordByID(id);
					if (word == null) {
						Logger.log("Added null element", "VOCABINPUT");
					}else {
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
	private void setEdittextHints(View layout) {
		EditText charactersEditText = (EditText)layout.findViewById(R.id.editText1_input);
		EditText romanizationsEditText = (EditText)layout.findViewById(R.id.editText2_input);
		
		Language language = Core.getVocabularyBox().getActiveVocabularyList().getLanguage();
		switch (language) {
		case CHINESE:
			charactersEditText.setHint("hanzi");
			romanizationsEditText.setHint("pinyin");
			break;
		case KOREAN:
			charactersEditText.setHint("hangeul");
			romanizationsEditText.setHint("romanization");
			break;
		}
	}
	
	private void setupHorizontalAdapter(View vocab_input_layout) {
		HorizontalListView horizontalListView = (HorizontalListView)vocab_input_layout.findViewById(R.id.listview_horizontal);
		final Intent intentResult = new Intent(this, SelectReferencesActivity.class);
		horizontalListView.setOnItemClickListener(new OnItemClickListener() {
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
		
		// adding an adapter to the horizontalview
		adapter = new ReferenceHorizontalAdapter(this, m__references);
		horizontalListView.setAdapter(adapter);
	}
	
	private void setupButtons(View vocab_input_layout) {
		final EditText hanzi_text = (EditText)vocab_input_layout.findViewById(R.id.editText1_input);
		final EditText pinyin_text = (EditText)vocab_input_layout.findViewById(R.id.editText2_input);
		final EditText translation_text = (EditText)vocab_input_layout.findViewById(R.id.editText3_input);
		final EditText mnemonics_text = (EditText)vocab_input_layout.findViewById(R.id.editText4_input);
		
		Button add_button = (Button)vocab_input_layout.findViewById(R.id.button1_input);
		ImageButton back_button = (ImageButton)vocab_input_layout.findViewById(R.id.imageButton_right);
		
		// create new ChineseWord and fill it with data from the view
		add_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	addVocabularyToActiveVocabularyList(hanzi_text, pinyin_text, translation_text, mnemonics_text);
            }
		});
		
		// clear all fields and exit activity
		back_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//clear fields
				hanzi_text.setText("");
				pinyin_text.setText("");
				translation_text.setText("");
				mnemonics_text.setText("");
				m__references.clear();
				m__referenceNumbers.clear();
				adapter.notifyDataSetChanged();
				finish();
			}
		});
	}
	
	private void addVocabularyToActiveVocabularyList(EditText character_text, EditText romanization_text, EditText translation_text, EditText mnemonics_text) {
      	String characters_string = character_text.getText().toString().trim();
		String romanization_string = romanization_text.getText().toString().trim();
		String translation_string = translation_text.getText().toString().trim();
		String mnemonic_string = mnemonics_text.getText().toString().trim();
        if (!characters_string.equals("") && !romanization_string.equals("") && !translation_string.equals("")) {
        	try {
        		//add new word to active list
        		Language language = Core.getVocabularyBox().getActiveVocabularyList().getLanguage();
				Word word = new Word(characters_string, romanization_string, language);
				word.setTranslation(translation_string).setMnemonic(mnemonic_string).setReferences(m__referenceNumbers);
				word.setId(Core.getDictionary().getNextFreeId());
				Core.getVocabularyBox().getActiveVocabularyList().addWord(word);
				
				//clear fields
				character_text.setText("");
				romanization_text.setText("");
				translation_text.setText("");
				mnemonics_text.setText("");
				m__references.clear();
				m__referenceNumbers.clear();
				adapter.notifyDataSetChanged();
				
				// display message
				Core.getIoManager().displayMessage("Word added (" + Core.getDictionary().getAllWords().size()+ ")");
        	} catch (HanziPinyinNotMatchException e) {
        		Core.getIoManager().displayMessage("Characters (" + characters_string.length() + ") and Romanizations (" + romanization_string.length() + ") does not match");
        	}
        }else {
        	Core.getIoManager().displayMessage("Could not add word since some required fields are empty");
        }
	}
	
}
