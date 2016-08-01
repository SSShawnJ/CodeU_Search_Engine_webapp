package resources;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SpecialWords {

	//store set of unimportant words
	private static final Set<String> unimportantWords=new HashSet<String>(Arrays.asList(
			"the","of", "to", "and", "a", "in", "is", "it", "you", "that","she","her","he",
			"was", "for", "on","are", "with", "as", "i", "his", "they", "be", "at", "one", 
			"have","this","him"));
	
	/**
	 * check if a word is unimportant
	 * 
	 * @param word
	 * @return true if a word is unimportant and false otherwise
	 */
	public static boolean isUnimportant(String word){
		return unimportantWords.contains(word);
	}
	
}
