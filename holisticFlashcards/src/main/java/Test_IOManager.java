import java.util.ArrayList;
import java.util.Random;

import com.celesterspencer.core.Core;
import com.celesterspencer.core.Language;
import com.celesterspencer.core.WordList;
import com.celesterspencer.core.Word;
import com.celesterspencer.exceptions.HanziPinyinNotMatchException;

import junit.framework.TestCase;


public class Test_IOManager extends TestCase {

	public void testCreateVocabularisWithAllReferences() {
		
		String [] testWords = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k"};
		ArrayList<Long> ids = new ArrayList<>();
		ArrayList<Word> words = new ArrayList<>();
		try {
			for (int i = 0; i < testWords.length; i++) {
				Word word = new Word(testWords[i], testWords[i], 1, Language.CHINESE);
				word.setTranslation(testWords[i]);
				Random randomGenerator = new Random();
				Long id = randomGenerator.nextLong();
				word.setId(id);
				ids.add(id);
				
				// add random selected id as references
				int rangeForNoreference = 2;
				ArrayList<Long> references = new ArrayList<>();
				while(true) {
					int positionWithinArray = randomGenerator.nextInt(ids.size() + rangeForNoreference - 0) + 0;
					if (positionWithinArray < ids.size()) {
						long referencedId = ids.get(positionWithinArray);
						references.add((Long)referencedId);
					}else {
						break;
					}
				}
				word.setReferences(references);
				words.add(word);
			}
			
		} catch (HanziPinyinNotMatchException e) {
			e.printStackTrace();
		}
		
		WordList list = new WordList("testList", Language.CHINESE);
		list.addVocabularys(words);
		
		// add words to dictionary
		for (Word word : words) {
			Core.getDictionary().addWord(word);
		}
		
		Core.getIoManager().prepareListForUpload(list);
		
	}
}
