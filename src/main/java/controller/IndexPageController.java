package controller;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.services.vision.v1.model.EntityAnnotation;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import resources.DetectLandmark;
import resources.JedisIndex;
import resources.SpringConfig;
import resources.WikiSearch;



@Controller
public class IndexPageController {
	
	private static final Logger logger = LoggerFactory
			.getLogger(IndexPageController.class);
	
	static Jedis jedis;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	   public String getSearchWord() {
	      return "index";
	}
	
	
	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public String search(@RequestParam String word,ModelMap model) {
		
		//System.out.println("Launching Redis sample. Configured with Spring");
		ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfig.class);
		JedisPool pool = ctx.getBean(JedisPool.class);
		jedis = pool.getResource();
		

		
		JedisIndex index = new JedisIndex(jedis);
		
		
		WikiSearch searchResult=WikiSearch.search(word, index);
		List<Entry<String,Integer>> pages=searchResult.sort();
		
		
		//build html elements and return to the View
		StringBuilder x=new StringBuilder();
		for(Entry<String,Integer> entry:pages){
			x.append("<a href="+'"'+entry.getKey()+'"'+"class="+'"'+"list-group-item"+'"'+" >"+
		"<h4 class="+'"'+"list-group-item-heading"+'"'+" >"+entry.getKey()+"</h4>"+
		"<p class="+'"'+"list-group-item-text"+'"'+" >"+entry.getValue()+"</p> "+"</a>");
		}
		
		if(x.length()==0){
			x.append("<a><h4 class="+'"'+"list-group-item-heading"+'"'+">"+"Sorry, your search - "+word+" - does not match any documents.</h4>"+
					"<p class="+'"'+"list-group-item-text"+'"'+" >"+"Suggestions:"+"</p> "
					+"<p class="+'"'+"list-group-item-text"+'"'+" >"+"Make sure all words are spelled correctly."+"</p> "
					+"<p class="+'"'+"list-group-item-text"+'"'+" >"+"Try different keywords."+"</p> "
					+"<p class="+'"'+"list-group-item-text"+'"'+" >"+"Try more general keywords."+"</p> "
					+"<p class="+'"'+"list-group-item-text"+'"'+" >"+"Try fewer keywords."+"</p></a> ");
		}
		
		//set model attribute
		model.addAttribute("word", x.toString());
		model.addAttribute("searchWord",word);
		
		//return result page to the user
		return "result";
	}
	
	
	@RequestMapping(value = "/searchImage", method = RequestMethod.GET)
	   public String imageSearch() {
	      return "imageSearch";
	}
	
	@RequestMapping(value = "/imageSearchResult", method = RequestMethod.POST)
	public String searchForImage(@RequestParam MultipartFile imagefile,ModelMap model) throws IOException, GeneralSecurityException {

		
		DetectLandmark app = new DetectLandmark(DetectLandmark.getVisionService());
		    List<EntityAnnotation> landmarks = app.identifyLandmark(imagefile);
		    String x="";
		    for (EntityAnnotation annotation : landmarks) {
		     x+=annotation.getDescription();
		    }  
		model.addAttribute("location", x);
		return "imageSearchResult";
	}
	
	@RequestMapping(value = "/imageSearchResult", method = RequestMethod.GET)
	   public String imageSearchResult() {
	      return "imageSearchResult";
	}
}

