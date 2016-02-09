package com.celesterspencer.activities;

import java.util.ArrayList;

import com.celesterspencer.core.Core;
import com.celesterspencer.core.WordList;
import com.celesterspencer.holisticflashcards.R;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.TokenPair;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class DropboxActivity extends HFActivity{

    private DropboxAPI<AndroidAuthSession> dropbox;
    
    private final static String FILE_DIR = "/";
    private final static String DROPBOX_NAME = "dropbox_prefs";
    private final static String ACCESS_KEY = "1y1a2y2fhapl3j2";
    private final static String ACCESS_SECRET = "2dny05p4pivhvpw";
    private boolean isLoggedIn;
    private Button downloadFile;
    private Button uploadFile;
     
 
    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.dropbox_layout, null);
        
        setupButtons(layout);
        setupDropBoxSession(layout);
        
        setContentView(layout);
    }
 
    @Override
    protected void onResume() {
        super.onResume();
        
        // retrieve DBSession if possible
        AndroidAuthSession session = dropbox.getSession();
        if (session.authenticationSuccessful()) {
            try {
                session.finishAuthentication();
 
                TokenPair tokens = session.getAccessTokenPair();
                SharedPreferences prefs = getSharedPreferences(DROPBOX_NAME, 0);
                Editor editor = prefs.edit();
                editor.putString(ACCESS_KEY, tokens.key);
                editor.putString(ACCESS_SECRET, tokens.secret);
                editor.commit();
                loggedIn(true);
            } catch (IllegalStateException e) {
                Toast.makeText(this, "Error during Dropbox authentication",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
   
    public void changeText(String text) {
    	TextView textView = (TextView)findViewById(R.id.textView1);
    	textView.setText(text);
    }
    
    public void startLoading() {
    	runOnUiThread(new Runnable() {	
    		@Override
			public void run() {
		    	ProgressBar bar = (ProgressBar)findViewById(R.id.loadingPanel);
		    	bar.setVisibility(View.VISIBLE);
    		}	
    	});
    }
    
    public void endLoading() {
    	runOnUiThread(new Runnable() {	
    		@Override
			public void run() {
    			ProgressBar bar = (ProgressBar)findViewById(R.id.loadingPanel);
    			bar.setVisibility(View.INVISIBLE);
    		}	
    	});	
    }
    
    
    
	//-------------------------------------------------------------------------------------------------------------------
	// PRIVATE METHODS
	//-------------------------------------------------------------------------------------------------------------------
    private void setupButtons(View layout) {
    	
    	//setup download button 
        downloadFile = (Button)layout.findViewById(R.id.button2_db);
        downloadFile.setOnClickListener(new View.OnClickListener() {		
 			@Override
 			public void onClick(View v) {
 				if (isLoggedIn) {
 					startLoading();
 	                new Thread(new Runnable() {
						
						@Override
						public void run() {
							Core.getIoManager().downloadVocabularyListsFromDropBox(dropbox, FILE_DIR);
							endLoading();
						}
					}).start();
 	            } else {
 	                dropbox.getSession().startAuthentication(getApplicationContext());
 	            }
 			}
 		});
         
        // setup upload button
        uploadFile = (Button)layout.findViewById(R.id.button1_db);
        uploadFile.setOnClickListener(new View.OnClickListener() {		
 			@Override
 			public void onClick(View v) {
 				if (isLoggedIn) {
 					startLoading();
 	                new Thread(new Runnable() {
						
						@Override
						public void run() {
							Core.getIoManager().uploadVocabularyListsToDropBox(dropbox, FILE_DIR);
							endLoading();
						}
					}).start();
 	            } else {
 	                dropbox.getSession().startAuthentication(getApplicationContext());
 	            }
 			}
 		});
    }
    
    private void setupDropBoxSession(View layout) {
    	loggedIn(false);
    	 
        AndroidAuthSession session;
        AppKeyPair pair = new AppKeyPair(ACCESS_KEY, ACCESS_SECRET);
 
        SharedPreferences prefs = getSharedPreferences(DROPBOX_NAME, 0);
        String key = prefs.getString(ACCESS_KEY, null);
        String secret = prefs.getString(ACCESS_SECRET, null);
 
        if (key != null && secret != null) {
            AccessTokenPair token = new AccessTokenPair(key, secret);
            session = new AndroidAuthSession(pair, AccessType.APP_FOLDER, token);
            loggedIn(true);
        } else {
            session = new AndroidAuthSession(pair, AccessType.APP_FOLDER);
        }
 
        dropbox = new DropboxAPI<AndroidAuthSession>(session);
    }
    
    private void loggedIn(boolean isLogged) {
        isLoggedIn = isLogged;
    }
}
