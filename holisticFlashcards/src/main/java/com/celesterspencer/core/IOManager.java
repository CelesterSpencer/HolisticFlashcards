package com.celesterspencer.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;

import com.celesterspencer.exceptions.HanziPinyinNotMatchException;
import com.celesterspencer.util.IDWrapper;
import com.celesterspencer.util.LexicalArrayList;
import com.celesterspencer.util.Lexicalsortable;
import com.celesterspencer.util.Logger;
import com.celesterspencer.util.WordsWrapper;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DropboxFileInfo;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.exception.DropboxException;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.widget.TextView;
import android.widget.Toast;

public class IOManager {

	Context m__applicationContext;
	boolean m__loadedVocabularyLists;
	boolean m__loadedSettings;
	
	//-------------------------------------------------------------------------------------------------------------------
	// PUBLIC METHODS
	//-------------------------------------------------------------------------------------------------------------------
	
	public IOManager() {
		m__loadedVocabularyLists = false;
		m__loadedSettings = false;
	}
	
	public boolean isContextSet() {
		return m__applicationContext != null;
	}
	
	public void displayMessage(String msg) {
		if (Core.getApplicationContext().isContextSet()) {
			
			Toast.makeText(Core.getApplicationContext().getContext(), msg, Toast.LENGTH_SHORT).show();
		}else {
			Logger.log("Display cannot be shown, Context is not set",  "ALL");
		}	
	}

	public void displayError(String msg) {
		if (Core.getApplicationContext().isContextSet()) {
			Toast toast = Toast.makeText(Core.getApplicationContext().getContext(), msg, Toast.LENGTH_SHORT);
			TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
			v.setTextColor(Color.RED);
			toast.show();
		}else {
			Logger.log("Display cannot be shown, Context is not set",  "ALL");
		}
	}
	
