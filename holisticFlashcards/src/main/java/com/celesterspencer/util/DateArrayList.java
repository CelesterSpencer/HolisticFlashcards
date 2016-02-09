package com.celesterspencer.util;

import java.util.ArrayList;
import java.util.Date;

import com.celesterspencer.core.Word;

/**
 * Sorts Words by distance of their last reviewed date to today
 * @author Archdrian
 *
 */
public class DateArrayList extends ArrayList<Word>{
	
	DateHandler dateHandler;
	
	public DateArrayList() {
		dateHandler = new DateHandler();
	}
	
	@Override
	public boolean add(Word word) {
		if (dateHandler.compareTo(word.getDateOfReview()) >= 0) {
			int index = findIndexOfWord(word);
			super.add(index, word);
			return true;
		}else {
			return false;
		}
	}
	
	private int findIndexOfWord(Word word) {
		// iterate over words and find lexical position of new word
		int index = -1;
		if (super.size() != 0) {
			for (int i = 0; i < super.size() && index == -1; i++) {
				long compare = dateHandler.distanceTo(word.getDateOfReview()) - dateHandler.distanceTo(super.get(i).getDateOfReview());
				if (compare < 0) {
					index = i;
					break;
				}	
			}
			if (index == -1) index = super.size();
		}
		if (index == -1) index = 0;
		return index;
	}
	
}
