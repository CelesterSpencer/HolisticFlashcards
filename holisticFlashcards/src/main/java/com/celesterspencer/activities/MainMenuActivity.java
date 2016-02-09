package com.celesterspencer.activities;

import java.util.ArrayList;

import com.celesterspencer.adapters.ExpandableVocabularyListAdapter;
import com.celesterspencer.core.Core;
import com.celesterspencer.core.WordList;
import com.celesterspencer.dummyclasses.AddReferenceButton;
import com.celesterspencer.holisticflashcards.R;
import com.celesterspencer.test.TestIO;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;



public class MainMenuActivity extends HFActivity {

	ExpandableVocabularyListAdapter m__adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View m__view = inflater.inflate(R.layout.layout_mainmenu, null, false);
		
		
		m__adapter = new ExpandableVocabularyListAdapter(this, Core.getVocabularyBox().getAllLists());
		
		setupListView(m__view, m__adapter);
		setupButton(m__view);
		
		setContentView(m__view);
	}

	@Override
	protected void onStart() {
		super.onStart();
		
		m__adapter.setSelectedPosition(-1);
		m__adapter.notifyDataSetChanged();
		
	}
	
	
	
	//-------------------------------------------------------------------------------------------------------------------
	// PRIVATE METHODS
	//-------------------------------------------------------------------------------------------------------------------	
	private void setupListView(View layout, final ExpandableVocabularyListAdapter adapter) {
		ListView listView = (ListView)layout.findViewById(R.id.list_main);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id0) {
				adapter.setSelectedPosition(position);
				adapter.notifyDataSetChanged();

				Object viewObj = adapter.getItem(position);
				if (viewObj == null) {
					// do nothing
				}else if (viewObj instanceof WordList) {
					
					// set listname in layout
					WordList list = (WordList) viewObj;
					// set selected list as active
					Core.getVocabularyBox().setActiveVocabularyList(list);
					
				}else if (viewObj instanceof AddReferenceButton) {
					Intent intent = new Intent("android.intent.action.CREATEVOCABULARYLISTACTIVITY");
					startActivity(intent);
				}
			}
		});
		listView.setAdapter(adapter);
	}
	
	private void setupButton(View layout) {
		ImageButton syncButton = (ImageButton)layout.findViewById(R.id.imageButton_right);
		syncButton.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent("android.intent.action.DROPBOXACTIVITY");
				startActivity(intent);
			}
		});
	}
	
}
