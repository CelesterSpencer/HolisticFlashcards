package com.celesterspencer.core;

import java.util.HashMap;
import java.util.HashSet;

import com.celesterspencer.util.LexicalArrayList;
import com.celesterspencer.util.Lexicalsortable;
import com.celesterspencer.util.Logger;

public class Dictionary {

	private LexicalArrayList m__words;
	private HashMap<Long, Word> m__word_references;
	private HashSet<Long> m__usedIds;

	
	
	//-------------------------------------------------------------------------------------------------------------------
	// CONSTRUCTOR
	//-------------------------------------------------------------------------------------------------------------------	
	public Dictionary() {
		m__words = new LexicalArrayList();
		m__word_references = new HashMap<Long, Word>();
		m__usedIds = new HashSet<>();
	}
	
	
	
	//-------------------------------------------------------------------------------------------------------------------
	// GETTERS AND SETTERS
	//-------------------------------------------------------------------------------------------------------------------		
	public HashSet<Long> getAllUsedIds() {
		return m__usedIds;
	}
	
	public LexicalArrayList getAllWords() {
		return m__words;
	}
	
	/**
	 * Add word with a valid id <br>
	 * word.setId(Dictionary.getNextFreeId()) must be called first
	 * @param word
	 * @return
	 */
	public boolean addWord(Word word) {
		if (word.getId() >= 0) {
//			Logger.log("Words before adding " + m__words.size() + ", " + m__word_references.size(), "Dictionary");
			Logger.log("Word " + word.getAllCharacters() + " id " + word.getId() + " is added to dictionary!" , "Dictionary");
			m__words.add(word);
			m__word_references.put((Long)word.getId(), word);
			m__usedIds.add((Long)word.getId());
//			Logger.log("Words after adding " + m__words.size() + ", " + m__word_references.size(), "Dictionary");
			return true;
		}else {
			Logger.log("Dictionary refused word. It has no id", "Dictionary");
			return false;
		}
	}
	
	public void removeWord(Word word) {
		Logger.log("Words before removing " + m__words.size() + ", " + m__word_references.size(), "Dictionary");
		m__words.remove(word);
		m__word_references.remove(word);
		Logger.log("Words after removing " + m__words.size() + ", " + m__word_references.size(), "Dictionary");
	}
	
	public long getNextFreeId() {
		long freeId = 0;
		
		Logger.log("Currently used ids are ", "Dictionary");
		for (Long id : m__usedIds) {
			Logger.log("id: " + id, "Dictionary");
		}
		
		// find free id
		while (freeId >= 0) {
			if (m__usedIds.contains((Long)(freeId))) {
				freeId++;
			}else {
				break;
			}
		}
		
		if (freeId < 0) Logger.log("Dictionary ran out of free ids", "ALL");
		Logger.log("Next free id is " + freeId, "Dictionary");
		return freeId;
	}

	public Word getWordByID(long id) {
		return m__word_references.get(id);
	}

	public boolean isWordAlreadyPresent(String characters, String romanizations) {
		for (Lexicalsortable lexicalSortable : m__words) {
			Word word = (Word)lexicalSortable;
			if (word.getAllCharacters().contains(characters) && word.getAllRomanizations().contains(romanizations)) {
				return true;
			}
		}
		return false;
	}
	
	public Long getIdByCharactersAndRomanization(String characters, String romanizations) {
		for (Lexicalsortable lexicalSortable : m__words) {
			Word word = (Word)lexicalSortable;
			if (word.getAllCharacters().contains(characters) && word.getAllRomanizations().contains(romanizations)) {
				Logger.log("Dictionary returns id " + word.getId(), "Dictionary");
				return word.getId();
			}
		}
		Logger.log("Word doesn't exist! Dictionary returns error id", "Dictionary");
		Long errorId = -1L;
		return errorId;
	}
	
}
