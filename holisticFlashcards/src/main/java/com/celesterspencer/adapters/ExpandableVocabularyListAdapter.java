package com.celesterspencer.adapters;

import java.util.ArrayList;

import com.celesterspencer.core.Core;
import com.celesterspencer.core.Language;
import com.celesterspencer.core.WordList;
import com.celesterspencer.dummyclasses.AddReferenceButton;
import com.celesterspencer.holisticflashcards.R;
import com.celesterspencer.util.DateHandler;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class ExpandableVocabularyListAdapter extends BaseAdapter {

	Context m__context;
	ArrayList<WordList> m__lists;
	int m__selectedPosition = -1;

	public ExpandableVocabularyListAdapter(Context c, ArrayList<WordList> lists) {
		m__context = c;
		this.m__lists = lists;
	}
	
	public void setSelectedPosition(int selectedPosition) {
		m__selectedPosition = selectedPosition;
	}
	
	public int getCount() {
		return m__lists.size() + 1;
	}	

	public Object getItem(int position) {
		if (position < m__lists.size()) {
			return m__lists.get(position);
		}
		else {
			return new AddReferenceButton();
		}
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		// recycle layout
		View referenceView;
	
		// list
		if (position < m__lists.size()) {
			referenceView = getOpenList(convertView, parent, position);
		}
		// check for add reference button
		else {
			referenceView = getAddListButton(convertView, parent);
		}
		
		return referenceView;
	}
	
	public View getAddListButton (View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) m__context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View addListButton = inflater.inflate(R.layout.entry_add_vocablist, parent, false);
		return addListButton;
	}
	
	public View getOpenList (View convertView, ViewGroup parent, int position) {
		// inflate layout
		LayoutInflater inflater = (LayoutInflater) m__context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View openListView = inflater.inflate(R.layout.entry_expandable_menu, parent, false);
		
		// get the list
		final WordList list = m__lists.get(position);
		
		//set color of selected list
		if (position == m__selectedPosition) {
			View child = ((ViewGroup)openListView).getChildAt(0);
			child.setBackgroundColor(m__context.getResources().getColor(R.color.hf_lightgreen));
		}
		
		// set language flag
		ImageView flagView = (ImageView)openListView.findViewById(R.id.imageView1_expand);
		Language language = list.getLanguage();
		switch (language) {
		case CHINESE:
			flagView.setImageResource(R.drawable.china_flag);
			break;
        case KOREAN:
        	flagView.setImageResource(R.drawable.korea_flag);
			break;	
		}
		
		//get textviews
		TextView listNameText = (TextView) openListView.findViewById(R.id.textView1_expand);
		TextView vocabularyNumberText = (TextView) openListView.findViewById(R.id.textView2_expand);
		
		
		
		//fill textviews with data
		listNameText.setText(list.getListName());
		
		vocabularyNumberText.setText("");
		// -------------------------------------------------------------------------------------------
		// TODO Count number of remaining vocab of this list
		// 		and set it in vocabularyNumberText
		// -------------------------------------------------------------------------------------------
		
			
		// if list is selected
		if (position == m__selectedPosition) {
			ImageButton flashcards = (ImageButton)openListView.findViewById(R.id.imageButton_flashcards);
			flashcards.setVisibility(View.VISIBLE);
			flashcards.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//TODO access flashcards only if vocab for learning is available
					DateHandler dateHandler = new DateHandler();
					if (dateHandler.compareTo(Core.getVocabularyBox().getActiveVocabularyList().getDateOfLastFinish()) > 0) {
						Intent intent = new Intent("android.intent.action.FLASHCARDACTIVITY");
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						Core.getApplicationContext().getContext().startActivity(intent);
					}else {
						Core.getIoManager().displayMessage("Thats enough for today, continue tomorrow!");
					}
				}
			});
			ImageButton settings = (ImageButton)openListView.findViewById(R.id.imageButton_settings);
			settings.setVisibility(View.VISIBLE);
			settings.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent("android.intent.action.VOCABULARYLISTSETTINGSACTIVITY");
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					Core.getApplicationContext().getContext().startActivity(intent);
				}
			});
			ImageButton vocabs = (ImageButton)openListView.findViewById(R.id.imageButton_vocabulary);
			vocabs.setVisibility(View.VISIBLE);
			vocabs.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent("android.intent.action.VOCABULARYLISTACTIVITY");
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					Core.getApplicationContext().getContext().startActivity(intent);
				}
			});
		}
		else {
			ImageButton flashcards = (ImageButton)openListView.findViewById(R.id.imageButton_flashcards);
			flashcards.setVisibility(View.GONE);
			ImageButton vocabs = (ImageButton)openListView.findViewById(R.id.imageButton_vocabulary);
			vocabs.setVisibility(View.GONE);
			ImageButton settings = (ImageButton)openListView.findViewById(R.id.imageButton_settings);
			settings.setVisibility(View.GONE);
		}
		
		return openListView;
	}
	
}
