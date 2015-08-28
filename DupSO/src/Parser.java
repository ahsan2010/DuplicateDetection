
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;


public class Parser {
	
	
	static ArrayList<String> word ;
	static String path = "/home/amee/Documents/StackOverflowData/home/users/ponza/stack_overflow_201208/";
	static String path2 = "/media/New Volume/MyResearch/MSR2015/Minning Challange/SOdata/";
	
	public static  Connection connect = null;
	public static Statement statement = null; 
	
	public static ArrayList <Integer> users = new ArrayList < Integer > ();
	public static HashMap < Integer,Integer> infoUser = new HashMap < Integer,Integer>();
	public static HashMap < Integer, Set<String>> topic = new HashMap < Integer, Set<String>>();
	public static HashMap <Integer,Set<String>> tag = new HashMap <Integer,Set<String>>();
	//public static HashMap <Integer,User> userAnswerQuestion = new HashMap < Integer,User>();

	//public static HashMap < Integer,Integer> infoUser = new HashMap < Integer,UserQuestion>();
	
	Map<Integer,Integer> qm = new HashMap<Integer,Integer>();
	static ArrayList<Integer> oq = new ArrayList<Integer>();
	static ArrayList<Integer> dq = new ArrayList<Integer>();
	
	public Parser(){
		
	}
	
	
	
	public void readCSV (){
		
		String csvFile = "/home/amee/Documents/SO/duplicate.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
	 
		try {
	 
			br = new BufferedReader(new FileReader(csvFile));
			br.readLine(); // For Column Reading.
			while ((line = br.readLine()) != null) {
	 
			        // use comma as separator
				String[] data = line.split(cvsSplitBy);
				String Did =  data[0].substring(1, data[0].length()-1);
				String Qid = data[1].substring(1,data[1].length()-1);
				//users.add(Integer.parseInt(id));
				System.out.println(Did+" " + Qid);
				qm.put(Integer.parseInt(Did), Integer.parseInt(Did));
				oq.add(Integer.parseInt(Qid));
				dq.add(Integer.parseInt(Did));
			}
	 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	 
		for ( int i : users ){
			System.out.println(i);
		}
		
		System.out.println("Done");
	 
	}
	
	
	
	public void connect(String database){
		  
		try{
			  
			  String url = "jdbc:mysql://localhost:3306/"+database;
			  Class.forName("com.mysql.jdbc.Driver");
		     
		      connect = DriverManager
		    			.getConnection(url,"root", "root");
		      System.out.println("Connect Successfully");
		     
		      statement = connect.createStatement();
		      

		  }catch(Exception e ){
			  System.out.println("Problem in COnnecting Database");
			  e.printStackTrace();
		  }
		
	  }
	  
	
	
	public void ParseXml () throws Exception {
				
				
				readCSV();
				word = new ArrayList<String>();
				
		        SAXParserFactory spf = SAXParserFactory.newInstance();
		        SAXParser sp = spf.newSAXParser();
		        XMLReader xr = sp.getXMLReader();
		      
		        xr.setContentHandler(new PostHandle(xr));
		        try {
		        	xr.parse("/home/amee/Documents/MSR2015/Posts.xml");
		        } catch (SAXException e) {
		            e.printStackTrace();
		        }
		        
		      
	}
	
	
	public void createFeatureVector(){
		try{
		//ConstructFeature cf = new ConstructFeature(2);
		//ParseXml();
		readCSV();
		ParseXml();
		
		}catch (  Exception e ){
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		
			//new Parser().readUserAnswerQuestionFile();
			// new Parser().readUserInfo();
			//new Parser().ParseXml();
        	//new Parser().connect("stackoverflow");
		   	new Parser().createFeatureVector();
			//new ConstructFeature().readQuestionerQuestion();
			
	}

}