	// Checks if external storage is available for read and write
	public boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}
	
	
	
	//-------------------------------------------------------------------------------------------------------------------
	// VOCABULARYLIST
	//-------------------------------------------------------------------------------------------------------------------
	public void saveVocabularyLists() {
		ArrayList<WordList> allVocabularyLists = Core.getVocabularyBox().getAllLists(); 
		for (WordList list : allVocabularyLists) {
			if (!list.isSaved()) {
				saveList(list);
			}
		}
	}
	
	public void loadVocabularyLists() {
		if (!m__loadedVocabularyLists) {
			displayMessage("Loading...");
			File path = new File(Environment.getExternalStorageDirectory() + "/hf_data");
			if (path.exists()) {
				File f = new File(path.getPath());        
				File file[] = f.listFiles();
				Logger.log("" + file.length + " Files will be loaded from external storage", "IOMANAGER");
				displayMessage("" + file.length + " Files will be loaded from external storage");
				for (int i=0; i < file.length; i++) {
					boolean loadingSuccessful = loadFile(file[i]);
					if (loadingSuccessful) readFile(file[i]);
				}
			} else {
				displayError("" + path.getAbsolutePath() + " cannot be accessed!");
			}
			m__loadedVocabularyLists = true;
		}
	}
	
	public void uploadVocabularyListsToDropBox(DropboxAPI<?> dropbox, String path) {
		for (WordList list : Core.getVocabularyBox().getAllLists()) {
			// create tempfile
			String listname = list.getListName().replaceAll(" ", "") + ".hfpk";
			String pathname = Core.getApplicationContext().getContext().getFilesDir() + listname;
    		File tempFile = new File(pathname);
    		
			try {
				
				// reorganize list
				WordsWrapper reorganizedList = prepareListForUpload(list);
				
				// fill file with data
	    		fillFileWithVocabularyListInfos(tempFile, reorganizedList, list);
	    		FileInputStream fileInputStream;
				fileInputStream = new FileInputStream(tempFile);
				Logger.log("Save " + path + listname + " to dropbox", "IOMANAGER");
	    		
	    		// write file to DB
	    		Entry response = dropbox.putFileOverwrite(path + listname, fileInputStream, tempFile.length(), null);
	    		Logger.log("Entry path is " + response.path, "IOMANAGER");
	            tempFile.delete();
			} catch (FileNotFoundException e) {
				displayError("File Not Found while trying to upload");
				e.printStackTrace();
			} catch (DropboxException e) {
				displayError("DB Exception while trying to upload");
				e.printStackTrace();
			}
    		
    	}
	}
	
	public void downloadVocabularyListsFromDropBox(DropboxAPI<?> dropbox, String path) {
		
		// get filelist in DB folder
		Entry dirent;
		try {
			dirent = dropbox.metadata(path, 1000, null, true, null);
			ArrayList<WordList> vocabularylists = new ArrayList<WordList>(); 
			
			// import all entries
	        for (Entry ent: dirent.contents) {
	        	
	        	// get file
	            String pathname = Core.getApplicationContext().getContext().getFilesDir() + ent.path;
	            pathname.replace("hfpk", "txt");
	            String filename = ent.path;
	            Logger.log("Create file " + pathname, "IOMANAGER");
	            
	            // check if local file exists
	            //if not create local file
	            File file = new File(pathname);
	            if(!file.exists()) {
	                file.createNewFile();
	            } 
	            
	            // write file info from DB folder to local file
	            FileOutputStream outputStream = new FileOutputStream(file);
	            System.out.println("Try to read file " + filename);
	            DropboxFileInfo info = dropbox.getFile(filename, null, outputStream, null);
	            
	            // load vocabularylist
	            loadFile(file);
	        }
		} catch (DropboxException e) {
			Logger.log("DB Exception while trying to download", "IOMANAGER");
			e.printStackTrace();
		} catch (IOException e) {
			Logger.log("IOException while trying to upload", "IOMANAGER");
			e.printStackTrace();
		}
		
        
	}

	private void saveList(WordList list) {
		// create filename
		String listname = list.getListName().replaceAll(" ", "") + ".hfpk";
		displayMessage("Save list " + listname);
		
		// check if storage is writeable
		if (isExternalStorageWritable()) {
			
			// create file within externalDirectory/holistic_flashcards/listname.hfpk
			File data_directory = new File(Environment.getExternalStorageDirectory() + "/hf_data");
			
			// check if directory exists and create it if necessary
			if (!data_directory.exists()) {
				Logger.log("" + data_directory.getAbsolutePath() + " does not exist!", "ALL");
				if (data_directory.mkdir()) Logger.log("Created directory " + data_directory.getAbsolutePath(), "IOMANAGER");
			}
			File file = new File(data_directory, listname);			
			
			WordsWrapper wrapper = list.getVocabulariesWrapper();
			
			fillFileWithVocabularyListInfos(file, wrapper, list);
			list.setIsSaved();
		    
		    Core.getIoManager().displayMessage("Saved list " + list.getListName());

		} else {
			displayError("External storage is not writeable!");
		}
	}
	
	public boolean loadFile(File file) {
		try {
			// setup stream
			FileInputStream fIn = new FileInputStream(file);
			BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
			
			// load list informations
			String listname = myReader.readLine();
			listname = listname.trim();
			Logger.log("Loaded files name is " + listname, "IOMANAGER");
			
			// check if listname already exists and rename it
			int numberOfCopies = 0;
			boolean hasBeenRenamed = false;
			String tempname = listname;
			for (int ind = 0; ind < Core.getVocabularyBox().getAllLists().size(); ind++) {
				if (tempname.equals(Core.getVocabularyBox().getAllLists().get(ind).getListName().trim())) {
					numberOfCopies++;
					tempname = listname + " " + numberOfCopies;
					ind = 0;
					hasBeenRenamed = true;
				}
			}
			if (hasBeenRenamed) {
				listname = tempname;
				Logger.log("VocabularyList " + listname + " already exists", "IOMANAGER");
			}
			
			// set language
			String languageString = myReader.readLine();
			Language language = Language.CHINESE;
			if (languageString.contains("Chinese")) {
				language = Language.CHINESE;
			} else if (languageString.contains("Korean")) {
				language = Language.KOREAN;
			}
			Logger.log("Loaded language is " + languageString, "IOMANAGER");
			
			// get list settings
			String isTranslationQuestionString = myReader.readLine().trim();
			boolean isTranslationQuestion = (isTranslationQuestionString.contains("true")) ? true : false;
			String areCharacteresColoredString = myReader.readLine().trim();
			boolean areCharactersColored = (areCharacteresColoredString.contains("true")) ? true : false;
			String numberOfNewVocabulariesString = myReader.readLine().trim();
			long numberOfNewVocabularies = Long.parseLong(numberOfNewVocabulariesString);
			String numberOfRevisedVocabulariesString = myReader.readLine().trim();
			long numberOfRevisedVocabularies = Long.parseLong(numberOfRevisedVocabulariesString);
			String dateOfLastFinish = myReader.readLine().trim();
			
			// get number of vocabularies
			String numberOfWordsString = myReader.readLine().trim();
			int numberOfWords = Integer.parseInt(numberOfWordsString);
			Logger.log("Loaded list has " + numberOfWords + " words", "IOMANAGER");
			String numberOfReferencedWordString = myReader.readLine().trim();
			int numberOfReferencedWords = Integer.parseInt(numberOfReferencedWordString);
			Logger.log("Loaded list has " + numberOfReferencedWords + " references", "IOMANAGER");
			
			// load vocabularies
			Logger.log("List loads vocabularies", "IOMANAGER");
			LexicalArrayList vocabularies = new LexicalArrayList();
			fillListFromFile(myReader, numberOfWords, language, vocabularies);
			
			String referencesLabelString = myReader.readLine();
			Logger.log("Reference label is " + referencesLabelString, "IOMANAGER");
			
			// load references
			Logger.log("List loads referenced vocabularies", "IOMANAGER");
			LexicalArrayList referencedWords = new LexicalArrayList();
			fillListFromFile(myReader, numberOfReferencedWords, language, referencedWords);
			
			// if size of vocabularies is not zero add them to the dictionary
			if (vocabularies.size() > 0) {
				Logger.log("Loaded list is not empty", "IOMANAGER");
				
				myReader.close();
				
				// create vocablist 
				WordList vocabList = new WordList(listname, language);
				vocabList.setListSettings(isTranslationQuestion, areCharactersColored, numberOfNewVocabularies, numberOfRevisedVocabularies, dateOfLastFinish);
				renameListAndAddToVocabularyList(vocabList, vocabularies, referencedWords);
				
				// add vocablist to the vocabularybox
				Core.getVocabularyBox().addVocabularyList(vocabList);
				
				Logger.log("Loaded vocabularyList " + listname, "IOMANAGER");
				return true;
				
			}else {
//				displayMessage("Error while loading vocabularylist " + listname + ". List is either empty or all vocabularies exist already!");
				displayError("Error while loading vocabularylist " + listname + ". List is either empty or all vocabularies exist already!");
			}
			
			myReader.close();
			return false;
		} catch (Exception e) {
//			displayMessage("Exception while loading vocabularyList! What: " + e.getMessage());
			displayError("Exception while loading vocabularyList! What: " + e.getMessage());
			return false;
		}
	}

	public void deleteFile(WordList list) {
		
		// create filename
		String listname = list.getListName().replaceAll(" ", "") + ".hfpk";
		
		// check if storage is writeable
		if (isExternalStorageWritable()) {
			
			// create file within externalDirectory/holistic_flashcards/listname.hfpk
			File file = new File(Environment.getExternalStorageDirectory() + "/hf_data", listname);
			
			// check if file exists and then deletes it
			if (file.exists()) {
				file.delete();
				displayMessage("Deleted vocabularyList " + listname + "!");
			}
		}	
			
	}
	
	public WordsWrapper prepareListForUpload(WordList list) {
		
		// FILL IDS and REFERENCES SET
		WordsWrapper wrapper = new WordsWrapper();
		wrapper.mainvocabs = (LexicalArrayList)list.getAllWords().clone();
		wrapper.referenes = new LexicalArrayList();
		
		// create helper sets
		IDWrapper idwrapper = new IDWrapper();
		
		// fill sets with data
		fillIDWrapper(wrapper, idwrapper);
		
		// START RENAMING IDS
		HashMap<Long, Long> renamings = new HashMap<>();
		fillRenamings(wrapper, renamings);
		
		// apply renamings
		applyRenamings(wrapper, renamings);
		
		// put  both lists together
		ArrayList<Word> resultList = new ArrayList<>();
		for (int i = 0; i < wrapper.mainvocabs.size(); i++) {
			resultList.add((Word)wrapper.mainvocabs.get(i));
		}
		for (int i = 0; i < wrapper.referenes.size(); i++) {
			resultList.add((Word)wrapper.referenes.get(i));
		}
		return wrapper;
		
	}

	
	
	
	//-------------------------------------------------------------------------------------------------------------------
	// PRIVATE METHODS
	//-------------------------------------------------------------------------------------------------------------------
	private void fillListFromFile(BufferedReader myReader, int numberOfWords, Language language, LexicalArrayList vocabularies) throws IOException, HanziPinyinNotMatchException {
		String dataRow = "";
		Logger.log("Fill list from file", "IOMANAGER");
		for (int i = 0; i < numberOfWords; i++) {
			dataRow = myReader.readLine();
			
			// get word date
			StringTokenizer tokenizer = new StringTokenizer(dataRow, "|");
			String idStr = tokenizer.nextToken().trim();
			long id = Long.parseLong(idStr);
			String characters = tokenizer.nextToken().trim();
			String romanization = tokenizer.nextToken().trim();
			String translations = tokenizer.nextToken().trim();
			String references = tokenizer.nextToken().trim();
			String mnemonics = tokenizer.nextToken().trim();
			
			// get word settings
			String numberOfRepetitionsString = tokenizer.nextToken().trim();
			int numberOfRepetitions = Integer.parseInt(numberOfRepetitionsString);
			String eFactorString = tokenizer.nextToken().trim();
			double eFactor = Double.parseDouble(eFactorString);
			String interRepetitionIntervalString = tokenizer.nextToken().trim();
			int interRepetitionInterval = Integer.parseInt(interRepetitionIntervalString);
			String dateOfReview = tokenizer.nextToken().trim();
			
			// create word and add to vocabularies list
			if (!Core.getDictionary().isWordAlreadyPresent(characters, romanization)) {
				Word vocab = new Word(characters, romanization, id, language).setTranslation(translations).setReferences(references)
						.setMnemonic(mnemonics).setNumberOfRepetitions(numberOfRepetitions).setEFactor(eFactor)
						.setInterRepetitionInterval(interRepetitionInterval).setDateOfReview(dateOfReview);
				vocabularies.add(vocab);
			}else {
				System.out.println("Vocab already exists");
			}
		}
	}
	
	private void fillIDWrapper(WordsWrapper vocabwrapper, IDWrapper idwrapper) {
		// fill word ids
		for (int i = 0; i < vocabwrapper.mainvocabs.size(); i++) {
			Word word = (Word)vocabwrapper.mainvocabs.get(i);
			idwrapper.ids.add((Long)word.getId());
			for (Long reference : word.getReferences()) {
				idwrapper.references.add((Long)reference);
			}
		}
		
		// check if referenced ids are already contained in ids set
		while (!idwrapper.references.isEmpty()) {
			Object[] temp_references = idwrapper.references.toArray();
			for (Object r : temp_references) {
				
				Long reference = (Long)r;
				
				// reference is already in vocablist
				if (idwrapper.ids.contains((Long)reference)) {
					idwrapper.references.remove((Long)reference);
					
				// reference is not in vocablist	
				}else {
					Word referencedWord = Core.getDictionary().getWordByID(reference);
					if (referencedWord != null) {
						for (Long refref : referencedWord.getReferences()) {
							if (!idwrapper.ids.contains((Long)refref)) {
								idwrapper.references.add((Long)refref);
								idwrapper.ids.add((Long)reference);
								vocabwrapper.referenes.add(referencedWord);
							}
						}
						
					// referenced word doesn't exist	
					}else {
						idwrapper.references.remove((Long)reference);
					}
				}
			}
		}
	}
	
	private void fillFileWithVocabularyListInfos(File file, WordsWrapper wrapper, WordList list) {
		FileOutputStream fOut;
		try {
			Logger.log("Start writing to File", "IOMANAGER");
			fOut = new FileOutputStream(file);
			OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
			
			// define special symbols
			String endLine = "    \n";
			String seperator = "    |    ";
			
			// write list meta data
			myOutWriter.append(list.getListName() + endLine);   // listname
			Language language = list.getLanguage();
			switch (language) {						  // language
			case CHINESE:
				Logger.log("Set language to Chinese", "IOMANAGER");
				myOutWriter.append("Chinese" + endLine);
				break;
			case KOREAN:
				Logger.log("Set language to Korean", "IOMANAGER");
				myOutWriter.append("Korean" + endLine);
				break;
			}
			
			// write list settings
			String isTranslationQuestionString = (list.isTranslationQuestion()) ? "true" : "false";
			myOutWriter.append("" + isTranslationQuestionString + endLine); 							// is Translation Question
			String areCharactersColored = (list.areCharacteresColored()) ? "true" : "false";
			myOutWriter.append("" + areCharactersColored + endLine); 									// are Characters Colored
			myOutWriter.append("" + list.getNumberOfNewVocabularies() + endLine); 						// number of new vocabularies
			myOutWriter.append("" + list.getNumberOfRevisedVocabularies() + endLine); 					// number of revised vocabularies
			myOutWriter.append("" + list.getDateOfLastFinish() + endLine); 								// date of last finish
			
			// write number of vocabs to follow
			myOutWriter.append("" + wrapper.mainvocabs.size() + endLine); 	// number of vocabs
			Logger.log("Set list number of vocabularies " + wrapper.mainvocabs.size(), "IOMANAGER");
			myOutWriter.append("" + wrapper.referenes.size() + endLine);   // number of references
			Logger.log("Set list number of referenced vocabularies " + wrapper.referenes.size(), "IOMANAGER");
			
			//write vocabulary
			for(int ind = 0; ind < wrapper.mainvocabs.size(); ind++) {
				Word word = (Word) wrapper.mainvocabs.get(ind);
				// add word information
				myOutWriter.append("" + word.getId() + seperator);
				myOutWriter.append("" + word.getAllCharacters() + seperator);
				myOutWriter.append("" + word.getAllRomanizations() + seperator);
				myOutWriter.append("" + word.getTranslationsString() + seperator);
				myOutWriter.append("" + word.getReferencesString() + seperator);
				myOutWriter.append("" + word.getMnemonic() + seperator);	
				// add word settings
				myOutWriter.append("" + word.getNumberOfRepetitions() + seperator);
				myOutWriter.append("" + word.getEFactor() + seperator);
				myOutWriter.append("" + word.getInterRepetitionInterval() + seperator);
				myOutWriter.append("" + word.getDateOfReview() + endLine);	
			}
			
			myOutWriter.append("References" + seperator);
			
			//write references
			for(int ind = 0; ind < wrapper.referenes.size(); ind++) {
				Word word = (Word) wrapper.referenes.get(ind);
				myOutWriter.append("" + word.getId() + seperator);
				myOutWriter.append("" + word.getAllCharacters() + seperator);
				myOutWriter.append("" + word.getAllRomanizations() + seperator);
				myOutWriter.append("" + word.getTranslationsString() + seperator);
				myOutWriter.append("" + word.getReferencesString() + seperator);
				myOutWriter.append("" + word.getMnemonic() +  seperator);
				// add word settings
				myOutWriter.append("" + word.getNumberOfRepetitions() + seperator);
				myOutWriter.append("" + word.getEFactor() + seperator);
				myOutWriter.append("" + word.getInterRepetitionInterval() + seperator);
				myOutWriter.append("" + word.getDateOfReview() + endLine);	
			}
			
			// close outputstream
			myOutWriter.close();
			fOut.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	//-------------------------------------------------------------------------------------------------------------------
	// RENAMING
	//-------------------------------------------------------------------------------------------------------------------	
	private void fillRenamings(WordsWrapper wrapper, HashMap<Long, Long> renamings) {
		for (long newId = 0; newId < wrapper.mainvocabs.size(); newId++) {
			Word word = (Word)wrapper.mainvocabs.get((int)newId);
			renamings.put((Long)word.getId(), (Long)newId);
		}
		
		for (long newId = 0; newId < wrapper.referenes.size(); newId++) {
			Word word = (Word)wrapper.referenes.get((int)newId);
			renamings.put((Long)word.getId(), (Long)newId + wrapper.mainvocabs.size());
		}
	}
	
	private void applyRenamings(WordsWrapper wrapper, HashMap<Long, Long> renamings) {
		Logger.log("Apply renamings", "IOMANAGER");
		for (int i = 0; i < wrapper.mainvocabs.size(); i++) {
			
			// set new id
			Word word = (Word)wrapper.mainvocabs.get((int)i);
			Long newId = renamings.get((Long)word.getId());
			if (newId != null) {
				Logger.log("Rename id " + word.getAllCharacters() + ": " + word.getId() + " --> " + newId, "IOMANAGER");
				word.setId(newId);
			}	
			
			// set new references
			ArrayList<Long> newReferences = new ArrayList<>();
			for (Long oldReference : word.getReferences()) {
				Long newReference = renamings.get((Long)oldReference);
				if (newReference != null) {
					Logger.log("Rename reference: " + oldReference + " --> " + newReference, "IOMANAGER");
					newReferences.add(newReference);
				}
			}
			word.setReferences(newReferences);
		}
		
		// referenced words that are not included in the list but in the dictionary
		for (int i = 0; i < wrapper.referenes.size(); i++) {
			
			// set new id
			Word word = (Word)wrapper.referenes.get((int)i);
			Long newId = renamings.get((Long)word.getId());
			if (newId != null) {
				word.setId(newId);
			}	
			
			// set new references
			ArrayList<Long> newReferences = new ArrayList<>();
			for (Long oldReference : word.getReferences()) {
				Long newReference = renamings.get((Long)oldReference);
				if (newReference != null) {
					newReferences.add(newReference);
				}
			}
			word.setReferences(newReferences);
		}
	}
	
	private void renameListAndAddToVocabularyList(WordList vocabList, LexicalArrayList vocabularies, LexicalArrayList referencedWords) {
		
		// create renaming hashmap
		HashMap<Long, Long> renamings = new HashMap<>();
		HashSet<Long> tempIds = (HashSet<Long>) Core.getDictionary().getAllUsedIds().clone();
		
		ArrayList<Word> wordstoBeRemoved = new ArrayList<>();
		
		// create renamings for vcoabularies
		Logger.log("While renaming vocab there are " + vocabularies.size() + " words in the list", "IOMANAGER");
		for (Lexicalsortable sortable : vocabularies) {
			
			Word word = (Word)sortable;
			Long id = word.getId();
			
			// remove word from list if it already exists and just add rename rule for the id of the existing word
			Long exisitingId = Core.getDictionary().getIdByCharactersAndRomanization(word.getAllCharacters(), word.getAllRomanizations());
			if (exisitingId >= 0) {
				Logger.log("Word " + word.getAllCharacters() + " already exists", "IOMANAGER");
				renamings.put((Long)id, (Long)exisitingId);
				wordstoBeRemoved.add(word);
			}else {		
				// if id is already occupied add new renaming
				Logger.log("Check if id " + id + " is already occupied", "IOMANAGER");
				if (Core.getDictionary().getWordByID(id) != null) {
					Logger.log("Ids is already occupied, trying to get a new id", "IOMANAGER");
					Long newId = getNextFreeId(tempIds);
					tempIds.add((Long)newId);
					renamings.put((Long)id, (Long)newId);
				}
			}
			
			Logger.log("---------------", "IOMANAGER");
		}
		
		Logger.log("Remove words that already exist", "IOMANAGER");
		for (Word word : wordstoBeRemoved) {
			vocabularies.remove(word);
		}
		// clear wordsToBeRemoved
		wordstoBeRemoved.clear();
		
		Logger.log("While renaming referenced vocab there are " + referencedWords.size() + " referenced words in the list", "IOMANAGER");
		for (Lexicalsortable sortable : referencedWords) {
			Word word = (Word)sortable;
			Long id = word.getId();
			
			Long exisitingId = Core.getDictionary().getIdByCharactersAndRomanization(word.getAllCharacters(), word.getAllRomanizations());
			if (exisitingId >= 0) {
				Logger.log("Word " + word.getAllCharacters() + " already exists", "IOMANAGER");
				renamings.put((Long)id, (Long)exisitingId);
				wordstoBeRemoved.add(word);
			}else {	
				// if id is already occupied add new renaming
				Logger.log("Check if id " + id + " is already occupied", "IOMANAGER");
				if (Core.getDictionary().getWordByID(id) != null) {
					Logger.log("Ids is already occupied, trying to get a new id", "IOMANAGER");
					Long newId = getNextFreeId(tempIds);
					tempIds.add((Long)newId);
					renamings.put((Long)id, (Long)newId);
				}
			}
		}
		
		Logger.log("Remove referenced words that already exist", "IOMANAGER");
		for (Word word : wordstoBeRemoved) {
			referencedWords.remove(word);
		}
		referencedWords.clear();
		
		WordsWrapper wrapper = new WordsWrapper();
		wrapper.mainvocabs = vocabularies;
		wrapper.referenes = referencedWords;
		applyRenamings(wrapper, renamings);
		
		// add referenced words
		vocabList.addVocabularys(wrapper.mainvocabs);
		vocabList.addReferencedVocabularys(wrapper.referenes);
	}


	
	//-------------------------------------------------------------------------------------------------------------------
	// GETTING FREE ID FOR REAMING
	//-------------------------------------------------------------------------------------------------------------------		
	private Long getNextFreeId(HashSet<Long> tempIds) {
		long freeId = 0;
		
		// find free id
		while (freeId >= 0) {
			if (tempIds.contains((Long)(freeId))) {
				freeId++;
			}else {
				break;
			}
		}
		
		if (freeId < 0) Logger.log("Dictionary ran out of free ids", "ALL");
		Logger.log("Next free id is " + freeId, "Dictionary");
		return freeId;
	}
	
	
	
	//-------------------------------------------------------------------------------------------------------------------
	// DEBUG
	//-------------------------------------------------------------------------------------------------------------------	
	public void readFile(File file) {
		try {
			// setup stream
			FileInputStream fIn = new FileInputStream(file);
			BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
			
			// load list informations
			int counter = 0;
			while (true) {
				String str = myReader.readLine();
				if (str != null) {
					String lineNumber = intToString(counter, 3);
					Logger.log(lineNumber + ": " + str, "ALL");
				}else {
					break;
				}
				counter++;
			}

			// close stream
			myReader.close();
			
		} catch (Exception e) {
			displayMessage("Exception while loading vocabularyList! What: " + e.getMessage());
		}
	}
	
	private String intToString(int number, int sizeOfNumber) {
		String intString = "" + number;
		
		int sizeOfString = intString.length();
		int numberOfZeros = sizeOfNumber - sizeOfString;
		String zeros = "";
		for (int i = 0; i < numberOfZeros; i++) {
			zeros += "0";
		}
		intString = zeros + intString;
		
		return intString;
	}
	
}
