package com.celesterspencer.core;

import java.util.ArrayList;

import com.celesterspencer.util.DateArrayList;
import com.celesterspencer.util.DateHandler;
import com.celesterspencer.util.LexicalArrayList;
import com.celesterspencer.util.Lexicalsortable;
import com.celesterspencer.util.WordsWrapper;

public class WordList {
	
	/*
	 * Wordlist information
	 */
	private String m__listName;
	private LexicalArrayList m__vocabs;
	private LexicalArrayList m__referencedwords;
	private Language m__language;
	private boolean m__isSaved;
	/*
	 * Wordlist settings
	 */
	private boolean m__isTranslationQuestion = false;
	private boolean m__areCharacteresColored = true;
	private long m__numberOfNewVocabularies = 10;
	private long m__numberOfRevisedVocabularies = 20;
	private String m__dateOfLastFinish = "1990-01-01";
	
	
	
	//-------------------------------------------------------------------------------------------------------------------
	// CONSTRUCTOR
	//-------------------------------------------------------------------------------------------------------------------	
	public WordList(String listName, Language language) {
		m__listName = listName;
		long millis = System.currentTimeMillis();
		String listNameShort = listName.replace(" ", "");
		m__language = language;
		m__vocabs = new LexicalArrayList();
		m__referencedwords = new LexicalArrayList();
		m__isSaved = false;
	}
	
	public WordList(String listName, Language language, String listId) {
		m__listName = listName;
		m__language = language;
		m__vocabs = new LexicalArrayList();
		m__referencedwords = new LexicalArrayList();
		m__isSaved = false;
	}
	
	
	
	//-------------------------------------------------------------------------------------------------------------------
	// PUBLIC GETTER AND SETTER
	//-------------------------------------------------------------------------------------------------------------------
	public String getListName() {
		return m__listName;
	}
	
	public long getNumberOfVocabularies() {
		return m__vocabs.size();
	}
	
	public Language getLanguage() {
		return m__language;
	}
	
	public boolean isSaved() {
		return m__isSaved;
	}
	
	public void setIsSaved() {
		m__isSaved = true;
	}

	public void delete() {
		for (Lexicalsortable lexicalSortable : m__vocabs) {
			Word word = (Word) lexicalSortable;
			Core.getDictionary().removeWord(word);
		}
		m__vocabs.clear();
	}	
	
	public boolean isWordListLastFinishedBeforeToday () {
		DateHandler dateHandler = new DateHandler();
		int compare = dateHandler.compareTo(m__dateOfLastFinish);
		if (compare > 0) return true;
		else return false;
	}

	
	
	//-------------------------------------------------------------------------------------------------------------------
	// MANAGING WORDS
	//-------------------------------------------------------------------------------------------------------------------
	public void addWord(Word word) {
		m__vocabs.add(word);
		Core.getDictionary().addWord(word);
		m__isSaved = false;
	}

	public void removeWord(Word word) {
		m__vocabs.remove(word);
		m__referencedwords.remove(word);
		Core.getDictionary().removeWord(word);
		m__isSaved = false;
	}
	
	public void addVocabularys(ArrayList<Word> words) {
		for (Word vocab : words) {
			addWord(vocab);
		}
	}
	
	public void addVocabularys(LexicalArrayList words) {
		for (Lexicalsortable vocab : words) {
			addWord((Word)vocab);
		}
	}
	
	public LexicalArrayList getAllWords() {
		return m__vocabs;
	}
	
	
	
	//-------------------------------------------------------------------------------------------------------------------
	// GETTING WORDS DEPENDING ON TIME
	//-------------------------------------------------------------------------------------------------------------------
	public ArrayList<Word> getNewWords() {
		ArrayList<Word> newWords = new ArrayList<>();
		int index = 0;
		while (index < m__vocabs.size() && newWords.size() <= m__numberOfNewVocabularies) {
			Word word = (Word)m__vocabs.get(index);
			if (word.getNumberOfRepetitions() == 0) {
				newWords.add(word);
			}
			index++;
		}
		return newWords;
	}
	
