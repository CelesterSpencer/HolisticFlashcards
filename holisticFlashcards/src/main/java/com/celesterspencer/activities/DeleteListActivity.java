package com.celesterspencer.activities;

import com.celesterspencer.holisticflashcards.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DeleteListActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.delete_list_layout);
		Button yesButton = (Button)findViewById(R.id.button1_delete_list);
		Button noButton = (Button)findViewById(R.id.button2_delete_list);
		
		yesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = getIntent();
                intent.putExtra("isdeleted", "yes");
                setResult(RESULT_OK, intent);
                finish();
            }
       });
		
		noButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = getIntent();
                intent.putExtra("isdeleted", "no");
                setResult(RESULT_OK, intent);
                finish();
            }
       });
	}

}
