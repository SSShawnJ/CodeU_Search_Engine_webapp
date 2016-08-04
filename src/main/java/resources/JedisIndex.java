package resources;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jsoup.select.Elements;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

/**
 * Represents a Redis-backed web search index.
 * 
 */
public class JedisIndex {

	
	private static final String PageWordsKey="PageWords";
	
	private Jedis jedis;
	
	//total number of documents in the corpus 
	private int N;

	/**
	 * Constructor.
	 * 
	 * @param jedis
	 */
	public JedisIndex(Jedis jedis) {
		this.jedis = jedis;
		this.setN(urlSetKeys().size());
	}
	
	/**
	 * get number of documents in the corpus
	 * @return int value
	 */
	public int getN() {
		return N;
	}

	/**
	 * set number of documents in the corpus
	 * @return 
	 */
	public void setN(int n) {
		N = n;
	}
	
	/**
	 * Returns the Redis key for a given search term.
	 * 
	 * @return Redis key.
	 */
	private String urlSetKey(String term) {
		return "URLSet:" + term;
	}
	
	/**
	 * Returns the Redis key for a URL's TermCounter.
	 * 
	 * @return Redis key.
	 */
	private String termCounterKey(String url) {
		return "TermCounter:" + url;
	}
	
	/**
	 * Returns the Redis key for a URL's total words count.
	 * 
	 * @return Redis key.
	 */
	private String pageWordsKey(String url) {
		return "PageWords:" + url;
	}

	/**
	 * Checks whether we have a TermCounter for a given URL.
	 * 
	 * @param url
	 * @return
	 */
	public boolean isIndexed(String url) {
		String redisKey = termCounterKey(url);
		return jedis.exists(redisKey);
	}
	
	/**
	 * Adds a URL to the set associated with `term`.
	 * 
	 * @param term
	 * @param tc
	 */
	public void add(String term, TermCounter tc) {
		jedis.sadd(urlSetKey(term), tc.getLabel());
	}

	/**
	 * Looks up a search term and returns a set of URLs.
	 * 
	 * @param term
	 * @return Set of URLs.
	 */
	public Set<String> getURLs(String term) {
		Set<String> set = jedis.smembers(urlSetKey(term));
		return set;
	}
	
	/**
	 * Looks up a url and returns total number of words in this url document.
	 * 
	 * @param url
	 * @return Integer of number of words in this document.
	 */
	public Integer getWordsCount(String url) {
		String redisKey=pageWordsKey(url);
		String count = jedis.hget(PageWordsKey, redisKey);
		return new Integer(count);
	}

	/**
	 * returns average words count in all documents
	 * 
	 * @return double avg
	 */
	public double getAvgWordsCount(){
		Map<String,String> map=jedis.hgetAll(PageWordsKey);
		int length=map.size();
		Double avg=0.0;
		for(Entry<String,String> entry:map.entrySet()){
			avg+=Double.parseDouble(entry.getValue())/length;
		}
		return avg;
	}
	
	
	/**
	 * Looks up a term and returns a map from URL to count.
	 * 
	 * @param term
	 * @return Map from URL to count.
	 */
	public Map<String, Integer> getCounts(String term) {
		// convert the set of strings to a list so we get the
				// same traversal order every time
				List<String> urls = new ArrayList<String>();
				urls.addAll(getURLs(term));

				// construct a transaction to perform all lookups
				Transaction t = jedis.multi();
				for (String url: urls) {
					String redisKey = termCounterKey(url);
					t.hget(redisKey, term);
				}
				List<Object> res = t.exec();

				// iterate the results and make the map
				Map<String, Integer> map = new HashMap<String, Integer>();
				int i = 0;
				for (String url: urls) {
					//System.out.println(url);
					Integer count = new Integer((String) res.get(i++));
					map.put(url, count);
				}
				return map;
	}

	

	/**
	 * Returns the number of times the given term appears at the given URL.
	 * 
	 * @param url
	 * @param term
	 * @return
	 */
	public Integer getCount(String url, String term) {
		String redisKey = termCounterKey(url);
		String count = jedis.hget(redisKey, term);
		return new Integer(count);
	}

	/**
	 * Add a page to the index.
	 * 
	 * @param url         URL of the page.
	 * @param paragraphs  Collection of elements that should be indexed.
	 */
	public void indexPage(String url, Elements paragraphs) {
		System.out.println("indexing:"+url);
		//System.out.println("Indexing " + url);
		
		// make a TermCounter and count the terms in the paragraphs
		TermCounter tc = new TermCounter(url);
		tc.processElements(paragraphs);
		
		// push the contents of the TermCounter to Redis
		pushTermCounterToRedis(tc);
	}

