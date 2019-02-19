package app.questions.utilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import app.questions.models.QuestionModel;



@Component
public class ListofQuestions {
	
	private QuestionModel questionModel;
	private static String UPLOADED_FOLDER = "C://tmp//";
	private List<QuestionModel> questionsList=new ArrayList<QuestionModel>();
	
	public List<QuestionModel> getListofQuestions(MultipartFile file){
		
		
		  try {

	            byte[] bytes = file.getBytes();
	            Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
	            Files.write(path, bytes);
	            
	            System.out.println("File uploaded successfully"+path);
	            
	            // Creating a Workbook from an Excel file (.xls or .xlsx)
	            Workbook workbook = WorkbookFactory.create(new File(path.toString()));

	            // Retrieving the number of sheets in the Workbook
	            System.out.println("Workbook has " + workbook.getNumberOfSheets() + " Sheets : ");

	            // Getting the Sheet at index zero
	            Sheet sheet = workbook.getSheetAt(0);
	            
	            // Create a DataFormatter to format and get each cell's value as String
	            DataFormatter dataFormatter = new DataFormatter();

	          for (Row row: sheet) {
	            	questionModel=new QuestionModel();
	            	
	            	int rownumber=0;
	                for(Cell cell: row) {
	                    String cellValue = dataFormatter.formatCellValue(cell);
	                    rownumber=rownumber+1;
	                    
	                    if(rownumber==1) {
	                    	questionModel.setQuestion(cellValue);
	                    }
	                    if(rownumber==2) {
	                    	questionModel.setQuestion_desc(cellValue);
	                    }
	                    if(rownumber==3) {
	                    	questionModel.setQuestion_type(cellValue);
	                    }
	                    if(rownumber==4) {
	                    	questionModel.setQuestion_tag(cellValue);
	                    }
	                    if(rownumber==5) {
	                    	questionModel.setQuestion_imageUrl(cellValue);
	                    }
	                    if(rownumber==6) {
	                    	questionModel.setQuestion_answers(cellValue.split(":"));
	                    }
	                }
	                
	                questionsList.add(questionModel);
	            
	            }

	            workbook.close();
	                    
	        } catch (IOException | EncryptedDocumentException | InvalidFormatException e) {
	            e.printStackTrace();
	        }
		return questionsList;
	}

}
