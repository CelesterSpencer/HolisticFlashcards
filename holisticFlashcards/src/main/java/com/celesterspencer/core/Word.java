package com.celesterspencer.core;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.celesterspencer.core.romanizations.KoreanRomanization;
import com.celesterspencer.core.romanizations.Pinyin;
import com.celesterspencer.exceptions.HanziPinyinNotMatchException;
import com.celesterspencer.util.Lexicalsortable;
import com.celesterspencer.util.Logger;

public class Word implements Lexicalsortable{

	/*
	 * Vocabulary information
	 */
	private ArrayList<String> m__characters;
	private ArrayList<Romanization> m__romanizations;
	private Language m__language;
	private ArrayList<String> m__translations;
	private String m__mnemonic;
	private ArrayList<Long> m__references;
	private long m__id;
	/*
	 * Supermemo information
	 */
	private int m__numberOfRepetitions = 0;
	private double m__eFactor = 2.5;
	private int m__interRepetitionInterval = 0;
	private String m__dateOfReview = "1990-01-01";
	
	
	
	
	//-------------------------------------------------------------------------------------------------------------------
	// CONSTRUCTOR
	//-------------------------------------------------------------------------------------------------------------------
	public Word(String characters, String romanizations, Language language) throws HanziPinyinNotMatchException {
		m__characters = new ArrayList<>();
		m__romanizations = new ArrayList<>();
		m__translations = new  ArrayList<>();
		m__references = new ArrayList<>();
		m__mnemonic = "";
		m__language = language;
		m__id = -1;
		
		if (!chopWordIntoCharacters(characters, romanizations)) {
			throw new HanziPinyinNotMatchException();
		}
	}

	public Word(String characters, String romanizations, long id, Language language) throws HanziPinyinNotMatchException {
		m__characters = new ArrayList<>();
		m__romanizations = new ArrayList<>();
		m__translations = new  ArrayList<>();
		m__references = new ArrayList<>();
		m__mnemonic = "";
		m__language = language;
		m__id = id;
		
		if (!chopWordIntoCharacters(characters, romanizations)) {
			throw new HanziPinyinNotMatchException();
		}
	}
	
	
	//-------------------------------------------------------------------------------------------------------------------
	// PUBLIC GETTER AND SETTER
	//-------------------------------------------------------------------------------------------------------------------	
	public int length() {
		return m__characters.size();
	}
	
	public long getId() {
		return m__id;
	}
	
	public void setId(long id) {
		m__id = id;
	}

	public String getCharacterAt(int ind) {
		return m__characters.get(ind);
	}
	
	public String getAllCharacters() {
		String allCharacters = "";
		for (String characters : m__characters) {
			allCharacters += characters;
		}
		return allCharacters;
	}
	
	public String getAllRomanizations() {
		String allRomanizations = "";
		for (Romanization romanization : m__romanizations) {
			allRomanizations += romanization.getWrittenRomanization() + " ";
		}
		return allRomanizations;
	}
	
	public Romanization getRomanizationAt(int ind) {
		return m__romanizations.get(ind);
	}
	
	public Word setMeaning(String characters, String romanizations) throws HanziPinyinNotMatchException {
		if (!chopWordIntoCharacters(characters, romanizations)) {
			throw new HanziPinyinNotMatchException();
		}
		return this;
	}
	
	public ArrayList<String> getTranslations() {
		return m__translations;
	}
	
	public String getTranslationsString() {
		String translationsString = "";
		for (int i = 0; i < m__translations.size(); i++) {
			if (i == m__translations.size() - 1) {
				translationsString += m__translations.get(i);
			}else {
				translationsString += m__translations.get(i) + ", ";
			}
		}
		return translationsString;
	}
	
	public Word setTranslation(String translation_string) {
		m__translations.add(translation_string);
		return this;
	}
		
	public String getMnemonic() {
		return m__mnemonic;
	}
	
	public Word setMnemonic(String translation_string) {
		m__mnemonic = translation_string;
		return this;
	}
	
	public ArrayList<Long> getReferences() {
		return m__references;
	}
	
	public String getReferencesString() {
		String referencesString  = "";
		for (Long ref : m__references) {
			referencesString  += (ref + ",");
		}
		return referencesString;
	}

	public void setReferences(ArrayList<Long> references) {
		m__references = (ArrayList<Long>) references.clone();
	}
	
	public Word setReferences(String references) {
		StringTokenizer tokenizer = new StringTokenizer(references, ",");
		while (tokenizer.hasMoreTokens()) {
			long id = Long.parseLong(tokenizer.nextToken().trim());
			m__references.add(id);
		}
		return this;
	}
	
	
	
