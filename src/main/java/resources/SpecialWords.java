package resources;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SpecialWords {

	private static final Set<String> unimportantWords=new HashSet<String>(Arrays.asList("of","an","a","the"));
	
	public static boolean isUnimportant(String word){
		return unimportantWords.contains(word);
	}
	
}
