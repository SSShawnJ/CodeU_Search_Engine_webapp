package controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.services.vision.v1.model.EntityAnnotation;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import resources.DetectImage;
import resources.JedisIndex;
import resources.SpringConfig;
import resources.WikiSearch;

@Controller
public class ImageSearchingController {
	
	//load imageSearch page UI
	@RequestMapping(value = "/searchImage", method = RequestMethod.GET)
	   public String imageSearch() {
	      return "imageSearch";
	}
	
	//get the uploaded image file and perform image searching using Google Cloud Vision API 
	@RequestMapping(value = "/imageSearchResult", method = RequestMethod.POST)
	public String searchForImage(@RequestParam MultipartFile imagefile,ModelMap model) throws IOException, GeneralSecurityException {
		StringBuilder result=new StringBuilder();
		
		//detect landmark and logo
		DetectImage detectImage = new DetectImage(DetectImage.getVisionService());
		//detect landmark
		List<EntityAnnotation> imageAnnotation = detectImage.identifyLandmark(imagefile);
		
		//if landmark annotation is not found, detect logo
		if(imageAnnotation==null){
			imageAnnotation = detectImage.identifyLogo(imagefile);
		}
		
		//if image searching found result
	    if(imageAnnotation!=null){
	    	for (EntityAnnotation annotation : imageAnnotation) {
	    		result.append(annotation.getDescription());
	    	}  
	    	
	    	
	    	//////search for pages that is relevant to this result////
	    	
	    	//make JedisIndex object
	    	AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(SpringConfig.class);
			JedisPool jedisPool=annotationConfigApplicationContext.getBean(JedisPool.class);
			Jedis jedis=jedisPool.getResource();
			annotationConfigApplicationContext.close();
	    	//connect to redis and set up JedisIndex
			JedisIndex index = new JedisIndex(jedis);
			
			
			//build html elements and return to View
			StringBuilder x=new StringBuilder();
			
			//search web pages
			String searchWord=result.toString();
			WikiSearch searchResult=WikiSearch.searchPages(searchWord,index);
			List<Entry<String,Double>> pages;
			//if page searching result is found
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
			}
			model.addAttribute("word", x.toString());
			
	    }
	    //image searching result is not found
	    else{
	    	result.append("<img src="+'"'+"/images/noresultstoast2.png"+'"'+" width=600,"+ " height=430 " +"/>");
	    }
	    
	    //add result to web page
		model.addAttribute("annotation", result.toString());
		return "imageSearchResult";
		    
	}
	
	//load imageSearchingResult page UI
	@RequestMapping(value = "/imageSearchResult", method = RequestMethod.GET)
	   public String imageSearchResult() {
	      return "imageSearchResult";
	}


}