	/**
	 * Pushes the contents of the TermCounter to Redis.
	 * 
	 * @param tc
	 * @return List of return values from Redis.
	 */
	public List<Object> pushTermCounterToRedis(TermCounter tc) {
		Transaction t = jedis.multi();
		
		String url = tc.getLabel();
		String hashname = termCounterKey(url);
		
		// if this page has already been indexed; delete the old hash
		t.del(hashname);

		// for each term, add an entry in the termcounter and a new
		// member of the index
		for (String term: tc.keySet()) {
			Integer count = tc.get(term);
			t.hset(hashname, term, count.toString());
			t.hset(PageWordsKey, pageWordsKey(url), Integer.toString(tc.getTotalTerms()));
			t.sadd(urlSetKey(term), url);
		}
		List<Object> res = t.exec();
		return res;
	}

	/**
	 * Prints the contents of the index.
	 * 
	 * Should be used for development and testing, not production.
	 */
	public void printIndex() {
		// loop through the search terms
		for (String term: termSet()) {
			System.out.println(term);
			
			// for each term, print the pages where it appears
			Set<String> urls = getURLs(term);
			for (String url: urls) {
				Integer count = getCount(url, term);
				System.out.println("    " + url + " " + count);
			}
		}
	}

	/**
	 * Returns the set of terms that have been indexed.
	 * 
	 * Should be used for development and testing, not production.
	 * 
	 * @return
	 */
	public Set<String> termSet() {
		Set<String> keys = urlSetKeys();
		Set<String> terms = new HashSet<String>();
		for (String key: keys) {
			String[] array = key.split(":");
			if (array.length < 2) {
				terms.add("");
			} else {
				terms.add(array[1]);
			}
		}
		return terms;
	}

	/**
	 * Returns URLSet keys for the terms that have been indexed.
	 * 
	 * Should be used for development and testing, not production.
	 * 
	 * @return
	 */
	public Set<String> urlSetKeys() {
		return jedis.keys("URLSet:*");
	}

	/**
	 * Returns TermCounter keys for the URLS that have been indexed.
	 * 
	 * Should be used for development and testing, not production.
	 * 
	 * @return
	 */
	public Set<String> termCounterKeys() {
		return jedis.keys("TermCounter:*");
	}
	
	/**
	 * Returns pageWordsCounter keys for the URLS that have been indexed.
	 * 
	 * Should be used for development and testing, not production.
	 * 
	 * @return
	 */
	public Set<String> pageWordsCounterKeys() {
		return jedis.keys("PageWords:*");
	}

	/**
	 * Deletes all URLSet objects from the database.
	 * 
	 * Should be used for development and testing, not production.
	 * 
	 * @return
	 */
	public void deleteURLSets() {
		Set<String> keys = urlSetKeys();
		Transaction t = jedis.multi();
		for (String key: keys) {
			t.del(key);
		}
		t.exec();
	}

	/**
	 * Deletes all URLSet objects from the database.
	 * 
	 * Should be used for development and testing, not production.
	 * 
	 * @return
	 */
	public void deleteTermCounters() {
		Set<String> keys = termCounterKeys();
		Transaction t = jedis.multi();
		for (String key: keys) {
			t.del(key);
		}
		t.exec();
	}
	
	/**
	 * Deletes all pageWords objects from the database.
	 * 
	 * Should be used for development and testing, not production.
	 * 
	 * @return
	 */
	public void deletePageWordsCounters() {
		Set<String> keys = pageWordsCounterKeys();
		Transaction t = jedis.multi();
		for (String key: keys) {
			t.del(key);
		}
		t.exec();
	}

	/**
	 * Deletes all keys from the database.
	 * 
	 * Should be used for development and testing, not production.
	 * 
	 * @return
	 */
	public void deleteAllKeys() {
		Set<String> keys = jedis.keys("*");
		Transaction t = jedis.multi();
		for (String key: keys) {
			t.del(key);
		}
		t.exec();
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		Jedis jedis = JedisMaker.make();
		JedisIndex index = new JedisIndex(jedis);
		
		index.deleteTermCounters();
		index.deleteURLSets();
		index.deletePageWordsCounters();
		index.deleteAllKeys();
		//loadIndex(index);
		
		Map<String, Integer> map = index.getCounts("the");
		for (Entry<String, Integer> entry: map.entrySet()) {
			System.out.println(entry);
		}
	}

	/**
	 * Stores two pages in the index for testing purposes.
	 * 
	 * @return
	 * @throws IOException
	 */
	private static void loadIndex(JedisIndex index) throws IOException {
		WikiFetcher wf = new WikiFetcher();

		String url = "https://en.wikipedia.org/wiki/Java_(programming_language)";
		Elements paragraphs = wf.readWikipedia(url);
		index.indexPage(url, paragraphs);
		
		url = "https://en.wikipedia.org/wiki/Programming_language";
		paragraphs = wf.readWikipedia(url);
		index.indexPage(url, paragraphs);
	}

	
}
