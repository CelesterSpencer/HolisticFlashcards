package com.celesterspencer.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Random;
import java.util.StringTokenizer;

public class Session {

	private ArrayList<Word> newVocabs = new ArrayList<Word>();
	private ArrayList<Word> oldVocabs = new ArrayList<Word>();
	private ArrayList<Word> wrongVocabs = new ArrayList<Word>();
	private Date startTime = new Date();
	private Word recentVocab = null;
	boolean isFlipped = false;
	private WordList vocablist = null;
	private SpacedRepetitionManager spacedRepetitionManager = new SpacedRepetitionManager();
	private boolean isSetup = false;
	
	public boolean isSetUp() {
		return isSetup;
	}
	
	public void setUp(WordList list) {
		// get vocabularies
		vocablist = list;
		newVocabs = vocablist.getNewWords();
		oldVocabs = vocablist.getRevisedWords();
		// shuffle lists
		Collections.shuffle(newVocabs, new Random(System.nanoTime()));
		Collections.shuffle(oldVocabs, new Random(System.nanoTime()));
		next();
		isSetup = true;
	}
	

	

	
	public void restoreSession(String newVocab, String wrongVocab, String oldVocab, String currentVocab) {
		newVocabs.clear();
		StringTokenizer tokenizer = new StringTokenizer(newVocab, ",");
		while (tokenizer.hasMoreTokens()) {
			String wordId = tokenizer.nextToken();
			int id = Integer.parseInt(wordId);
			Word word = Core.getDictionary().getWordByID(id);
			if (word != null) newVocabs.add(word);			
		}
		
		wrongVocabs.clear();
		tokenizer = new StringTokenizer(wrongVocab, ",");
		while (tokenizer.hasMoreTokens()) {
			String wordId = tokenizer.nextToken();
			int id = Integer.parseInt(wordId);
			Word word = Core.getDictionary().getWordByID(id);
			if (word != null) wrongVocabs.add(word);
		}
		
		oldVocabs.clear();
		tokenizer = new StringTokenizer(oldVocab, ",");
		while (tokenizer.hasMoreTokens()) {
			String wordId = tokenizer.nextToken();
			int id = Integer.parseInt(wordId);
			Word word = Core.getDictionary().getWordByID(id);
			if (word != null) oldVocabs.add(word);
		}
		tokenizer = new StringTokenizer(currentVocab, ",");
		String wordId = tokenizer.nextToken();
		int id = Integer.parseInt(wordId);
		recentVocab = Core.getDictionary().getWordByID(id);
	}
	
	private long getDeltaTime() {
		Date endTime = new Date();
		long deltaTime = endTime.getTime() - startTime.getTime();
		return deltaTime;
	}
	
	public Word getRecentVocab() {
		return recentVocab;
	}
	
	public String getRecentVocabString() {
		String newVocabString = "" + recentVocab.getId();
		return newVocabString;
	}
	
	public String getNewVocabString() {
		String newVocabString = "";
		for (int i = 0; i < newVocabs.size(); i++) {
			Word word = newVocabs.get(i);
			newVocabString += word.getId();
			if (i != newVocabs.size()-1) {
				newVocabString += ",";
			}
		}
		return newVocabString;
	}
	
	public String getWrongVocabString() {
		String wrongVocabString = "";
		for (int i = 0; i < wrongVocabs.size(); i++) {
			Word word = wrongVocabs.get(i);
			wrongVocabString += word.getId();
			if (i != wrongVocabs.size()-1) {
				wrongVocabString += ",";
			}
		}
		return wrongVocabString;
	}
	
	public String getOldVocabString() {
		String oldVocabString = "";
		for (int i = 0; i < oldVocabs.size(); i++) {
			Word word = oldVocabs.get(i);
			oldVocabString += word.getId();
			if (i != oldVocabs.size()-1) {
				oldVocabString += ",";
			}
		}
		return oldVocabString;
	}
	
	public int getNumberOfNewVocab() {
		return newVocabs.size();
	}
	
	public int getNumberOfWrongVocab() {
		return wrongVocabs.size();
	}
	
	public int getNumberOfOldVocab() {
		return oldVocabs.size();
	}
	
	public int getNumberOfRemainingVocab() {
		int numberOfVocabs = 0;
		if (recentVocab != null) numberOfVocabs++;
		numberOfVocabs += newVocabs.size() + wrongVocabs.size() + oldVocabs.size();
		return numberOfVocabs;
	}
	
	public boolean isSessionFinished() {
		return newVocabs.isEmpty() && wrongVocabs.isEmpty() && oldVocabs.isEmpty() && recentVocab==null;
	}
	
	private void next() {
		int newVocabNumber = newVocabs.size();
		int wrongVocabNumber = wrongVocabs.size();
		int oldVocabNumber = oldVocabs.size();
		int totalVocabNumber = newVocabNumber + wrongVocabNumber + oldVocabNumber;
		
		if (totalVocabNumber <= 0) {
			recentVocab =  null;
			return;
		}
		
		Random r = new Random();
		int lowerBound = 0;
		int upperBound = totalVocabNumber;
		int rand = r.nextInt(upperBound - lowerBound) + lowerBound;
		
		Word nextVocab;
		
		if (rand < newVocabNumber) {
			nextVocab = newVocabs.get(0);
			int n = nextVocab.getNumberOfRepetitions();
			nextVocab.setNumberOfRepetitions(++n);
			newVocabs.remove(nextVocab);
		}else if (rand < (newVocabNumber + wrongVocabNumber)) {
			nextVocab = wrongVocabs.get(0);
			wrongVocabs.remove(nextVocab);
		}else {
			nextVocab = oldVocabs.get(0);
			int n = nextVocab.getNumberOfRepetitions();
			nextVocab.setNumberOfRepetitions(++n);
			oldVocabs.remove(nextVocab);
		}
	
		startTime = new Date();
		
		recentVocab = nextVocab;
	}
	
	public void again() {
		long deltaTime = getDeltaTime();
		if (recentVocab != null && recentVocab.getNumberOfRepetitions() == 0) {
			recentVocab.setNumberOfRepetitions(1);
		}else {
			if (recentVocab != null) {
				// vocab was remembered well before but is now forgotten
				if (recentVocab.getEFactor() > 2.7) {
					reviewVocabulary(0);
				}else {
					// wrong answer after a short response
					if (deltaTime < 60000) {
						reviewVocabulary(2);
					}else {
						reviewVocabulary(1);
					}
				}
			}
		}
		wrongVocabs.add(recentVocab);
		next();
	}
	
	public void fine() {
		if (recentVocab != null && recentVocab.getNumberOfRepetitions() == 0) {
			wrongVocabs.add(recentVocab);
			recentVocab.setNumberOfRepetitions(1);
		}else {
			reviewVocabulary(3);
		}
		next();
	}
	
	public void easy() {
		long deltaTime = getDeltaTime();
		if (recentVocab != null && recentVocab.getNumberOfRepetitions() == 0) {
			wrongVocabs.add(recentVocab);
			recentVocab.setNumberOfRepetitions(1);
		}else {
			// if answer came below a minute it is a perfect response
			if (deltaTime < 60000) {
				reviewVocabulary(5);
			}else {
				reviewVocabulary(4);
			}
		}
		next();
	}
	
	private void reviewVocabulary(int repetitionQuality) {
		if (recentVocab != null) {
			spacedRepetitionManager.changeNextReviewDate(recentVocab, repetitionQuality);
		}	
	}
}