	public ArrayList<Word> getRevisedWords() {
		ArrayList<Word> revisedWords = new ArrayList<>();
		DateArrayList wordsSortedByDistanceToToday = new DateArrayList();
		int index = 0;
		while (index < m__vocabs.size() && revisedWords.size() <= m__numberOfNewVocabularies) {
			Word word = (Word)m__vocabs.get(index);
			wordsSortedByDistanceToToday.add(word);
			index++;
		}
		for (Word word : wordsSortedByDistanceToToday) {
			revisedWords.add(word);
		}
		return revisedWords;
	}
	
	
	//-------------------------------------------------------------------------------------------------------------------
	// MANAGING REFERENCED WORDS
	//-------------------------------------------------------------------------------------------------------------------
	public void addReferencedWord(Word word) {
		m__referencedwords.add(word);
		Core.getDictionary().addWord(word);
		m__isSaved = false;
	}

	public void addReferencedVocabularys(ArrayList<Word> words) {
		for (Word vocab : words) {
			addReferencedWord(vocab);
		}
	}
	
	public void addReferencedVocabularys(LexicalArrayList words) {
		for (Lexicalsortable vocab : words) {
			addReferencedWord((Word)vocab);
		}
	}

	public LexicalArrayList getAllReferencedWords() {
		return m__referencedwords;
	}
	
	
	//-------------------------------------------------------------------------------------------------------------------
	// GET WORDS- / REFERENCED WORDS- WRAPPER
	//-------------------------------------------------------------------------------------------------------------------
	public WordsWrapper getVocabulariesWrapper() {
		WordsWrapper wrapper = new WordsWrapper();
		wrapper.mainvocabs = m__vocabs;
		wrapper.referenes = m__referencedwords;
		return wrapper;
	}

	


	//-------------------------------------------------------------------------------------------------------------------
	// LIST SETTINGS
	//-------------------------------------------------------------------------------------------------------------------
	public boolean isTranslationQuestion() {
		return m__isTranslationQuestion;
	}

	public void setTranslationQuestion(boolean isTranslationQuestion) {
		this.m__isTranslationQuestion = isTranslationQuestion;
	}

	public boolean areCharacteresColored() {
		return m__areCharacteresColored;
	}

	public void setCharacteresColored(boolean areCharacteresColored) {
		this.m__areCharacteresColored = areCharacteresColored;
	}

	public long getNumberOfNewVocabularies() {
		return m__numberOfNewVocabularies;
	}

	public void setNumberOfNewVocabularies(long numberOfNewVocabularies) {
		this.m__numberOfNewVocabularies = numberOfNewVocabularies;
	}

	public long getNumberOfRevisedVocabularies() {
		return m__numberOfRevisedVocabularies;
	}

	public void setNumberOfRevisedVocabularies(long numberOfRevisedVocabularies) {
		this.m__numberOfRevisedVocabularies = numberOfRevisedVocabularies;
	}
	
	public String getDateOfLastFinish() {
		return m__dateOfLastFinish;
	}

	public void setDateOfLastFinish() {
		DateHandler dateHandler = new DateHandler();
		m__dateOfLastFinish = dateHandler.getStringRepresentation();
	}
	
	public void setListSettings(boolean isTranslationQuestion, boolean areCharactersColored, long numberOfNewVocabularies, long numberOfRevisedVocabularies, String dateOfLastFinish) {
		m__isTranslationQuestion = isTranslationQuestion;
		m__areCharacteresColored = areCharactersColored; 
		m__numberOfNewVocabularies = numberOfNewVocabularies;
		m__numberOfRevisedVocabularies = numberOfRevisedVocabularies;
		m__dateOfLastFinish = dateOfLastFinish;
	}

	
	
}
