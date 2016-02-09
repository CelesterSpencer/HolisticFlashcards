import java.util.ArrayList;

import junit.framework.TestCase;


public class Test_Word extends TestCase {

	private void testChinese() {
		ArrayList<String> characters = new ArrayList<>();
		ArrayList<String> romanizations = new ArrayList<>();
		ArrayList<Integer> numberOfCharacters = new ArrayList<>();
		
		characters.add("落馬洲");
		romanizations.add("luo4ma3zhou");
		numberOfCharacters.add(3);
		
		characters.add("痺證");
		romanizations.add("bi4 zheng4");
		numberOfCharacters.add(2);
		
		characters.add("貢山縣");
		romanizations.add("gong4 shan1xian4");
		numberOfCharacters.add(3);
		
		characters.add("雅魯藏布江");
		romanizations.add("ya3lu3 zang4bu4jiang1");
		numberOfCharacters.add(5);
	}
	
	private void testKorean() {
		ArrayList<String> characters = new ArrayList<>();
		ArrayList<String> romanizations = new ArrayList<>();
		ArrayList<Integer> numberOfCharacters = new ArrayList<>();
		
		characters.add("년");
		romanizations.add("nyeon");
		numberOfCharacters.add(1);
		
		characters.add("오늘");
		romanizations.add("oneul");
		numberOfCharacters.add(2);
		
		characters.add("정각");
		romanizations.add("jeonggak");
		numberOfCharacters.add(2);
		
		characters.add("사용하다");
		romanizations.add("sayonghada");
	}
}
