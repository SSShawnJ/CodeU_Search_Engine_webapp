package controller;

import java.util.List;
import java.util.Map.Entry;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import resources.JedisIndex;
import resources.SpringConfig;
import resources.WikiSearch;



@Controller
public class IndexPageController {
	
	static Jedis jedis;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	   public ModelAndView getSearchWord() {
	      return new ModelAndView("index", "command", new SearchWord());
	   }
	
	
	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public String search(@ModelAttribute("SpringWeb")SearchWord word,ModelMap model) {
		
		//System.out.println("Launching Redis sample. Configured with Spring");
		ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfig.class);
		JedisPool pool = ctx.getBean(JedisPool.class);
		jedis = pool.getResource();
		

		
		JedisIndex index = new JedisIndex(jedis);
		
		
		WikiSearch searchResult=WikiSearch.search(word.getWord(), index);
		List<Entry<String,Integer>> pages=searchResult.sort();
		
		
		//build html elements and return to the View
		StringBuilder x=new StringBuilder();
		for(Entry<String,Integer> entry:pages){
			x.append("<a href="+'"'+entry.getKey()+'"'+"class="+'"'+"list-group-item"+'"'+" >"+
		"<h4 class="+'"'+"list-group-item-heading"+'"'+" >"+entry.getKey()+"</h4>"+
		"<p class="+'"'+"list-group-item-text"+'"'+" >"+entry.getValue()+"</p> "+"</a>");
		}
		
		if(x.equals(""))
			x.append("<p>Sorry,result not found</p>");
		
		//set model attribute
		model.addAttribute("word", x.toString());  
		
		//return result page to the user
		return "result";
	}
	
	
	
}

