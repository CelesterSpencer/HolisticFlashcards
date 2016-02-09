package com.celesterspencer.adapters;

import java.util.ArrayList;

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

public class VocabArrayAdapter extends ArrayAdapter<Word> {
  private final Activity context;
  private ArrayList<Word> words;

  static class ViewHolder {
	    public TextView characters;
	    public TextView romanization;
	    public TextView translation;
  }
  
  public VocabArrayAdapter(Activity context, ArrayList<Word> words) {
	    super(context, R.layout.entry_vocablist_vocab, words);
	    this.context = context;
	    this.words = words;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
	    View rowView = convertView;
	    
	    // reuse views
	    if (rowView == null) {
		      LayoutInflater inflater = context.getLayoutInflater();
		      rowView = inflater.inflate(R.layout.entry_vocablist_vocab, null);
		      // configure view holder
		      ViewHolder viewHolder = new ViewHolder();
		      viewHolder.characters = (TextView) rowView.findViewById(R.id.label1_vocab);
		      viewHolder.romanization = (TextView) rowView.findViewById(R.id.label2_vocab);
		      viewHolder.translation = (TextView) rowView.findViewById(R.id.label3_vocab);
		      rowView.setTag(viewHolder);
	    }
	
	    // fill data
	    ViewHolder holder = (ViewHolder) rowView.getTag();
	    Word word = words.get(position);
	    holder.characters.setText("");
	    holder.romanization.setText("");
	    holder.translation.setText(word.getTranslationsString());
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
	    return rowView;
  }
  
  

} 