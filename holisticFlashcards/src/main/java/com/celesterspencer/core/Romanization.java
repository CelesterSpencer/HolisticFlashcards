package com.celesterspencer.core;

public interface Romanization {

	public String getWrittenRomanization();
	public String getWrittenRomanizationWithTonemark();
	public String getWrittenRomanizationWithoutTones();
	public boolean isSpokenAndWrittenToneDifferent();
	public int getColorOfWrittenPinyin();
	public int getColorOfSpokenPinyin();
	
	public String getSpokenRomanization();
	
}
