package text.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;

public class DocumentProcess {

	/*
	 * We are using Line Corpus Formating
	 * 
	 */
	
	BufferedReader documentReader;
	BufferedWriter wordWriter;
	BufferedWriter tfInfoWriter;
	BufferedWriter wordNormWriter;
	BufferedWriter wordIdfWriter;
	
	int totalDocument;
	int totalWord;
	
	Map<Integer,Features> featDoc = new HashMap<Integer,Features>();
	Map<String,Double> idfWord = new HashMap<String,Double>();
	
	public DocumentProcess(){
		intialization();		
	}
	/*
	 * Initialize all the class variable
	 */
	public void intialization(){
		try{
			
			documentReader = new BufferedReader(new FileReader(OkapaiModel.documentPath));
			wordWriter = new BufferedWriter(new FileWriter(OkapaiModel.wordPath));
			tfInfoWriter = new BufferedWriter(new FileWriter(OkapaiModel.TfPath));
			wordNormWriter = new BufferedWriter(new FileWriter(OkapaiModel.wordNormPath));
			wordIdfWriter = new BufferedWriter(new FileWriter(OkapaiModel.IDFPath));
			
		}catch( Exception e ){
			e.printStackTrace();
		}
		
	}
	
	/*
	 * get the per document word Frequency considering each document
	 * also update the total number of word and word Frequency consider total Document 	
	 */
	
	public Map<String,Integer> getWordFrequency(String document){
		Map <String,Integer> wordFreq = new HashMap<String,Integer>();
		
		StringTokenizer tokenizer = new StringTokenizer(document, " ,;.?!\"'()[]{}:*&@#$%/\\+~^|0123456789<>");
		while(tokenizer.hasMoreElements()){
			String word = tokenizer.nextToken().trim().toLowerCase();
			if(word.length()>0){
				if(wordFreq.containsKey(word)){
					int v = wordFreq.get(word);
					wordFreq.put(word, v + 1);
				}else{
					wordFreq.put(word, 1);
					Map<String,Integer> wordDocument = OkapaiModel.wordDocument;
					if(wordDocument.containsKey(word)){
						int v = wordDocument.get(word);
						wordDocument.put(word, v + 1 );
					}else{
						wordDocument.put(word, 1);
					}
				}
			}
		}
		
		
		return wordFreq;
	}
	
	/*
	 * Count total word in each Document 
	 */
	
	public int countTotalWordDocument( Map<String,Integer> wordFreq){
		int totalWord = 0;
		
		for( Map.Entry<String, Integer> entry : wordFreq.entrySet()){
			totalWord += entry.getValue();
		}
		
		return totalWord;
	}
	
	/*
	 * get the Normalizing Frequency Value
	 */
	
	public Map<String,Double> getNormilazingWordFreq( Map <String,Integer> wordFreq, int totalWord){
		Map<String,Double> norm = new HashMap<String,Double>();
		
		for( Map.Entry<String, Integer> entry : wordFreq.entrySet()){
			double normValue = (double)entry.getValue()/totalWord;
			norm.put(entry.getKey(), normValue);
		}
		
		return norm;
	}
	
	
	/*
	 * Calculate the Idf for each word
	 */
	
	public double getIdf(String word){
		
		Map<String,Integer> wordDocument  = OkapaiModel.wordDocument;
		
		double idf = 0;
		
		idf = 1 + Math.log(totalDocument/wordDocument.get(word));
		
		return idf;
	}
	
	public void genIdf(){
		Map<String,Integer> wordDocument  = OkapaiModel.wordDocument;
		
		
		for(Map.Entry<String, Integer> entity : wordDocument.entrySet() ){
			String word = entity.getKey();
			double idf = 0;
			//System.out.println(featDoc.size() +" " + wordDocument.get(word));
			idf = 1 + Math.log(featDoc.size()/wordDocument.get(word));
			//System.out.println("IDF "+idf);
			idfWord.put(word, idf);
		}
	}

	
	
	/*
	 * Writing the data value in the file
	 * 
	 */
	
	public void writeResult( Map<String,Integer> wordFreq)throws Exception{
		
		wordWriter.write(Integer.toString(wordFreq.size()));
		//System.out.println(wordFreq.size());
		
		if( wordFreq.size() > 0 ){
			
			for( Map.Entry<String, Integer> entry : wordFreq.entrySet()){
				wordWriter.write(" " +entry.getKey()+" "+entry.getValue());
				//System.out.println(entry.getKey() +" " + entry.getValue());
			}
		}	
		wordWriter.write("\n");
		
	}
	
