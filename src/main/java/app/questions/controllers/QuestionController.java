package app.questions.controllers;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.validation.Valid;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import app.questions.documents.Question;
import app.questions.models.QuestionModel;
import app.questions.services.QuestionService;
import app.questions.utilities.ListofQuestions;
import app.questions.utilities.QuestionDocumentToQuestionModel;

/**
 * Created by vinod on 22/05/18.
 */
@RestController
public class QuestionController {
	
	
	
	
	private QuestionService questionService;
	private QuestionDocumentToQuestionModel questionDocumentToQuestionModel;
	
	private List<QuestionModel> questionsList;
	
	@Autowired
	private ListofQuestions listofquestions; 
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	public void setQuestionService(QuestionService questionService) {
		this.questionService = questionService;
	}
	
	@Autowired
	 public void setQuestionDocumentToQuestionModel(QuestionDocumentToQuestionModel questionDocumentToQuestionModel) {
		this.questionDocumentToQuestionModel = questionDocumentToQuestionModel;
	}



	@RequestMapping("/")
	    public String redirtHome(){
		
	        return "questionviews/home";
	    }
	 
	 @RequestMapping("/question/new")
	    public String newProduct(Model model){
	       model.addAttribute("questionModel", new QuestionModel());
	        return "questionviews/questionform";
	    }
	 
	 @RequestMapping(value = "/question", method = RequestMethod.POST)
	    public String saveOrUpdateQuestion(@Valid QuestionModel questionModel, BindingResult bindingResult ){

		 Question savedQuestion= questionService.saveOrUpdateQuestionModel(questionModel);
		 
		 System.out.println("Saved Question  "+savedQuestion.getQuestion() );
	    
	        return "questionviews/displayquestions";
		
	 }
	 
	 @RequestMapping({"/questionviews/displayquestions", "/question"})
	    public String listQuestions(Model model){
	        model.addAttribute("questions", questionService.listAll());
	        
	        for (Question question: questionService.listAll()){
				 System.out.println("Question Name    "+question.getQuestion());
			 }
	        return "questionviews/displayquestions";
	    }
	 @RequestMapping("/display")
	    public String display(){
		 
		 for (Question question: questionService.listAll()){
			 System.out.println("Question Name    "+question.getQuestion());
		 }
	        return "questionviews/displayquestions";
	    }
	 
	 @RequestMapping("/file")
	    public String fileUpload(){
	    	
	       return "questionviews/fileupload";
	    }
	 
	 @RequestMapping(value="/questions/upload", method = RequestMethod.POST) 
	    public String singleFileUpload(@RequestParam("file") MultipartFile file,
	                                   RedirectAttributes redirectAttributes) {

	        if (file.isEmpty()) {
	            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
	            System.out.println("It is an empty file");
	            return "questionviews/fileupload";
	        }

	        questionsList=listofquestions.getListofQuestions(file);
	        questionService.saveQuestions(questionsList);
	        return "questionviews/home";
	    }
	 
	 @RequestMapping(value = "/questions/add", method = RequestMethod.POST)
	    public QuestionModel addQuestion(@RequestBody QuestionModel questionModel) throws JsonProcessingException{
		 
		 questionModel.setQuestion_uid(UUID.randomUUID());
		 
		 redisTemplate.opsForValue().set(questionModel.getQuestion_uid().toString(), objectMapper.writeValueAsString(questionModel));
	    
		 return questionModel;
	 	}
	 
	 @RequestMapping(value = "/questions/get/{key}", method = RequestMethod.GET)
	    public QuestionModel getQuestionByKey(@PathVariable String key) throws JsonParseException, JsonMappingException, IOException{
		 
		 return objectMapper.readValue(redisTemplate.opsForValue().get(key), QuestionModel.class);
	 	}
	 
	 @RequestMapping(value = "/questions/getAll", method = RequestMethod.GET)
	    public Set<String> getAllQuestions() {
		 
		 return redisTemplate.keys("*");
	 	}
	 @RequestMapping(value = "/questions/delete/{key}", method = RequestMethod.DELETE)
	    public String deleteQuestionByKey(@PathVariable String key) {
		 
		redisTemplate.delete(key);
		
		 return "deleted Key "+key;
	 	}
	 
	 
	 	
}
