package com.celesterspencer.activities;

import java.util.ArrayList;

import com.celesterspencer.adapters.SelectReferenceArrayAdapter;
import com.celesterspencer.core.Core;
import com.celesterspencer.core.Word;
import com.celesterspencer.holisticflashcards.R;
import com.celesterspencer.util.LexicalArrayList;
import com.celesterspencer.util.Lexicalsortable;
import com.celesterspencer.util.Logger;
import com.devsmart.android.ui.HorizontalListView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

// tutorial : http://www.vogella.com/tutorials/AndroidListView/article.html
// next time start with 9. Storing selection of a view
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;

public class SelectReferencesActivity extends HFActivity {
	
	SelectReferenceArrayAdapter m__adapter;
	ArrayList<Word> m__selectedList = new ArrayList<>();
	
	  public void onCreate(Bundle savedInstanceState) {
		    super.onCreate(savedInstanceState); 

		    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.layout_vocablist_vocabs, null);
			
		    setup(view);
		    setupButton(view);
		    setupSearchbar(view);

		    setContentView(view);
	  }	  

		@Override
		protected void onResume() {
			super.onResume();
			m__selectedList.clear();
			for (Lexicalsortable word : Core.getDictionary().getAllWords()) {
				m__selectedList.add(((Word)word));
			}
			if (m__adapter != null) m__adapter.notifyDataSetChanged();
		}
		
		
		//-------------------------------------------------------------------------------------------------------------------
		// PRIVATE METHODS
		//-------------------------------------------------------------------------------------------------------------------	
		private void setup(View view){
			ListView listview = (ListView)view.findViewById(R.id.list_selectedlist);
		    
		    for (Lexicalsortable word : Core.getDictionary().getAllWords()) {
		    	m__selectedList.add((Word)word);
		    }
		    
		    m__adapter = new SelectReferenceArrayAdapter(this, m__selectedList);
		    listview.setAdapter(m__adapter);
		}
		
		private void setupButton(View layout) {
		    Button b = (Button)layout.findViewById(R.id.button1_vocab_selectlist);
		    b.setOnClickListener(new View.OnClickListener() {
	             public void onClick(View v) {
	            	 m__selectedList.clear();
	            	 LexicalArrayList allWords = Core.getDictionary().getAllWords();
	            	 for(int i = 0; i < allWords.size(); i++) {
						m__selectedList.add((Word)allWords.get(i));
	            	 }	
	                 String resultReferences = m__adapter.getSelectedReferences();
	                 Intent intent = getIntent();
	                 intent.putExtra("references", resultReferences);
	                 setResult(RESULT_OK, intent);
	                 finish();
	             }
	        });
		}
		
		private void setupSearchbar(View layout) {
		    EditText searchbar = (EditText)layout.findViewById(R.id.editText1_select_reference);
			
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
							m__selectedList.clear();
							String editTextString = s.toString();
							if (editTextString.length() == 0) {
								System.out.println("String is empty");
								for(Lexicalsortable word : Core.getDictionary().getAllWords()) {
									m__selectedList.add((Word)word);
								}	
							}else {
								for(Lexicalsortable word : Core.getDictionary().getAllWords()) {
									if (((Word)word).getAllRomanizations().contains(editTextString)) {
										Logger.log("Filtered word is " + ((Word)word).getAllRomanizations(), "SELECTEDREFERENCES");
										m__selectedList.add(((Word)word));
									}
								}	
							}
							runOnUiThread(new Runnable() {
								public void run() {
									m__adapter.notifyDataSetChanged();
								}
							});	
						}
					}).start();
				}
			});
		}
} 