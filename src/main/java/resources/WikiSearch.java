package resources;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import redis.clients.jedis.Jedis;


/**
 * Represents the results of a search query.
 *
 */
public class WikiSearch {
	
	
	
	// map from URLs that contain the term(s) to relevance score
	private Map<String, Double> map;

	/**
	 * Constructor.
	 * 
	 * @param map
	 */
	public WikiSearch(Map<String, Double> map) {
		this.map = map;
	}
	
	/**
	 * Looks up the relevance of a given URL.
	 * 
	 * @param url
	 * @return
	 */
	public Double getRelevance(String url) {
		Double relevance = map.get(url);
		return relevance==null ? 0: relevance;
	}
	
	/**
	 * Prints the contents in order of term frequency.
	 * 
	 * @param map
	 */
	private  void print() {
		List<Entry<String, Double>> entries = sort();
		for (Entry<String, Double> entry: entries) {
			System.out.println(entry);
		}
	}
	
	/**
	 * Computes the union of two search results.
	 * 
	 * @param that
	 * @return New WikiSearch object.
	 */
	public WikiSearch or(WikiSearch that) {
		Map<String,Double> orMap=new HashMap<String,Double>(this.map);
		
		for(String thatKey:that.map.keySet()){
			if(orMap.containsKey(thatKey)){
				double newRelavance=orMap.get(thatKey)+that.getRelevance(thatKey);
				orMap.put(thatKey,newRelavance);
			}
			else{
				orMap.put(thatKey,that.getRelevance(thatKey));
			}
		}
		return new WikiSearch(orMap);
	}
	
	/**
	 * Computes the intersection of two search results.
	 * 
	 * @param that
	 * @return New WikiSearch object.
	 */
	public WikiSearch and(WikiSearch that) {
        Map<String,Double> andMap=new HashMap<>();
        Set<String> keySet=this.map.size()>that.map.size()?that.map.keySet():this.map.keySet();
        for(String key:keySet){
        	double thisR=this.getRelevance(key);
        	double thatR=that.getRelevance(key);
        	if(thisR!=0 && thatR!=0){
        		andMap.put(key, thisR+thatR);
        	}
        }
		return new WikiSearch(andMap);
	}
	
	/**
	 * Computes the intersection of two search results.
	 * 
	 * @param that
	 * @return New WikiSearch object.
	 */
	public WikiSearch minus(WikiSearch that) {
        Map<String,Double> minusMap=new HashMap<>();
        for(String key:this.map.keySet()){
        	if(!that.map.containsKey(key)){
        		minusMap.put(key, this.getRelevance(key));
        	}
        }
		return new WikiSearch(minusMap);
	}
	
	/**
	 * Computes the relevance of a search with multiple terms.
	 * 
	 * @param rel1: relevance score for the first search
	 * @param rel2: relevance score for the second search
	 * @return
	 */
	protected double totalRelevance(Double rel1, Double rel2) {
		// simple starting place: relevance is the sum of the term frequencies.
		return rel1 + rel2;
	}

	/**
	 * Sort the results by relevance.
	 * 
	 * @return List of entries with URL and relevance.
	 */
	public List<Entry<String, Double>> sort() {
        Comparator<Entry<String,Double>> comparator=new Comparator<Entry<String,Double>>(){

			@Override
			public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
				if(o1.getValue()>o2.getValue()) return -1;
				else if(o1.getValue()<o2.getValue()) return 1;
				else return 0;
			}	
        };
        
        List<Entry<String,Double>> list=new ArrayList<>();
        list.addAll(this.map.entrySet());
        Collections.sort(list, comparator);
		return list;
	}

	
	
	/**
	 * Performs a search and makes a WikiSearch object.
	 * Implements BM25+ version of TF-IDF ranking algorithm 
	 * 
	 * @param term
	 * @param index
	 * @return a url map associated with tf-idf relevance value; or null if search term is empty
	 */
	public static WikiSearch search(String term, JedisIndex index) {
		//checking search term is not empty	
		if(!term.equals("")){
			String[] termArray=term.trim().split(" ");
			Map<String,Double> map=new HashMap<>();
			
			//get total number of documents in the corpus 
			int N=index.getN();
			//get avg number of words in all documentation the corpus 
			double avg=index.getAvgWordsCount();
			
			//iterate through search term one by one and calculate tf-idf relevance	
			for (int i = 0; i < termArray.length; i++) {
				//make all key words to lower case
				String t=termArray[i].trim().toLowerCase();

				if(SpecialWords.isUnimportant(t) && termArray.length>1) continue;
				//get the mapping from urls to termCount for this particular search term
				Map<String, Integer> termMap = index.getCounts(t);
				//get number of documents where the term appears
				int d=index.getURLs(t).size();
				
				// for each url, calculate and increase/store the tf-idf relevance value 
				for(String url:termMap.keySet()){
					int termCount=termMap.get(url);
					int D=index.getWordsCount(url);
					//calculate tf-idf relevance value
					double relevance=TFIDF.TF(termCount,avg,D)*TFIDF.IDF(N,d);
					//add calculated relevance to the result map
					if(map.containsKey(url)){
						double newRelevance=map.get(url)+relevance;
						map.put(url, newRelevance);
					}
					else{
						map.put(url, relevance);
					}
				}
				
			}	
			return new WikiSearch(map);
		
		}
		//search term is empty,return null
		else
			return null;
	}

	
	//unit testing
	public static void main(String[] args) throws IOException {
		
		// make a JedisIndex
		Jedis jedis = JedisMaker.make();
		JedisIndex index = new JedisIndex(jedis); 
		
		// search for the first term
		String term1 = "java";
		System.out.println("Query: " + term1);
		WikiSearch search1 = search(term1, index);
		search1.print();
		
		// search for the second term
		String term2 = "programming";
		System.out.println("Query: " + term2);
		WikiSearch search2 = search(term2, index);
		search2.print();
		
		// compute the intersection of the searches
		System.out.println("Query: " + term1 + " AND " + term2);
		WikiSearch intersection = search1.and(search2);
		intersection.print();
	}
}

