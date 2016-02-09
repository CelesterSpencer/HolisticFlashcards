package com.celesterspencer.activities;

import java.util.ArrayList;

import com.celesterspencer.adapters.DebugVocabArrayAdapter;
import com.celesterspencer.adapters.VocabArrayAdapter;
import com.celesterspencer.core.Core;
import com.celesterspencer.core.Language;
import com.celesterspencer.core.Word;
import com.celesterspencer.holisticflashcards.R;
import com.celesterspencer.util.LexicalArrayList;
import com.celesterspencer.util.Logger;
import com.devsmart.android.ui.HorizontalListView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

// tutorial : http://www.vogella.com/tutorials/AndroidListView/article.html
// next time start with 9. Storing selection of a view

import android.app.Activity;
import android.app.ListActivity;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

public class VocabularyListActivity extends HFActivity{
	
	ArrayAdapter adapter;
	ArrayList<Word> selectedList = new ArrayList<>();
	
	public void onCreate(Bundle icicle) {
	    super.onCreate(icicle); 
	    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.layout_vocablist, null);
	    setContentView(layout);
	    
	    setupButtons(layout);
	    
	    addAllWordsToSelectedList();
	    if (Core.getDebugState() == false) {
	    	adapter = new VocabArrayAdapter(this, selectedList);
	    }else {
	    	adapter = new DebugVocabArrayAdapter(this, selectedList);	
	    }
	    ListView listView = setupList(layout);
		listView.setAdapter(adapter);
		
		setupSearchbar(layout);
	}	  

	@Override
	protected void onResume() {
		super.onResume();
		selectedList.clear();
		addAllWordsToSelectedList();
		if (adapter != null) adapter.notifyDataSetChanged();
	}
	
	
	//-------------------------------------------------------------------------------------------------------------------
	// PRIVATE METHODS
	//-------------------------------------------------------------------------------------------------------------------
	private void addAllWordsToSelectedList() {
		LexicalArrayList list = Core.getVocabularyBox().getActiveVocabularyList().getAllWords();
		for (int i = 0; i < list.size(); i++) {
			selectedList.add((Word)list.get(i));
		}
	}

	private void setupButtons(View parentView) {
		ImageButton backbutton = (ImageButton)parentView.findViewById(R.id.imageButton_right);
		backbutton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	    
	    Button b = (Button)parentView.findViewById(R.id.button1_vocab_list);
	    b.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
                 Intent intent = new Intent("android.intent.action.VOCABINPUTACTIVITY");
                 startActivity(intent);
             }
        });
	}
	
	private ListView setupList(View parent) {
		ListView listView = (ListView)parent.findViewById(R.id.listview_vocabList);
		listView.setLongClickable(true); 
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Word word = (Word)adapter.getItem(position);
				String item = word.getAllCharacters();
				System.out.println("" + item + " was pressed");
				Language activeLanguage = Core.getVocabularyBox().getActiveVocabularyList().getLanguage();
				if (activeLanguage.equals(Language.CHINESE)) {
					System.out.println("Chinese language found");
					Core.getSpeechManager().pronounceWords(word.getAllRomanizations(), Language.CHINESE);
				}else if (activeLanguage.equals(Language.KOREAN)) {
					System.out.println("Korean language found");
					Core.getSpeechManager().pronounceWords(word.getAllCharacters(), Language.KOREAN);
				}
				
			}
		});
		
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
					long id) {
				if (parent != null){
					Object obj = parent.getItemAtPosition(position);
					if (obj instanceof Word) {
						Word word = (Word)obj;
						Intent intent = new Intent("android.intent.action.EDITVOCABULARYACTIVITY");
						intent.putExtra("vocabID", "" + word.getId());
						startActivity(intent);
					}
				}
				return false;
			}
		});
		
		return listView;
	}
	
	private void setupSearchbar(View parent) {
		EditText searchbar = (EditText)parent.findViewById(R.id.editText1_vocab_list);
		
		searchbar.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}
			
			@Override
			public void afterTextChanged(final Editable s) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						selectedList.clear();
						String searchbarTextString = s.toString().toLowerCase();
						if (searchbarTextString.length() == 0) {
							Logger.log("String is empty", "VocabularyList");
							addAllWordsToSelectedList();
						}else {
							LexicalArrayList list = Core.getVocabularyBox().getActiveVocabularyList().getAllWords();
							System.out.println("Searchbar checks " + list.size() + " elements");
							for (int i = 0; i < list.size(); i++) {
								Word word = (Word)list.get(i);
								String vocablistEntryRomanizationString = word.getAllRomanizations().toLowerCase();
								String vocablistEntryTranslationString = word.getTranslationsString().toLowerCase();
								Logger.log("Searchbar filter search " + searchbarTextString + " in " + vocablistEntryRomanizationString + " and " + vocablistEntryTranslationString, "VOCABLIST");
								if (vocablistEntryRomanizationString.contains(searchbarTextString) | vocablistEntryTranslationString.contains(searchbarTextString)) {
									Logger.log("Filtered word is " + word.getAllRomanizations(), "VocabularyList");
									selectedList.add(word);
								}
							}	
						}
						runOnUiThread(new Runnable() {
							public void run() {
								adapter.notifyDataSetChanged();
							}
						});	
					}
				}).start();
			}
		});
	}
} 