	/*
	 * Writing the Normalized data value in the file
	 * 
	 */
	
	
	public void writeWordNormResult(Map<String,Double> wordNormFreq) throws Exception {
		
		wordNormWriter.write(Integer.toString(wordNormFreq.size()));
		//System.out.println(wordNormFreq.size());
		
		if( wordNormFreq.size() > 0 ){
			
			for( Map.Entry<String, Double> entry : wordNormFreq.entrySet()){
				wordNormWriter.write(" " +entry.getKey()+" "+entry.getValue());
				//System.out.println(entry.getKey() +" " + entry.getValue());
			}
		}	
		wordNormWriter.write("\n");
		
	}

	/*
	 * Writing the per word Idf value in the file
	 * 
	 */
	
	public void writeWordIdf() throws Exception{
		Map<String,Integer> wordDocument = OkapaiModel.wordDocument;
		wordIdfWriter.write(wordDocument.size()+" "+totalDocument+"\n");
		for(Map.Entry<String, Integer> entry : wordDocument.entrySet() ){
			wordIdfWriter.write(entry.getKey() +" " + getIdf(entry.getKey())+"\n");
		}
	}
	
	/*
	 * Closing the writers
	 */
	
	public void closeWriter() throws Exception {
		wordWriter.close();
		wordNormWriter.close();
		wordIdfWriter.close(); 
	}
	
	
	
	public double getSim(Features query, Features doc){
		double score = 0 ;
		
		String title1 = OkapaiModel.post.get(query.id).title;
		String title2 = OkapaiModel.post.get(doc.id).title;
		
		AbstractStringMetric metric = new Levenshtein();
		score =  metric.getSimilarity(title1, title2);

		
		return score;
	}
	
	public double getScore(Features query, Features doc){
		double score = 0;
		
		double up = 0;
		double downQ = 0;
		double downDoc=0;
		
		
		
		for(Map.Entry<String, Double > entity : query.wordFreq.entrySet()){
			//System.out.println(entity.getKey());
			//System.out.println("Word " + doc.wordFreq.size());
			
			/*if(doc.id == 21766221){
				System.out.println("Key " + entity.getKey());
				for(Map.Entry<String, Double> t : doc.wordFreq.entrySet()){
					System.out.printf(t.getKey()+",");
				}
				System.out.println();
				
			}
	
*/		
			if(doc.wordFreq.containsKey(entity.getKey())){
				
				//System.out.println("GOT ");
				double idf = idfWord.containsKey(entity.getKey()) ? idfWord.get(entity.getKey()):0;
				
				double v = (entity.getValue()*idf) * (doc.wordFreq.get(entity.getKey())*idf);
				up += v;
				downQ += (entity.getValue()*idf) * (entity.getValue()*idf);
				downDoc += (doc.wordFreq.get(entity.getKey())*idf) * (doc.wordFreq.get(entity.getKey())*idf);
			}
		}
		
		//System.out.println("SCORE " + score);
		
		downQ = Math.sqrt(downQ);
		downDoc = Math.sqrt(downDoc);
		if(downQ == 0 || downDoc == 0) score = 0;
			else score = up/(downQ*downDoc);
		//System.out.println("SCORE " + score);
		if(score!=0){
			System.out.println(up +" " + downQ +" " + downDoc +" " + score);
			System.out.println(OkapaiModel.post.get(query.id).title);
			System.out.println(OkapaiModel.post.get(doc.id).title);
		}
		
		return score;
	}
	
	
	public void singleTest(){
		
		int relId = 2149785;
		
		String title = OkapaiModel.post.get(relId).title;
		Map<String,Integer> wordFreq = getWordFrequency(title);
		int totalWord = countTotalWordDocument(wordFreq);
		Map<String,Double> wordNormFreq = getNormilazingWordFreq(wordFreq, totalWord);
	
		ArrayList<PostSim> ps = new ArrayList<PostSim>();
		Features f = new Features();
		f.lineNo = OkapaiModel.pidrev.get(relId);
		f.id = relId;
		f.wordFreq = wordNormFreq;
		
		for(Map.Entry<Integer, Features> en : featDoc.entrySet()){
			//Double score = getSim(f,en.getValue());
			Double score = getScore(f,en.getValue());
			/*if(en.getValue().id == 21766221){
				System.out.println("GOT>>> " + score);
			}*/
			PostSim s = new PostSim(en.getKey(),en.getValue().lineNo,score);
			ps.add(s);
			
		}
		Collections.sort(ps,new Comparator<PostSim>(){
			public int compare(PostSim p1 , PostSim p2){
				return Double.compare(p2.score,p1.score);
			}
		});
		
		System.out.println("LIST :");
		
		for(int i = 0 ; i < 10 ; i ++ ){
			//System.out.println(ps.get(i).id+" " + ps.get(i).score + " "+ps.get(i).lineNo);
			System.out.println(ps.get(i).id);
		}
	
	}
	
