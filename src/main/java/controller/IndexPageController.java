package controller;

import java.util.List;
import java.util.Map.Entry;

import org.springframework.context.ApplicationContext;
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
	
	public static final Jedis jedis=new AnnotationConfigApplicationContext(SpringConfig.class).getBean(JedisPool.class).getResource();
	

	//load home page UI
	@RequestMapping(value = "/", method = RequestMethod.GET)
	   public String getSearchWord() {
	      return "index";
	}
	
	
	//get the search word and perform searching function and return the result
	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public String search(@RequestParam String word,ModelMap model) {
		
		
		
		//connect to redis and set up jedis
		JedisIndex index = new JedisIndex(jedis);
		
		//search
		WikiSearch searchResult=WikiSearch.search(word, index);
		//build html elements and return to View
				StringBuilder x=new StringBuilder();
		
		List<Entry<String,Double>> pages;
		
		//if the user's query is not empty
		if(searchResult!=null){
			pages=searchResult.sort();
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
	
}

