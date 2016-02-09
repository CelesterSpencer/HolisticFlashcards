package com.celesterspencer.util;

import java.util.ArrayList;

public class LexicalArrayList extends ArrayList<Lexicalsortable>{

	private ArrayList<Lexicalsortable> m__sortedEntries;

	@Override
	public boolean add(Lexicalsortable object) {
		int index = findIndexOfWord(object);
		super.add(index, object);
		return true;
	}
	
	private int findIndexOfWord(Lexicalsortable sortable) {
		// iterate over words and find lexical position of new word
		int index = -1;
		if (super.size() != 0) {
			for (int i = 0; i < super.size() && index == -1; i++) {
				for (int j = 0; j < sortable.getLength() && j < super.get(i).getLength(); j++) {
					int compare = sortable.getString().compareTo(super.get(i).getString());
					if (compare > 0) {
						break;
					}else if (compare < 0) {
						index = i;
						break;
					}
				}		
			}
			if (index == -1) index = super.size();
		}
		if (index == -1) index = 0;
		return index;
	}
	
}