	//-------------------------------------------------------------------------------------------------------------------
	// WORD SETTINGS
	//-------------------------------------------------------------------------------------------------------------------	
	public int getNumberOfRepetitions() {
		return m__numberOfRepetitions;
	}

	public Word setNumberOfRepetitions(int m__numberOfRepetitions) {
		this.m__numberOfRepetitions = m__numberOfRepetitions;
		return this;
	}

	public double getEFactor() {
		return m__eFactor;
	}

	public Word setEFactor(double m__eFactor) {
		this.m__eFactor = m__eFactor;
		return this;
	}

	public int getInterRepetitionInterval() {
		return m__interRepetitionInterval;
	}

	public Word setInterRepetitionInterval(int m__interRepetitionInterval) {
		this.m__interRepetitionInterval = m__interRepetitionInterval;
		return this;
	}
	
	public Word setDateOfReview(String date) {
		m__dateOfReview = date;
		return this;
	}
	
	public String getDateOfReview() {
		return m__dateOfReview;
	}
	
	
	//-------------------------------------------------------------------------------------------------------------------
	// PRIVATE METHODS
	//-------------------------------------------------------------------------------------------------------------------	
	/**
	 * Chop romanization into pieces and fill character and romanization arraylists
	 * @param charactersString
	 * @param romanizationsString
	 * @return true if chopping and assigning romanization to each character was successful, else return false
	 */
	private boolean chopWordIntoCharacters(String charactersString, String romanizationsString) {
		
		String tempRomanizationString = romanizationsString.toLowerCase();
		
		// clear lists
		m__characters.clear();
		m__romanizations.clear();
		
		// create regex for matching the right romanization
		ArrayList<String> matches = new ArrayList<String>();
		String regex = "";
		
		switch (m__language) {
		case CHINESE:
			Logger.log("Choosed chinese", "WORD");
			regex = "[a-z|A-Z][a-z]*\\s*([1-5](\\*[1-5])?)?";
			break;
		case KOREAN:
			Logger.log("Choosed korean", "WORD");
			String singleConsonant = "g|k|n|d|t|r|l|m|b|p|s|j|h";
			String doubleConsonant = "ng|ch|kk|tt|pp|ss|jj";
			String C = "(" + doubleConsonant + "|" + singleConsonant + ")";
			String singleVocal = "a|e|i|o|u";
			String doubleVocal = "ae|ya|eo|ye|wa|oe|yo|wo|we|wi|yu|eu|ui";
			String tripleVocal = "yae|yeo|wae";
			String V = "(" + tripleVocal + "|" + doubleVocal + "|" + singleVocal + ")";
			String CVCC =  C + V + C + C;
			String CVC = C + V + C;
			String VCC = V + C + C;
			String CV = C + V;
			String VC = V + C;
			regex = "(" + CVCC + "|" + CVC + "|" + VCC + "|" + CV + "|" + VC + "|" + V + ")";
			break;	
		default:
			Logger.log("Choosed no language", "ALL");
			break;
		}
		
		// match romanization string with regex pattern
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(tempRomanizationString);
		while(m.find()) {
			Logger.log("Match is " + m.group(), "WORD");
			matches.add(m.group());
		}
		
		/**
		 *  check if number of romanizations and characters are equal
		 *  if yes add them to the list
		 */
		int romanizations_counter = matches.size();
		int character_counter = charactersString.length();
		if (romanizations_counter == character_counter) {
			switch (m__language) {
			case CHINESE:
				for (int i = 0; i < romanizations_counter; i++) {
					m__romanizations.add(new Pinyin(matches.get(i)));
					m__characters.add(charactersString.substring(i, i+1));
				}
				break;
			case KOREAN:
				for (int i = 0; i < romanizations_counter; i++) {
					m__romanizations.add(new KoreanRomanization(matches.get(i)));
					m__characters.add(charactersString.substring(i, i+1));
				}
				break;	
			default:
				break;
			}
			return true;
		}else {
			Logger.log("Character number and romanization number does not match:", "ALL");
			Logger.log("Character number: " + character_counter +", romanization number: " + romanizations_counter, "ALL");
			return false;
		}
		
	}



	//-------------------------------------------------------------------------------------------------------------------
	// INHERITED METHODS
	//-------------------------------------------------------------------------------------------------------------------		
	@Override
	public int getLength() {
		return m__characters.size();
	}

	@Override
	public String getString() {
		String allRomanizations  = "";
		for (Romanization romanization : m__romanizations) {
			allRomanizations += romanization.getWrittenRomanization();
		}
		return allRomanizations;
	}

	
	
}
