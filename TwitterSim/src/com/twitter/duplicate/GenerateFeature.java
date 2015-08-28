package com.twitter.duplicate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;
import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;

public class GenerateFeature {

	
	static private GenerateFeature gf = new GenerateFeature();
	
	private GenerateFeature() {
	
	
	
	}
	
	
	public double getLavDistance( String p1, String p2){
		double sim=0;
		
		AbstractStringMetric metric = new Levenshtein();
		sim = metric.getSimilarity(p1,p2);
		
	//	System.out.println(p1 );
//		System.out.println(p2);
//		System.out.println(sim);
		
		return sim;
	}
	
	public double getTermOverlap( String p1, String p2){
		double overlap = 0;
		
		Map<String,Integer> m1 = new HashMap<String,Integer>();
		Map<String,Integer> m2 = new HashMap<String,Integer>();
		Map<String,Integer> m3 = new HashMap<String,Integer>();
		//System.out.println(p1 +"\n" + p2);
		
		StringTokenizer st = new StringTokenizer(p1," \t");
		while(st.hasMoreTokens()){
			
			String token = st.nextToken().toLowerCase();
			if(token.trim().length() > 0 ){
				if(!m1.containsKey(token))
					m1.put(token,1);
			}
			
		}
		
		
		st = new StringTokenizer(p2," \t[]");
		
		while(st.hasMoreTokens()){
			String token = st.nextToken().trim().toLowerCase();
			if(token.length() > 0 ){
				
				if(!m1.containsKey(token)){
					m2.put(token, 1);
				
				}else{
					if(!m3.containsKey(token)){
						overlap ++ ;
						m3.put(token, 1);
					}					
				 
						
				}
			}
		}
		
		/*System.out.println(p1 +" " + overlap);
		System.out.println(p2);
		System.out.println("Term " + overlap +" "+(m1.size()+m2.size()));
		*/
		overlap = overlap/(double)(m1.size() + m2.size());
		return overlap;
	}

	public double getTagOverlap( String p1, String p2){
		double overlap = 0;
		
		Map<String,Integer> m1 = new HashMap<String,Integer>();
		Map<String,Integer> m2 = new HashMap<String,Integer>();
		Map<String,Integer> m3 = new HashMap<String,Integer>();
		
		StringTokenizer st = new StringTokenizer(p1," < >");
		while(st.hasMoreTokens()){
			
			String token = st.nextToken().toLowerCase();
			if(token.trim().length() > 0 ){
				if(!m1.containsKey(token))
					m1.put(token,1);
		
			}
			
		}
		
		
		st = new StringTokenizer(p2," < >");
		
		while(st.hasMoreTokens()){
			String token = st.nextToken().trim().toLowerCase();
			if(token.length() > 0 ){
				
				
				if(!m1.containsKey(token)){
					m2.put(token, 1);

				}else{
					
					if(!m3.containsKey(token)){
						overlap ++ ;
						 m3.put(token, 1);
					}					
				
										
				}
			}
		}
		
		
		overlap = overlap/(double)(m1.size() + m2.size());
		
		return overlap;
	}

	
	
	public double getDifference(String v1, String v2){
		
		if(v1 == null && v2 ==  null) return 0;
		if(v1 == null){
			return (double)Integer.parseInt(v2);
		}
		
		else if(v2 == null){
			return (double)Integer.parseInt(v1);
		}
		
		
		double dif = (double)Math.abs(Integer.parseInt(v1) - Integer.parseInt(v2));
		
		return dif;
	}
	
	
	
	public long getDateDiffence(String d1, String d2){
		long res = 0 ;
		
		if(d1.trim().length() == 0 || d2.trim().length()==0) return 0;
		
	//	System.out.println(d1 + " " + d1.length());
	//	System.out.println(d2 +" "+d2.length());
		
		DateTimeFormatter dft = DateTimeFormat.forPattern("yyyy-mm-dd HH:mm:ss");
		DateTime date1 = dft.parseDateTime(d1);
		DateTime date2 = dft.parseDateTime(d2);
		
	//	System.out.println(date1.toString());
	//	System.out.println(date2.toString());
		
		
		
		 res = Math.abs(date2.getMillis() - date1.getMillis())/3660;
		
		// res = Minutes.minutesBetween(date1, date2).getMinutes()/60;
		
		System.out.println(res);
		 
		 
		return res;
	}
	
	static GenerateFeature getGenObject(){
		return gf;
	}
	
	
	public static Set<String> entityFind(String text) {
		
		Set<String> ent = new HashSet<String>();
		
		CRFClassifier classifier = Main.classifier;
		List<List<CoreLabel>> classify = classifier.classify(text);
		
		for (List<CoreLabel> coreLabels : classify) {
			for (CoreLabel coreLabel : coreLabels) {

				String word = coreLabel.word();
				String category = coreLabel
						.get(CoreAnnotations.AnswerAnnotation.class);
				if (!"O".equals(category)) {
					ent.add(word);
					
				}

			}

		}
		return ent;
	}
	
	public static Set<String> entityTypeFind(String text) {
		
		Set<String> ent = new HashSet<String>();
		
		CRFClassifier classifier = Main.classifier;
		List<List<CoreLabel>> classify = classifier.classify(text);
		
		for (List<CoreLabel> coreLabels : classify) {
			for (CoreLabel coreLabel : coreLabels) {

				String word = coreLabel.word();
				String category = coreLabel
						.get(CoreAnnotations.AnswerAnnotation.class);
				ent.add(category);

			}

		}
		return ent;
	}
	
	public double getEntityOverlap( String p1, String p2){
		double overlap = 0;
		
		Set<String> ent1 = entityFind(p1);
		Set<String> ent2 = entityFind(p2);
		
		Set <String> interSect = new HashSet<String>(ent1);
		Set <String> union = new HashSet<String>(ent1);
		
		interSect.retainAll(ent2);
		union.addAll(ent2);
		
		overlap = (double)(interSect.size())/(union.size());
		
		
		
		return overlap;
	}

	public double getEntityTypeOverlap( String p1, String p2){
		
		double overlap = 0;
		
		Set<String> ent1 = entityTypeFind(p1);
		Set<String> ent2 = entityTypeFind(p2);
		
		Set <String> interSect = new HashSet<String>(ent1);
		Set <String> union = new HashSet<String>(ent1);
		
		interSect.retainAll(ent2);
		union.addAll(ent2);
		//System.out.println(p1);
		
		
		overlap = (double)(interSect.size())/(union.size());
		
		System.out.println("U " + overlap + " " + ent1.size() +" " + ent2.size());
		
		return overlap;
	}
	
	public void jasw(){
		
		 
		 NounSynset nounSynset;
		 NounSynset[] hyponyms;
		 
		 Synset []sn;

		 WordNetDatabase database = WordNetDatabase.getFileInstance();
		 
		 Synset[] synsets = database.getSynsets("fly");
		 
		 for (int i = 0; i < synsets.length; i++) {
		     System.out.println(synsets[i].toString());
		 } 
		 
	}
	
	
}
