package com.celesterspencer.core;

import com.celesterspencer.util.DateHandler;
import com.celesterspencer.util.Logger;

public class SpacedRepetitionManager {
	
	public void changeNextReviewDate(Word word, int repetitionQuality) {
		// get values from unit
		int oldRepetitionInterval = word.getInterRepetitionInterval();
		double oldEFactor = word.getEFactor();
		int n = word.getNumberOfRepetitions();
		double newEFactor = oldEFactor;
		
		// calculate inter repetition interval
		int repetitionInterval = 0;
		switch (n) {
		case 1:
			repetitionInterval = 1;
			break;
		case 2:
			repetitionInterval = 6;
			break;
		default:
			repetitionInterval = (int)Math.ceil(oldRepetitionInterval * oldEFactor);
			break;
		} 
		
		// update n and EFactor
		if (repetitionQuality < 3) {
			n = 1;
		}else {
			newEFactor = getNewEFactor(oldEFactor, repetitionQuality);
		}
		
		// set values
		word.setNumberOfRepetitions(n);
		word.setEFactor(newEFactor);
		word.setInterRepetitionInterval(repetitionInterval);
		DateHandler dateHandler = new DateHandler();
		dateHandler.incrementBy(repetitionInterval);
		word.setDateOfReview(dateHandler.getStringRepresentation());
		
		// feedback
		Logger.log("Old E-Factor is " + oldEFactor + " and new E-Fector is " + newEFactor + ". Next review in " + repetitionInterval + " days", "SPACEDREPETITION");
	}
	
	private double getNewEFactor(double oldEFactor, int repetitionQuality) {
		int q = 5 - repetitionQuality;
		double newEFactor = oldEFactor + (0.1 - q * (0.08 + q * 0.02));
		if (newEFactor < 1.3) newEFactor = 1.3;
		return newEFactor;
	}
}
