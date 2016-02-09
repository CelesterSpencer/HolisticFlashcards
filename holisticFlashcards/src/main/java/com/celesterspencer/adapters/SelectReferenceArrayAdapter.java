package com.celesterspencer.adapters;

import java.util.ArrayList;
import java.util.HashSet;

import com.celesterspencer.core.Romanization;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class SelectReferenceArrayAdapter extends ArrayAdapter<Word> {
  private final Activity m__context;
  private ArrayList<Word> m__words;
  private HashSet<Long> m__selectedWordsIds;

  static class ViewHolder {
	    public TextView characters;
	    public TextView romanization;
	    public CheckBox check;
  }
  
  public SelectReferenceArrayAdapter(Activity context, ArrayList<Word> words) {
	    super(context, R.layout.entry_selectable_reference, words);
	    m__context = context;
	    m__words = words;
	    m__selectedWordsIds = new HashSet<>();
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
	    View rowView = convertView;
	    
	    // reuse views
	    if (rowView == null) {
	    	LayoutInflater inflater = m__context.getLayoutInflater();
	    	rowView = inflater.inflate(R.layout.entry_select_vocab, null);
	      
	    	// configure view holder
	    	final ViewHolder viewHolder = new ViewHolder();
	    	viewHolder.characters = (TextView) rowView.findViewById(R.id.label1_selecting);
	    	viewHolder.romanization = (TextView) rowView.findViewById(R.id.label2_selecting);
	    	viewHolder.check = (CheckBox) rowView.findViewById(R.id.check_selecting);
	    	viewHolder.check.setOnCheckedChangeListener(new OnCheckedChangeListener() {	
	    		@Override
	    		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	    			Word select = (Word) viewHolder.check.getTag();
	    			if(buttonView.isChecked()) {
	    				m__selectedWordsIds.add((Long)select.getId());
	    			}else {
	    				m__selectedWordsIds.remove((Long)select.getId());
	    			}
	    		}
	      	});
		  	rowView.setTag(viewHolder);
		  	viewHolder.check.setTag(m__words.get(position));
	    }else {
	    	((ViewHolder) rowView.getTag()).check.setTag(m__words.get(position));
	    }
	
	    // fill data
	    ViewHolder holder = (ViewHolder) rowView.getTag();
	    Word word = m__words.get(position);
	    holder.characters.setText("");
	    holder.romanization.setText("");
	    holder.check.setChecked(m__selectedWordsIds.contains((Long)word.getId()));
	    for (int i = 0; i < word.length(); i++) {
	    	// get words attributes
	    	String characters = word.getCharacterAt(i);
	    	String romanization = word.getRomanizationAt(i).getWrittenRomanization();
	    	int color = word.getRomanizationAt(i).getColorOfWrittenPinyin();
	    	
	    	//append hanzi
	    	Spannable characters_temp = new SpannableString(characters);
	    	characters_temp.setSpan(new ForegroundColorSpan(color), 0, characters_temp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	    	holder.characters.append(characters_temp);
	    	
	    	// append pinyin
	    	Spannable romanization_temp = new SpannableString(romanization);
	    	romanization_temp.setSpan(new ForegroundColorSpan(color), 0, romanization_temp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	    	holder.romanization.append(romanization_temp);
	    }
	    return rowView;
  }
  
  public String getSelectedReferences() {
	  String selectedReferences = "";
	  for (int ind = 0; ind < m__words.size(); ind++) {
		  Word word = m__words.get(ind);
		  if (m__selectedWordsIds.contains(word.getId())) {
			  selectedReferences += word.getId();
			  if (ind < m__words.size()) selectedReferences += ",";
		  }
	  }
	  return selectedReferences;
  }

} 