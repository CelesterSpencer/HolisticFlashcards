package com.celesterspencer.activities;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import com.celesterspencer.adapters.FlashcardHorizontalViewAdapter;
import com.celesterspencer.core.Core;
import com.celesterspencer.core.Session;
import com.celesterspencer.core.Word;
import com.celesterspencer.holisticflashcards.R;
import com.celesterspencer.util.DrawingView;
import com.devsmart.android.ui.HorizontalListView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class FlashcardActivity extends HFActivity{
	
	
	private FlashcardHorizontalViewAdapter adapter;
	private ArrayList<Word> m__references = new ArrayList<Word>();
	private String romanizedSpeech;
	private Session session;
	
	// lists
	Word recentVocab = null;
	boolean isFlipped = false;

	
	
	//-------------------------------------------------------------------------------------------------------------------
	// ANDROID CYCLE METHODS
	//-------------------------------------------------------------------------------------------------------------------	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Check whether we're recreating a previously destroyed instance
	    recreateSession(savedInstanceState);
		
		//get layout
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View flashcard_layout = inflater.inflate(R.layout.layout_flashcard, null);	
		
		// setup listview and adapter
		HorizontalListView view = (HorizontalListView)flashcard_layout.findViewById(R.id.listview_horizontal_flashcard);
		adapter = new FlashcardHorizontalViewAdapter(this, m__references);
		view.setAdapter(adapter);
		
		// get drawing view
		final DrawingView drawingView = (DrawingView)findViewById(R.id.drawingView);
		
		//set button onclicklistener
		setupButtons(flashcard_layout);
		
		// set layout
		setContentView(flashcard_layout);
	}

	@Override
	protected void onStart() {
		super.onStart();
		System.out.println("On Start");
		hideFields();
		recentVocab = session.getRecentVocab();
		fillView();
		if (isFlipped) {
			showFields();	
		}
	}	
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		System.out.println("On saved instance state");
	    // Save the user's current game state
		System.out.println("Save instance state");
		boolean isRunning = !session.isSessionFinished();
	    savedInstanceState.putBoolean("IS_SESSION_RUNNING", isRunning);
	    savedInstanceState.putBoolean("IS_FLIPPED", isFlipped);
	    if (isRunning) {
	    	savedInstanceState.putString("NEW_VOCAB", session.getNewVocabString());
	    	savedInstanceState.putString("WRONG_VOCAB", session.getWrongVocabString());
	    	savedInstanceState.putString("OLD_VOCAB", session.getOldVocabString());
	    	savedInstanceState.putString("RECENT_VOCAB", session.getRecentVocabString());	
	    }
	    // Always call the superclass so it can save the view hierarchy state
	    super.onSaveInstanceState(savedInstanceState);
	}
	
	


	
	
	
	//-------------------------------------------------------------------------------------------------------------------
	// PRIVATE METHODS
	//-------------------------------------------------------------------------------------------------------------------
	public void recreateSession(Bundle savedInstanceState) {
		System.out.println("Start Recreate Session");
		
		// if old session is running load it
		session = new Session();
		if (savedInstanceState != null) {
			System.out.println("Load old session");
	        // Restore value of members from saved state
	        boolean isSessionRunning = savedInstanceState.getBoolean("IS_SESSION_RUNNING",false);
	        System.out.println("Session is still running: " + isSessionRunning);
	        isFlipped = savedInstanceState.getBoolean("IS_FLIPPED", false);
	        if (isSessionRunning) {
	        	String newVocab = savedInstanceState.getString("NEW_VOCAB");
	        	String wrongVocab = savedInstanceState.getString("WRONG_VOCAB");
	        	String oldVocab = savedInstanceState.getString("OLD_VOCAB");
	        	String recentVocab = savedInstanceState.getString("RECENT_VOCAB");
	        	session = new Session();
	        	session.restoreSession(newVocab, wrongVocab, oldVocab, recentVocab);
	        }
	        
	    // else setup a new session    
	    }else {
	    	System.out.println("Setup new session");
	    	session.setUp(Core.getVocabularyBox().getActiveVocabularyList());
	    }
	}
	
	private void setupButtons(View layout) {
		System.out.println("Setup Buttons");
		// get Buttons
		Button flipFlashcard_button = (Button)layout.findViewById(R.id.button1_flashcard);
		Button againFlashcard_button = (Button)layout.findViewById(R.id.button2_flashcard);
		Button moderateFlashcard_button = (Button)layout.findViewById(R.id.button3_flashcard);
		Button easyFlashcard_button = (Button)layout.findViewById(R.id.button4_flashcard);
		ImageButton backButton = (ImageButton)layout.findViewById(R.id.imageButton_right);
		ImageButton clearDrawingButton = (ImageButton)layout.findViewById(R.id.imageButton_clear);
		
		System.out.println("Set Buttonlistener");
		//setup listener
		flipFlashcard_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				isFlipped = true;
				showFields();
			}
		});
		
		againFlashcard_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				isFlipped = false;
				hideFields();
				session.again();
				recentVocab = session.getRecentVocab();
				fillView();
			}
		});
		
		moderateFlashcard_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				isFlipped = false;
				hideFields();
				session.fine();
				recentVocab = session.getRecentVocab();
				fillView();
			}
		});
		
		easyFlashcard_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				isFlipped = false;
				hideFields();
				session.easy();
				recentVocab = session.getRecentVocab();
				fillView();
			}
		});
		
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		clearDrawingButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Word word = recentVocab;
				Intent intent = new Intent("com.holisticflashcards.activities.EDITVOCABULARY");
				intent.putExtra("vocabID", "" + word.getId());
				startActivity(intent);
			}
		});
	}
	
	
	
	private void sessionFinished() {
		System.out.println("Session Finished");
		Core.getVocabularyBox().getActiveVocabularyList().setDateOfLastFinish();
		Core.getIoManager().displayMessage("Finished learning Session!");
		finish();
	}
	
	private void hideFields() {
		System.out.println("Hide Fields");
		TextView pinyin = (TextView)findViewById(R.id.textView2_flashcard);
		TextView answer = (TextView)findViewById(R.id.textView3_flashcard);
		TextView mnemonics = (TextView)findViewById(R.id.textView4_flashcard);
		HorizontalListView listview = (HorizontalListView)findViewById(R.id.listview_horizontal_flashcard);
		Button flipFlashcard_button = (Button)findViewById(R.id.button1_flashcard);
		Button againFlashcard_button = (Button)findViewById(R.id.button2_flashcard);
		Button moderateFlashcard_button = (Button)findViewById(R.id.button3_flashcard);
		Button easyFlashcard_button = (Button)findViewById(R.id.button4_flashcard);
		DrawingView drawingView = (DrawingView)findViewById(R.id.drawingView);
		
		// hide views
		pinyin.setVisibility(View.GONE);
		answer.setVisibility(View.GONE);
		mnemonics.setVisibility(View.GONE);
		listview.setVisibility(View.GONE);
		flipFlashcard_button.setVisibility(View.VISIBLE);
		againFlashcard_button.setVisibility(View.GONE);
		moderateFlashcard_button.setVisibility(View.GONE);
		easyFlashcard_button.setVisibility(View.GONE);
		drawingView.setVisibility(View.VISIBLE);
		drawingView.clear();
	}
	
	private void showFields() {
		System.out.println("Show Fields");
		TextView pinyin = (TextView)findViewById(R.id.textView2_flashcard);
		TextView answer = (TextView)findViewById(R.id.textView3_flashcard);
		TextView mnemonics = (TextView)findViewById(R.id.textView4_flashcard);
		HorizontalListView listview = (HorizontalListView)findViewById(R.id.listview_horizontal_flashcard);
		Button flipFlashcard_button = (Button)findViewById(R.id.button1_flashcard);
		Button wrongFlashcard_button = (Button)findViewById(R.id.button2_flashcard);
		Button moderateFlashcard_button = (Button)findViewById(R.id.button3_flashcard);
		Button easyFlashcard_button = (Button)findViewById(R.id.button4_flashcard);
		DrawingView drawingView = (DrawingView)findViewById(R.id.drawingView);
		
		// hide views
		pinyin.setVisibility(View.VISIBLE);
		answer.setVisibility(View.VISIBLE);
		mnemonics.setVisibility(View.VISIBLE);
		listview.setVisibility(View.VISIBLE);
		flipFlashcard_button.setVisibility(View.GONE);
		wrongFlashcard_button.setVisibility(View.VISIBLE);
		moderateFlashcard_button.setVisibility(View.VISIBLE);
		easyFlashcard_button.setVisibility(View.VISIBLE);
		drawingView.setVisibility(View.VISIBLE);
		
	    //speak pinyin 
	    Core.getSpeechManager().pronounceWords(romanizedSpeech, Core.getVocabularyBox().getActiveVocabularyList().getLanguage());
	}
	
	private void fillView() {
		System.out.println("Fill View");
		TextView newVocabNumber = (TextView)findViewById(R.id.textViewNew_flashcard);
		TextView wrongVocabNumber = (TextView)findViewById(R.id.textViewWrong_flashcard);
		TextView oldVocabNumber = (TextView)findViewById(R.id.textViewOld_flashcard);
		
	    newVocabNumber.setText("" + session.getNumberOfNewVocab());
	    wrongVocabNumber.setText("" + session.getNumberOfWrongVocab());
	    oldVocabNumber.setText("" + session.getNumberOfOldVocab());
		
		// if no vocabulary is available end session
		if (recentVocab == null) {
			sessionFinished();
		}else {
			
			//get views
			TextView hanzi = (Core.getVocabularyBox().getActiveVocabularyList().isTranslationQuestion()) ? (TextView)findViewById(R.id.textView3_flashcard) : (TextView)findViewById(R.id.textView1_flashcard);
			TextView pinyin = (TextView)findViewById(R.id.textView2_flashcard);
			TextView translation = (Core.getVocabularyBox().getActiveVocabularyList().isTranslationQuestion()) ? (TextView)findViewById(R.id.textView1_flashcard) : (TextView)findViewById(R.id.textView3_flashcard);
			TextView mnemonics = (TextView)findViewById(R.id.textView4_flashcard);
			
			// fill with data
			hanzi.setText("");
		    pinyin.setText("");
		    romanizedSpeech = "";
		    // set hanzi and pinyin
		    for (int i = 0; i < recentVocab.length(); i++) {
		    	String ch_temp = recentVocab.getCharacterAt(i);
		    	int colorWritten = recentVocab.getRomanizationAt(i).getColorOfWrittenPinyin();
		    	
		    	//append hanzi
		    	Spannable hanzi_temp = new SpannableString(ch_temp);
		    	if (Core.getVocabularyBox().getActiveVocabularyList().areCharacteresColored()) {
		    		hanzi_temp.setSpan(new ForegroundColorSpan(colorWritten), 0, hanzi_temp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		    	}else {
		    		hanzi_temp.setSpan(new ForegroundColorSpan(Color.BLACK), 0, hanzi_temp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		    	}
		    	hanzi.append(hanzi_temp);
		    	
		    	// append pinyin
		    	Spannable pinyin_temp = new SpannableString(recentVocab.getRomanizationAt(i).getWrittenRomanizationWithTonemark());
		    	pinyin_temp.setSpan(new ForegroundColorSpan(colorWritten), 0, pinyin_temp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		    	pinyin.append(pinyin_temp);
		    	
		    	//add special tonemarks in case spoken and written sound are different
		    	if (recentVocab.getRomanizationAt(i).isSpokenAndWrittenToneDifferent()) {
		    		Spannable toneMark = new SpannableString("*");
		    		toneMark.setSpan(new ForegroundColorSpan(recentVocab.getRomanizationAt(i).getColorOfSpokenPinyin()), 0, toneMark.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			    	 if (Core.getVocabularyBox().getActiveVocabularyList().areCharacteresColored())hanzi.append(toneMark);
		    		pinyin.append(toneMark);
		    	}
		    	pinyin.setTextSize(TypedValue.COMPLEX_UNIT_PX, 500);
		    	//append spoken pinyin
		    	romanizedSpeech += recentVocab.getRomanizationAt(i).getSpokenRomanization() + " ";
		    }
		    translation.setText("");
		    int translationsSize = recentVocab.getTranslations().size();
		    for (int ind = 0; ind < translationsSize; ind++) {
		    	translation.append(recentVocab.getTranslations().get(ind));
		    	if (ind + 1 < translationsSize) translation.append(", ");
		    }
		    
		    // set mnemonics text
		    mnemonics.setText("");
		    String mnemonicsString = recentVocab.getMnemonic();
		    String temp = "";
		    boolean isDoubleQuotesOpen = false;
		    boolean isQuoteOpen = false;
		    while (mnemonicsString.length() != 0) {
		    	String lookahead = mnemonicsString.substring(0,1);
		    	if (mnemonicsString.length() > 1) {
		    		mnemonicsString = mnemonicsString.substring(1, mnemonicsString.length());
		    		System.out.println("Mnemonicsstring is now " + mnemonicsString);
		    	}else {
		    		mnemonicsString = "";
		    	}
		    	System.out.println("Lookahead is " + lookahead);
		    	
		    	if (lookahead.equals("\"")) {
		    		if (isDoubleQuotesOpen) {
		    			Spannable keyword = new SpannableString(temp);
			    		keyword.setSpan(new ForegroundColorSpan(Color.argb(255, 200, 200, 200)), 0, keyword.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		    			mnemonics.append(keyword);
		    			temp = "";
		    			isDoubleQuotesOpen = false;
		    		}else {
		    			mnemonics.append(temp);
		    			temp = "";
		    			isDoubleQuotesOpen = true;
		    		}
		    	}else if (lookahead.equals("\'")) {
		    		if (isQuoteOpen) {
		    			Spannable keyword = new SpannableString(temp);
			    		keyword.setSpan(new ForegroundColorSpan(Color.argb(255, 200, 100, 100)), 0, keyword.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		    			mnemonics.append(keyword);
		    			temp = "";
		    			isQuoteOpen = false;
		    		}else {
		    			mnemonics.append(temp);
		    			temp = "";
		    			isQuoteOpen = true;
		    		}	
		    	}else {
		    		temp += lookahead;
		    	}
		    }
		    if (temp.length() != 0) {
		    	mnemonics.append(temp);
		    }	    
		    
		    //set references
		    m__references.clear();
		    System.out.println("word has references: " + recentVocab.getReferencesString() + " and size: " + recentVocab.getReferences().size());
		    for (Long refID : recentVocab.getReferences()) {
		    	System.out.println("FlashcardActivity got reference id " + refID);
		    	Word refWord = Core.getDictionary().getWordByID(refID);
		    	if (refWord != null) {
		    		m__references.add(refWord);
		    	}
		    }
		    
		    adapter.notifyDataSetChanged();
		}
	}
}
