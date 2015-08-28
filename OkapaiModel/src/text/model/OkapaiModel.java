package text.model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class OkapaiModel {

	static String granu="Body";
	
	public static String documentPath = "/home/amee/Documents/LineCorpusFormat/WithWithoutStemmer/QTitleWithStemmer.txt";
	
	public static String wordPath="/home/amee/Documents/StackOverflowData/wordFreq"+granu+".txt";
	public static String wordNormPath="/home/amee/Documents/StackOverflowData/wordFreq"+granu+"Norm.txt";
	public static String TfPath="/home/amee/Documents/StackOverflowData/TfInfo"+granu+"ByDocument.txt";
	public static String IDFPath="/home/amee/Documents/StackOverflowData/word"+granu+"Idf.txt";
	
	public static Map<String,Integer> wordDocument = new HashMap<String,Integer>();
	
	public static Map<String,String> dupRelMap = new HashMap<String,String>();
	public static Map<String,String> relDupMap = new HashMap<String,String>();

	public static Map<String,Integer> dupList = new HashMap<String,Integer>();
	public static Map<String,Integer> relList = new HashMap<String,Integer>();
	
	public static Map<String,Integer> dupLine = new HashMap<String,Integer>();
	public static Map<String,Integer> relLine = new HashMap<String,Integer>();
	public static Map<String,String> dupRelLine = new HashMap<String,String>();
	
	
	public static Map<Integer,Posts> post = new HashMap<Integer,Posts>();
	public static  Map<Integer,Integer> pid = new HashMap<Integer,Integer>();
	public static  Map<Integer,Integer> pidrev = new HashMap<Integer,Integer>();
	
	static Map<String,Integer> dupTest = new HashMap<String,Integer>();
	
	public void loadTitle(){
		try{
			
			BufferedReader br  = null;
			br = new BufferedReader(new FileReader("/home/amee/Documents/LineCorpusFormat/Questions/Qid.txt"));
			int lineNo = 0;
			String line = "";
			while ((line = br.readLine())!=null){
				lineNo++;
				pid.put(lineNo,Integer.parseInt(line));
				pidrev.put(Integer.parseInt(line), lineNo);
				if(Integer.parseInt(line) == 21766221){
					//System.out.println("Line "+lineNo);
					//System.exit(1);
				}
			}
			br.close();
			BufferedReader br2 = new BufferedReader(new FileReader(documentPath));
			//BufferedReader br2 = new BufferedReader(new FileReader("/home/amee/Documents/LineCorpusFormat/Questions/Qtitle"));
			
			lineNo=0;
			while((line = br2.readLine())!=null){
				lineNo++;
				int d = pid.get(lineNo);
				Posts p = new Posts(null,line);
				post.put(d, p);				
			}
			System.out.println("Successfully Finished " + post.size());
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void readDupTest(){
		String csvFile = "/home/amee/Documents/StackOverflowData/Analysis Duplicate/dupTest.txt";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
	 
		try {
	 
			br = new BufferedReader(new FileReader(csvFile));
			br.readLine(); // For Column Reading.
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if(line.length()>0)
					dupTest.put(line, 1);
			}
			System.out.println("Size : " + dupTest.size());
	 
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
	 
		
		System.out.println("Done");
	}
	
	public void readCSV (){
		
		String csvFile = "/home/amee/Documents/StackOverflowData/DupList/javaDupList.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
	 
		int dupSet = 0;
		int relSet = 0;
		
		try {
	 
			br = new BufferedReader(new FileReader(csvFile));
			br.readLine(); // For Column Reading.
			while ((line = br.readLine()) != null) {
	 
			        // use comma as separator
				String[] data = line.split(cvsSplitBy);
				String dupId =  data[0].substring(1, data[0].length()-1);
				String relId = data[1].substring(1,data[1].length()-1);
			//	System.out.println(relId);
				dupRelMap.put(dupId,relId);
				relDupMap.put(relId, dupId);
				if(dupList.containsKey(dupId)){
					int v = dupList.get(dupId);
					dupList.put(dupId, v + 1 );
				}else{
					dupList.put(dupId, 1);
				}
				
				
				if(relList.containsKey(relId)){
					int v = relList.get(relId);
					relList.put(relId, v + 1 );
					//System.out.println("GOT");
				}else{
					relList.put(relId, 1);
				}
				
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
		
		int tot = 0 ;
	 
		for(Map.Entry<String, Integer> entity : relList.entrySet()){
			if(entity.getValue()>1){
				tot++;
			}
		}
		System.out.println("More than 1 sim : " +tot);
		
		tot=0;
		for(Map.Entry<String, Integer> entity : dupList.entrySet()){
			if(entity.getValue()>1){
				tot++;
			}
		}
		System.out.println("More than 1 sim : " +tot);
		try{
			int number=0;
			br = new BufferedReader(new FileReader("/home/amee/Documents/LineCorpusFormat/Questions/Qid.txt"));
			while((line=br.readLine())!= null){
				number++;
				int id = Integer.parseInt(line);
				if(dupList.containsKey(line)){
					dupLine.put(line, number);
				}
				else if (relList.containsKey(line)){
						relLine.put(line, number);	
				}
			}
			
			
			for(Map.Entry<String, String> entity : dupRelMap.entrySet()){
				String dupId = entity.getKey();
				String relId = entity.getValue();
				
				if(dupLine.containsKey(dupId)){
					if(relLine.containsKey(relId)){
						
						int dl = dupLine.get(dupId);
						int rl = relLine.get(relId);
						
						dupRelLine.put(Integer.toString(dl),Integer.toString(rl));
					}
				}
			}
			
			System.out.println("List :");
			System.out.println("MapDup : "+dupList.size() +" MapRel : "+relList.size() +" MapDupRel : " + dupRelMap.size());
			System.out.println("MapDupLine: " + dupLine.size() +" MapRelLine: "+relLine.size()+" dupRelLine: "+dupRelLine.size());;
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		System.out.println("Done");
	 
	}
	
	public OkapaiModel(){
	
		readCSV();
		loadTitle();
		readDupTest();
		DocumentProcess dp = new DocumentProcess();
		dp.createDocumentInfo();
	}
	
	public static void main ( String arg[]){
		
		OkapaiModel op = new OkapaiModel();
	}
}
