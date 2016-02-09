package com.celesterspencer.adapters;

import java.util.ArrayList;

import com.celesterspencer.core.Core;
import com.celesterspencer.core.Word;
import com.celesterspencer.holisticflashcards.R;

import android.app.Activity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DebugVocabArrayAdapter extends ArrayAdapter<Word> {
  private final Activity context;
  private ArrayList<Word> words;

  static class ViewHolder {
	  	public TextView ids;
	    public TextView characters;
	    public TextView romanization;
	    public TextView translation;
	    public TextView references;
	    public TextView numberOfRepetitions;
	    public TextView eFactor;
	    public TextView interRepetitionInterval;
	    public TextView dateOfReview;
  }
  
  public DebugVocabArrayAdapter(Activity context, ArrayList<Word> words) {
	    super(context, R.layout.entry_vocablist_vocab_debug, words);
	    this.context = context;
	    this.words = words;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
	    View rowView = convertView;
	    
	    // reuse views
	    if (rowView == null) {
		      LayoutInflater inflater = context.getLayoutInflater();
		      rowView = inflater.inflate(R.layout.entry_vocablist_vocab_debug, null);
		      // configure view holder
		      ViewHolder viewHolder = new ViewHolder();
		      viewHolder.ids = (TextView) rowView.findViewById(R.id.label0_vocab);
		      viewHolder.characters = (TextView) rowView.findViewById(R.id.label1_vocab);
		      viewHolder.romanization = (TextView) rowView.findViewById(R.id.label2_vocab);
		      viewHolder.translation = (TextView) rowView.findViewById(R.id.label3_vocab);
		      viewHolder.references = (TextView) rowView.findViewById(R.id.label4_vocab);
		      viewHolder.numberOfRepetitions = (TextView) rowView.findViewById(R.id.label5_vocab);
		      viewHolder.eFactor = (TextView) rowView.findViewById(R.id.label6_vocab);
		      viewHolder.interRepetitionInterval = (TextView) rowView.findViewById(R.id.label7_vocab);
		      viewHolder.dateOfReview = (TextView) rowView.findViewById(R.id.label8_vocab);
		      rowView.setTag(viewHolder);
	    }
	
	    // fill data
	    ViewHolder holder = (ViewHolder) rowView.getTag();
	    Word word = words.get(position);
	    holder.ids.setText("" + word.getId());
	    holder.characters.setText("");
	    holder.romanization.setText("");
	    holder.translation.setText(word.getTranslationsString());
	    holder.numberOfRepetitions.setText("" + word.getNumberOfRepetitions());
	    holder.eFactor.setText("" + word.getEFactor());
	    holder.interRepetitionInterval.setText("" + word.getInterRepetitionInterval());
	    holder.dateOfReview.setText(word.getDateOfReview());
	    for (int i = 0; i < word.length(); i++) {
	    	String charactersString = word.getCharacterAt(i);
	    	int color =  word.getRomanizationAt(i).getColorOfWrittenPinyin();
	    	
	    	//append hanzi
	    	Spannable charactersSpannable = new SpannableString(charactersString);
	    	charactersSpannable.setSpan(new ForegroundColorSpan(color), 0, charactersSpannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	    	holder.characters.append(charactersSpannable);
	    	
	    	// append pinyin
	    	Spannable pinyin_temp = new SpannableString(word.getRomanizationAt(i).getWrittenRomanizationWithTonemark());
	    	pinyin_temp.setSpan(new ForegroundColorSpan(color), 0, pinyin_temp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	    	holder.romanization.append(pinyin_temp);
	    }
	    if (position % 2 == 0) {
	    	rowView.setBackgroundColor(context.getResources().getColor(R.color.hf_lightgray));
	    }else {
	    	rowView.setBackgroundColor(context.getResources().getColor(R.color.hf_gray));
	    }
	    
	    // add all referenced words
	    holder.references.setText("");
	    for (Long refId : word.getReferences()) {
	    	Word referencedWord = Core.getDictionary().getWordByID(refId);
	    	if (referencedWord != null) {
	    		holder.references.append("" + referencedWord.getAllCharacters() + ", ");
	    	}else {
	    		holder.references.append("[" + refId + "?] , ");
	    	}
	    }
	    
	    return rowView;
  }
  
} 