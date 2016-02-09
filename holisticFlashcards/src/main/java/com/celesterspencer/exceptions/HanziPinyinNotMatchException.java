package com.celesterspencer.exceptions;

public class HanziPinyinNotMatchException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public HanziPinyinNotMatchException() {
		super("Number of Hanzi and Pinyin does not match!");
	}
}