	public void test(){
		
		Map<String,String> relDupMap = OkapaiModel.relDupMap;
		int total = 0;
		int l = 0;
		for(Map.Entry<String, String> entity : relDupMap.entrySet()){
			
			int relId = Integer.parseInt(entity.getKey());
			
			if(!OkapaiModel.post.containsKey(relId)) 
				continue;
			
			if(!OkapaiModel.post.containsKey(Integer.parseInt(relDupMap.get(Integer.toString(relId)))))
				continue;
			l ++ ;
			String title = OkapaiModel.post.get(relId).title;
			
			//System.out.println("T1 : " + title +" " +relId +" "+relDupMap.get(Integer.toString(relId)));
			//System.out.println("T2 "+OkapaiModel.post.get(Integer.parseInt(relDupMap.get(Integer.toString(relId)))).title);
			
			Map<String,Integer> wordFreq = getWordFrequency(title);
			int totalWord = countTotalWordDocument(wordFreq);
			Map<String,Double> wordNormFreq = getNormilazingWordFreq(wordFreq, totalWord);
			
			ArrayList<PostSim> ps = new ArrayList<PostSim>();
			Features f = new Features();
			f.lineNo = -1;
			f.id = relId;
			f.wordFreq = wordNormFreq;
			
			for(Map.Entry<Integer, Features> en : featDoc.entrySet()){
				
				Double score = getScore(f,en.getValue());
				/*if(en.getValue().id == 21766221){
					System.out.println("GOT>>> " + score);
				}*/
				PostSim s = new PostSim(en.getKey(),en.getValue().lineNo,score);
				ps.add(s);
				
			}
			Collections.sort(ps,new Comparator<PostSim>(){
				public int compare(PostSim p1 , PostSim p2){
					return Double.compare(p2.score,p1.score);
				}
			});
		/*	for(PostSim p : ps){
				System.out.println(p.id +" " + p.score);
			}*/
			//total= 0 ;
			String dupId = OkapaiModel.relDupMap.get(Integer.toString(relId));
			
			if(dupId == null )continue;
			//System.out.println("GOT");
			for(int i = 0 ; i < 10 ; i++ ){
				
				int id = ps.get(i).id;
			//	System.out.println(id);
				if( id == Integer.parseInt(dupId)){
					total++;
				}
			}
			
			
		//	System.out.println("Testing: " + total);
			//if(l>2) break;
			//break;
		}
		
		
		System.out.println("Total : " + l +"  Success(top 1000): " + total +" Rate: "+ ((double)total/l));
	}
	
	/*
	 * Main Function generating the Document Info
	 */
	
	public void createDocumentInfo(){
		String lineDoc;
		int doc = 0 ;
		totalDocument = 0;
		int id;
		try{
			int lineNo = 0;
			while((lineDoc = documentReader.readLine()) != null){
				lineNo++;
				if(OkapaiModel.pid.containsKey(lineNo))
					id = OkapaiModel.pid.get(lineNo);
				else continue;
				//if(OkapaiModel.relList.containsKey(lineNo))continue;
				
				if(OkapaiModel.dupTest.containsKey(Integer.toString(id)) || id%9 == 0){
						
				
				System.out.println(lineNo);
				
				
				//if(OkapaiModel.dupList.containsKey(Integer.toString(id))){
				
				Map<String,Integer> wordFreq = getWordFrequency(lineDoc);
				int totalWord = countTotalWordDocument(wordFreq);
				Map<String,Double> wordNormFreq = getNormilazingWordFreq(wordFreq, totalWord);
				
				
			/*	if(lineNo == 562905){
					System.out.println(lineDoc);
					System.exit(1);
					break;
				}
				*/
				int docId=OkapaiModel.pid.get(lineNo);
				
				
					
				Features f = new Features();
				f.lineNo = lineNo;
				f.id = docId;
				f.wordFreq = wordNormFreq;
				featDoc.put(docId, f);
				
				//writeResult(wordFreq);
				//writeWordNormResult(wordNormFreq);
				totalDocument++;
				
				//if(totalDocument>150000)break;
			//	}
				}
			}
				System.out.println("Total Document " + totalDocument);
			genIdf();
			totalWord = OkapaiModel.wordDocument.size();
			
			genIdf();
			//test();
			
			System.out.println("Finished IDF, TF");
			
			singleTest();
			
			//writeWordIdf();
			//closeWriter();
			
			//System.out.println("Successfully Finished Writing Document");
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
}
