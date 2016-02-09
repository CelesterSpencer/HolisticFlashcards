package com.celesterspencer.core.romanizations;

import android.graphics.Color;

import com.celesterspencer.core.Romanization;

public class KoreanRomanization implements Romanization{

	String romanization;
	String romanization_spoken;
	int m__color;
	
	public KoreanRomanization(String original_string) {
		// set string to uppercase
		if (original_string.length() > 1) {
			romanization = original_string.substring(0,1).toUpperCase() + original_string.substring(1, original_string.length());
		}else if (original_string.length() == 1){
			romanization = original_string.substring(0,1).toUpperCase();
		}else {
			romanization = original_string;
		}
		// set spoken string
		romanization_spoken = original_string;
		// set color to gray
		m__color = Color.rgb(0, 0, 0);
	}

	@Override
	public String getWrittenRomanization() {
		return romanization;
	}

	@Override
	public String getWrittenRomanizationWithTonemark() {
		return romanization;
	}

	@Override
	public String getWrittenRomanizationWithoutTones() {
		return romanization;
	}

	@Override
	public boolean isSpokenAndWrittenToneDifferent() {
		return false;
	}

	@Override
	public int getColorOfWrittenPinyin() {
		return m__color;
	}

	@Override
	public int getColorOfSpokenPinyin() {
		return m__color;
	}

	@Override
	public String getSpokenRomanization() {
		return romanization_spoken;
	}

}
