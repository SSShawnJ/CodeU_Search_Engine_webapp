package controller;

import java.util.List;
import java.util.Map.Entry;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import resources.JedisIndex;
import resources.SpringConfig;
import resources.WikiSearch;


//This controller handles the request from home page /, /index and /search page

@Controller
public class IndexPageController {
	

	//load home page UI
	@RequestMapping(value = "/", method = RequestMethod.GET)
	   public String getSearchWord() {
	      return "index";
	}
	
	
	//get the search word and perform searching function and return the result
	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public String search(@RequestParam String word,ModelMap model) {
		//make JedisIndex object
		AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(SpringConfig.class);
		JedisPool jedisPool=annotationConfigApplicationContext.getBean(JedisPool.class);
		Jedis jedis=jedisPool.getResource();
		annotationConfigApplicationContext.close();
		//connect to redis and set up jedis
		JedisIndex index = new JedisIndex(jedis);
		
		
		//////search//////
		//build html elements and return to View
		StringBuilder x=new StringBuilder();
		//checking search key words are not empty	
		if(!word.equals("")){	
			WikiSearch searchResult=searchPages(word,index);
			List<Entry<String,Double>> pages;
			
			//if page searching result is found
			if(searchResult!=null){
				//sort the page according to relevance
				pages=searchResult.sort();
				//add to HTML output
				for(Entry<String,Double> entry:pages){
					String url=entry.getKey();
					String title=url.substring(url.lastIndexOf('/')+1, url.length()).replace('_', ' ');
					x.append("<a href="+'"'+url+'"'+"class="+'"'+"list-group-item"+'"'+" >"+
				"<h4 class="+'"'+"list-group-item-heading"+'"'+" >"+title+"</h4>"+
				"<p class="+'"'+"list-group-item-text"+'"'+" >"+url+"</p> " +
				"<p class="+'"'+"list-group-item-text"+'"'+" >"+entry.getValue()+"</p> "+"</a>");
				}
				
				//return suggestions if no result is found
				if(x.length()==0){
					x.append(suggestions(word));
				}
			}
		}
		else{
			x.append(suggestions(word));
		}	
		
		//set model attribute
		model.addAttribute("word", x.toString());
		model.addAttribute("searchWord",word);
		//return result page to the user
		return "result";
	}
	
	
	private static String suggestions(String word){
		return "<h4 class="+'"'+"list-group-item-heading"+'"'+">"+"Sorry, your search - "+word+" - does not match any documents.</h4>"+
				"<p class="+'"'+"list-group-item-text"+'"'+" >"+"Suggestions:"+"</p> "
				+"<p class="+'"'+"list-group-item-text"+'"'+" >"+"Make sure all words are spelled correctly."+"</p> "
				+"<p class="+'"'+"list-group-item-text"+'"'+" >"+"Try different keywords."+"</p> "
				+"<p class="+'"'+"list-group-item-text"+'"'+" >"+"Try more general keywords."+"</p> "
				+"<p class="+'"'+"list-group-item-text"+'"'+" >"+"Try fewer keywords."+"</p> ";
	}
	
	
	/**
	 * Search for pages associated with the key words.Also supports AND(Exact),OR,EXCLUDE query
	 * 
	 * @param term String of search key words
	 * @param index JedisIndex object
	 * @return WikiSearch class containing result web pages
	 */
	private static WikiSearch searchPages(String term,JedisIndex index){
		//search for key words one by one
		String[] termArray=term.trim().split(" ");
		WikiSearch searchResult=WikiSearch.search(termArray[0], index);
		
		//iterate through search term one by one and calculate tf-idf relevance	
		int iterator=1;
		while(iterator<termArray.length){
			String t=termArray[iterator];
			//subtract or exclude pages that contain a specific term
			if(t.charAt(0)=='-'){
				searchResult=searchResult.minus(WikiSearch.search(t.substring(1),index));
			}
			//include pages that contains either or all of the search term
			else if(t.equals("|")){
				searchResult=searchResult.or(WikiSearch.search(t.substring(1),index));
			}
			//include the pages that have this key words exactly
			else if(t.charAt(0)=='"' && t.charAt(t.length()-1)=='"'){
				searchResult=searchResult.and(WikiSearch.search(t.substring(1,t.length()-1),index));
			}
			//general case, add the tf_idf relevance together
			else{
				searchResult=searchResult.or(WikiSearch.search(t,index));
			}
			iterator++;
		}
		
		return searchResult;
	}
	
	
}

