package controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.services.vision.v1.model.EntityAnnotation;

import resources.DetectImage;

// This controller handles the request from /seachImage and /imageSearchResult

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
		
		//if found result
	    if(imageAnnotation!=null){
	    	for (EntityAnnotation annotation : imageAnnotation) {
	    		result.append(annotation.getDescription());
	    	}  
	    }
	    //result not found
	    else{
	    	result.append("No result is found.");
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
