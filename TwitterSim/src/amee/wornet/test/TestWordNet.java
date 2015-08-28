package amee.wornet.test;

import java.util.Map.Entry;
import java.util.TreeMap;

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.sussex.nlp.jws.JWS;
import edu.sussex.nlp.jws.Resnik;

public class TestWordNet {

	
	public void jasw(){
		System.setProperty("wordnet.database.dir", "/home/amee/workspace/WordNet-3.0/dict");
		 
		 NounSynset nounSynset;
		 NounSynset[] hyponyms;

		 WordNetDatabase database = WordNetDatabase.getFileInstance();
		 Synset[] synsets = database.getSynsets("fly");
		 for (int i = 0; i < synsets.length; i++) {
		     System.out.println(synsets[i].toString());
		 } 
		 
	}
	
	public TestWordNet(){
		jasw();
	}
	
	public void similarityTest(){
		System.setProperty("wordnet.database.dir", "/home/amee/workspace/WordNet-3.0/dict");
		
		JWS ws = new JWS("/home/amee/workspace/WordNet", "3.0");
		
		//System.out.println("GOT..");
		Resnik res = ws.getResnik();
		
		TreeMap<String, Double> scores1 = res.res("kill","die", "v");
		System.out.println("GOT " + scores1.size());
		for(Entry<String, Double> e: scores1.entrySet())
		    System.out.println(e.getKey() + "\t" + e.getValue());
	}
	
	///home/amee/workspace/WordNet-3.0/dict
	public static void main ( String arg[])  {
		
		new TestWordNet();
	}
}
