import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;




public class Main {
	
	Map<Integer,Posts> mp = new HashMap<Integer,Posts>();
	Map<Integer,Integer> qm = new HashMap<Integer,Integer>();
	static ArrayList<Integer> oq = new ArrayList<Integer>();
	static ArrayList<Integer> dq = new ArrayList<Integer>();
	
public void readCSV (){
		
		String csvFile = "/home/amee/Documents/SO/duplicate.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
	 
		try {
	 
			br = new BufferedReader(new FileReader(csvFile));
			br.readLine(); // For Column Reading.
			while ((line = br.readLine()) != null) {
	 
				String[] data = line.split(cvsSplitBy);
				String Did =  data[0].substring(1, data[0].length()-1);
				String Qid = data[1].substring(1,data[1].length()-1);
	
			//	System.out.println(Did+" " + Qid);
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
	 
			System.out.println("Done");
	 
	}

	public void readSer(){
		try {
			FileInputStream fileIn = new FileInputStream(
					"/home/amee/Documents/Question.ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);

			System.out.println("Done Reading Questioner Questions... ");
			mp = (Map<Integer,Posts>)in.readObject();
			  for(Map.Entry<Integer, Posts> q : mp.entrySet()){
			  System.out.println(q.getKey() +" " +
			  q.getValue().Id); 
			  }
			 
			int total = 0;
		// 	System.out.println("SIZE " + questionerQuestion.size());
			in.close();
			fileIn.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Main(){
		readSer();
	}
	
	public static void main ( String arg[]){
		new Main();
	}
}
