package com.celesterspencer.core.romanizations;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.celesterspencer.core.Romanization;

import android.graphics.Color;

public class Pinyin implements Romanization{
	
	private String m__original_string;
	private String m__pinyinWithoutTone;
	private String m__pinyinWitTonemark;
	private int m__colorWritten;
	private int m__colorSpoken;
	private int m__toneNumber;
	private int m__toneNumberSound;
	
	public Pinyin(String original_string) {
		m__original_string = original_string;
		m__colorWritten = Color.GRAY;
		m__colorSpoken = Color.GRAY;
		extractInformation();
		setPinyinWithTonemark();
		setColor();
	}
	
	
	
	//-------------------------------------------------------------------------------------------------------------------
	// PUBLIC GETTER AND SETTER
	//-------------------------------------------------------------------------------------------------------------------
	public String getWrittenRomanization() {
		return m__pinyinWithoutTone + m__toneNumber;
	}
	
	public String getSpokenRomanization() {
		return m__pinyinWithoutTone + m__toneNumberSound;
	}
	
	public String getWrittenRomanizationWithTonemark() {
		return m__pinyinWitTonemark;
	}
	
	public String getWrittenRomanizationWithoutTones() {
		return m__pinyinWithoutTone;
	}
	
	public boolean isSpokenAndWrittenToneDifferent() {
		return (m__toneNumber != m__toneNumberSound);
	}
	
	public int getColorOfWrittenPinyin() {
		return m__colorWritten;
	}
	
	public int getColorOfSpokenPinyin() {
		return m__colorSpoken;
	}
	
	
	
	//-------------------------------------------------------------------------------------------------------------------
	// PRIVATE METHODS
	//-------------------------------------------------------------------------------------------------------------------	
	// chop string into its pronunciation and tone
	private void extractInformation() {
		String regex= "[1-5](\\*[1-5])?";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(m__original_string);
		if (m.find()) {
			String match = m.group();
			if (match.length() == 3) {
				m__toneNumber = Integer.parseInt(match.substring(0,1));
				m__toneNumberSound = Integer.parseInt(match.substring(2,3));
			}else if (match.length() == 1) {
				m__toneNumber = Integer.parseInt(match.substring(0,1));
				m__toneNumberSound = Integer.parseInt(match.substring(0,1));
			}else {
				m__toneNumber = 5;
				m__toneNumberSound = 5;
			}
		// estimate 5th tone in case no number is set	
		}else {
			m__toneNumber = 5;
			m__toneNumberSound = 5;
		}
		
		//set pinyin without tone
		String regexWord= "[A-Z]?[a-z]*";
		Pattern pWord = Pattern.compile(regexWord);
		Matcher mWord = pWord.matcher(m__original_string);
		m__pinyinWithoutTone="";
		if (mWord.find()) {			
			String raw = mWord.group();
			raw = raw.replace("ü", "v");
			if (raw.length() > 1) {
				m__pinyinWithoutTone = raw.substring(0,1).toUpperCase() + raw.substring(1);
			}else {
				m__pinyinWithoutTone = raw.substring(0,1).toUpperCase();
			}
			
		}
	}
	
	private void setColor() {
		switch (m__toneNumber) {
		case 1:
			m__colorWritten = Color.RED;
			break;
		case 2:
			m__colorWritten = Color.GREEN;
			break;
		case 3:
			m__colorWritten = Color.BLUE;
			break;
		case 4:
			m__colorWritten = Color.rgb(255, 191, 0);
			break;
		}	
		
		switch (m__toneNumberSound) {
		case 1:
			m__colorSpoken = Color.RED;
			break;
		case 2:
			m__colorSpoken = Color.GREEN;
			break;
		case 3:
			m__colorSpoken = Color.BLUE;
			break;
		case 4:
			m__colorSpoken = Color.rgb(200, 200, 0);
			break;
		}	
	}
	
	private void setPinyinWithTonemark() {
		
		//make all lowercase
		String temp_pinyin = m__pinyinWithoutTone;
		if (temp_pinyin.length() > 1) {
			temp_pinyin = temp_pinyin.substring(0,1).toLowerCase() + temp_pinyin.substring(1);
		}else {
			temp_pinyin = temp_pinyin.substring(0,1).toLowerCase();
		}
		
		// find the right vocal with the rule a < e < o
		// in case of iu, ui .. the second vocale is selected
		int indexToChange = 0;
		int smallestVocalValue = 5; 
		for (int ind = 0; ind < temp_pinyin.length(); ind++) {
			int currentVocaleValue = getVocalValue(temp_pinyin.substring(ind, ind+1));
			if (currentVocaleValue <= smallestVocalValue) {
				smallestVocalValue = currentVocaleValue;
				indexToChange = ind;
			}
		}
		
		if (indexToChange + 1 < m__pinyinWithoutTone.length()) {
			m__pinyinWitTonemark = m__pinyinWithoutTone.substring(0, indexToChange) + 
					getToneMark(m__pinyinWithoutTone.substring(indexToChange, indexToChange + 1),m__toneNumber) +
					m__pinyinWithoutTone.substring(indexToChange + 1);
		}else {
			m__pinyinWitTonemark = m__pinyinWithoutTone.substring(0, indexToChange) + 
					getToneMark(m__pinyinWithoutTone.substring(indexToChange, indexToChange + 1),m__toneNumber);
		}
	}

	private int getVocalValue(String vocale) {
		switch (vocale) {
		case "a":
			return 1;
		case "e":
			return 2;
		case "o":
			return 3;
		case "i":
			return 4;			
		case "u":
			return 4;
		case "v":
			return 4;
		case "ü":
			return 4;
		default:
			return 5;
		}
	}
	
	private String getToneMark(String vocale, int tone) {
		switch (tone) {
		case 1:
			switch (vocale) {
			case "A":	
				return "Ā";
			case "E":	
				return "Ē";
			case "O":	
				return "Ō";
			case "a":	
				return "ā";				
			case "e":	
				return "ē";
			case "i":	
				return "ī";
			case "o":	
				return "ō";
			case "u":	
				return "ū";
			case "v":
				return "ǖ";
			case "ü":
				return "ǖ";
			}	
			break;
			
		case 2:
			switch (vocale) {
			case "A":	
				return "Á";
			case "E":	
				return "É";
			case "O":	
				return "Ó";
			case "a":	
				return "á";				
			case "e":	
				return "é";
			case "i":	
				return "í";
			case "o":	
				return "ó";
			case "u":	
				return "ú";
			case "v":
				return "ǘ";
			case "ü":
				return "ǘ";
			}				
			break;
			
		case 3:
			switch (vocale) {
			case "A":	
				return "Ǎ";
			case "E":	
				return "Ĕ";
			case "O":	
				return "Ŏ";
			case "a":	
				return "ǎ";				
			case "e":	
				return "ě";
			case "i":	
				return "ǐ";
			case "o":	
				return "ǒ";
			case "u":	
				return "ǔ";
			case "v":
				return "ǚ";
			case "ü":
				return "ǚ";
			}		
			break;
			
		case 4:
			switch (vocale) {
			case "A":	
				return "À";
			case "E":	
				return "È";
			case "O":	
				return "Ò";
			case "a":	
				return "à";				
			case "e":	
				return "è";
			case "i":	
				return "ì";
			case "o":	
				return "ò";
			case "u":	
				return "ù";
			case "v":
				return "ǜ";
			case "ü":
				return "ǜ";
			}				
			break;
		}
		return vocale;
	}
}
