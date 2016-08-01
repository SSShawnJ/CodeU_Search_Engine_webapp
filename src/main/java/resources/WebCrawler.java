package resources;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import redis.clients.jedis.Jedis;


public class WebCrawler {
	// keeps track of where we started
	private final String source;
	
	// the index where the results go
	private JedisIndex index;
	
	// queue of URLs to be indexed
	private Queue<String> queue = new LinkedList<String>();
	
	// fetcher used to get pages from Wikipedia
	final static WikiFetcher wf = new WikiFetcher();

	/**
	 * Constructor.
	 * 
	 * @param source
	 * @param index
	 */
	public WebCrawler(String source, JedisIndex index) {
		this.source = source;
		this.index = index;
		queue.offer(source);
	}

	/**
	 * Returns the number of URLs in the queue.
	 * 
	 * @return
	 */
	public int queueSize() {
		return queue.size();	
	}
	
	/**
	 * Adds url to the end of the queue.
	 * 
	 * @param url
	 */
	public void addUrl(String url) {
		queue.offer(url);
	}

	/**
	 * Gets a URL from the queue and indexes it.
	 * @param b 
	 * 
	 * @return Number of pages indexed.
	 * @throws IOException
	 */
	public void crawl() throws IOException {
		if (queue.isEmpty()) {
            return;
        }
		
		String url=queue.poll();
     	if(index.isIndexed(url)){
     		return;
     	}
     	else{
     		Elements paragraphs=wf.fetchWikipedia(url);
     		index.indexPage(url,paragraphs);
     		//queueInternalLinks(paragraphs);
     		return;
     	}
   	}
	
	/**
	 * Parses paragraphs and adds internal links to the queue.
	 * 
	 * @param paragraphs
	 */
	// NOTE: absence of access level modifier means package-level
	void queueInternalLinks(Elements paragraphs) {
		for(Element node:paragraphs){
        	Elements n = node.select("a[href]");
        	for (Element e: n) {
           		String relURL = e.attr("href");
            	if (relURL.startsWith("/wiki/")) {
                	String absURL = e.attr("abs:href");
                	//System.out.println(absURL);
                	queue.offer(absURL);
            	}
       		}
        }
	}

	public static void main(String[] args) throws IOException {
		
		// make a WikiCrawler
		Jedis jedis = JedisMaker.make();
		JedisIndex index = new JedisIndex(jedis); 
		String source = "https://en.wikipedia.org/wiki/Java_(programming_language)";
		String urlStart = "https://en.wikipedia.org/wiki/";
		String[] pages = {"C_(programming_language)", "Tf–idf",
				//"Programming_language", "Object-oriented_programming", 
				"Computer_architecture",
				"Great_Wall_of_China", "Big_Ben", 
				"Wall",	"Mercedes-Benz",
				"Bread", "Pie", "Pi"
				};
		
		WebCrawler wc = new WebCrawler(source, index);
		
		// for testing purposes, load up the queue
		//Elements paragraphs = wf.fetchWikipedia(source);
		//wc.queueInternalLinks(paragraphs);
		
		// add pages want to index
		for (String page : pages) {
			wc.addUrl(urlStart + page);
		}
		
		// loop until we index a new page
		
		do {
			wc.crawl();
			
            // REMOVE THIS BREAK STATEMENT WHEN crawl() IS WORKING
            
		} while (!wc.queue.isEmpty());
		
//		System.out.println(wc.queueSize());
//		
//		System.out.println(index.urlSetKeys().size());
//		System.out.println(index.urlSetKeys());
	}
}
