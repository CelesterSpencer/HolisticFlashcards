package com.celesterspencer.core;

import java.util.ArrayList;

public class VocabularyBox {

	private ArrayList<WordList> m__vocabularyLists = new ArrayList<>();
	private WordList m__activeVocabularyList = null;
	
	
	
	//-------------------------------------------------------------------------------------------------------------------
	// GETTERS AND SETTERS
	//-------------------------------------------------------------------------------------------------------------------	
	public WordList getActiveVocabularyList() {
		if (m__activeVocabularyList == null) {
			if (!m__vocabularyLists.isEmpty()) {
				m__activeVocabularyList = m__vocabularyLists.get(0);
			}else {
				m__activeVocabularyList = new WordList("Dummy List", Language.CHINESE);
			}
		}
		return m__activeVocabularyList;
	}
	
	public void setActiveVocabularyList(WordList vocabularyList) {
		m__activeVocabularyList = vocabularyList;
	}
	
	public void addVocabularyList(WordList list) {
		m__vocabularyLists.add(list);
	}
	
	public void removeVocabularyList(WordList list) {
		m__vocabularyLists.remove(list);
		list.delete();
	}
	
	public ArrayList<WordList> getAllLists() {
		return m__vocabularyLists;
	}
}
