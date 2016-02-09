package com.celesterspencer.adapters;

import java.util.ArrayList;

import com.celesterspencer.core.Word;
import com.celesterspencer.dummyclasses.AddReferenceButton;
import com.celesterspencer.holisticflashcards.R;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class FlashcardHorizontalViewAdapter extends BaseAdapter{
    private Context mContext;
    private ArrayList<Word> words;
    
    public FlashcardHorizontalViewAdapter(Context c, ArrayList<Word> words) {
        mContext = c;
        this.words = words;
    }

    public int getCount() {
        return words.size() + 1;
    }

    public Object getItem(int position) {
    	if (position == words.size()) {
    	    return new AddReferenceButton();
    	}
        return words.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
    	// recycle layout
    	View referenceView = convertView;
    	
    	// check for last item
    	if (position == words.size()) {
    		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	    referenceView = inflater.inflate(R.layout.entry_reference, parent, false);	
    	    return referenceView;
    	}
    	
    	// inflate layout
	    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    referenceView = inflater.inflate(R.layout.entry_vocab_reference, parent, false);

        // fill data
	    TextView hanziText = (TextView)referenceView.findViewById(R.id.textView1_reference_entry);
	    TextView translationText = (TextView)referenceView.findViewById(R.id.textView2_reference_entry);
	    hanziText.setText("");
	    translationText.setText("");
	    
	    Word word = words.get(position);
	    
	    //color every hanzi with respect to the pinyin tone transcription
	    for (int i = 0; i < word.length(); i++) {	    	
	    	//append hanzi
	    	String ch_temp = word.getCharacterAt(i);
	    	int color = word.getRomanizationAt(i).getColorOfWrittenPinyin();
	    	Spannable hanzi_temp = new SpannableString(ch_temp);
	    	hanzi_temp.setSpan(new ForegroundColorSpan(color), 0, hanzi_temp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	    	hanziText.append(hanzi_temp);
	    }
	    
	    // append translation
	    ArrayList<String> translations = word.getTranslations();
	    int bound = translations.size();
	    for(int ind = 0; ind < bound; ind++) {
	    	translationText.append(translations.get(ind));
	    	if (ind+1 < bound) translationText.append(" \n "); 
	    }
	    
        return referenceView;
    }
    
}
