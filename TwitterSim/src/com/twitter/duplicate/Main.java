package com.twitter.duplicate;

//import RemoveStopWord;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.OutputDocument;
import net.htmlparser.jericho.Renderer;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;

import org.tartarus.snowball.SnowballStemmer;

import com.csvreader.CsvReader;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ie.machinereading.common.StringDictionary;

public class Main {

	Map<String, ArrayList<String>> dupRelatedList = new HashMap<String, ArrayList<String>>();
	Map<String, ArrayList<posts>> dupPosts = new HashMap<String, ArrayList<posts>>();
	Map<String, posts> relPosts = new HashMap<String, posts>();

	Map<String, Integer> flag = new HashMap<String, Integer>();
	Map<String, Integer> stopList = new HashMap<String, Integer>();
	
	
	ArrayList<postFeature> featureList = new ArrayList<postFeature>();
	ArrayList<postFeature> negativeFeatureList = new ArrayList<postFeature>();
	ArrayList<postFeature> testingFeature = new ArrayList<postFeature>();
	
	
	ArrayList<String> duplicateId = new ArrayList<String>();
	ArrayList<String> allPostId = new ArrayList<String>();
	
	public static String entityModel = "/home/amee/Downloads/stanford-ner-2014-06-16/classifiers/english.conll.4class.distsim.crf.ser.gz";

	ArrayList<String> testingD = new ArrayList<String>();
	ArrayList<String> testingP = new ArrayList<String>();
	
	
	public static String serializedClassifier = entityModel;
	//System.out.println(serializedClassifier);
	public static CRFClassifier classifier = CRFClassifier
			.getClassifierNoExceptions(serializedClassifier);
	
	int tagSim[] = new int[12];
	
	private static final Set<String> DISALLOWED_HTML_TAGS = new HashSet<String>(
			Arrays.asList(HTMLElementName.PRE, HTMLElementName.CODE,
					HTMLElementName.A, HTMLElementName.LINK));

	public void readCSV(String csvFile, boolean flag) {

		// String csvFile = "/home/amee/Downloads/Dup_Related_Java.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";

		int i = 0;
		int len = 0;
		try {

			CsvReader products = new CsvReader(csvFile);
			products.readHeaders();

			while (products.readRecord()) {

				// answerCount,commentCount,favoriteCount,closedDate,ownerUserId,detecDuplicate

				String dupId = products.get("dupId");
				String postId = products.get("PostId");
				String accAnswerId = products.get("acceptedAnswerId");
				String creationDate = products.get("creationDate");
				String score = products.get("score");
				String viewCount = products.get("viewCount");
				String body = products.get("body");
				String title = products.get("title");
				String tags = products.get("tags");
				String commentCount = products.get("commentCount");
				String favoriteCount = products.get("favoriteCount");
				String closedDate = products.get("closedDate");
				String ownerUserId = products.get("ownerUserId");
				String detecDuplicate = products.get("detecDuplicate");

				posts p = new posts();
				p.setDupId(dupId);
				p.setPostId(postId);
				p.setAccAnswerId(accAnswerId);
				p.setCreationDate(creationDate);
				p.setScore(score);
				p.setViewCount(viewCount);
				p.setBody(body);
				p.setTitle(title);
				p.setTags(tags);
				p.setCommentCount(commentCount);
				p.setFavoriteCount(favoriteCount);
				p.setClosedDate(closedDate);
				p.setOwnerUserId(ownerUserId);
				p.setDetecDuplicate(detecDuplicate);

				if (flag) {

					if (!dupPosts.containsKey(dupId)) {
						ArrayList<String> match = new ArrayList<String>();
						match.add(postId);
						ArrayList<posts> list = new ArrayList<posts>();
						list.add(p);
						dupPosts.put(dupId, list);
						dupRelatedList.put(dupId, match);
						duplicateId.add(dupId);
					//	allPostId.add(dupId);
					} else {
						ArrayList<posts> list = dupPosts.get(dupId);
						list.add(p);
						ArrayList<String> match = dupRelatedList.get(dupId);
						match.add(postId);
					}

				} else {

					// System.out.println("PostID " + postId +" DupId: " +
					// dupId);

					if (!dupPosts.containsKey(postId)) {
						ArrayList<posts> list = new ArrayList<posts>();
						list.add(p);
						dupPosts.put(postId, list);
						allPostId.add(postId);	
					} else {
						//ArrayList<posts> list = dupPosts.get(postId);
						//list.add(p);
					}

				}

			}

			System.out.println(dupPosts.size());

			System.out.println("Complete Reading");
			products.close();

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

	public void ReadStopWord() {
		try {
			FileReader fl = new FileReader(
					"/home/amee/Documents/StackOverflowData/stopList.txt");
			BufferedReader br = new BufferedReader(fl);

			String line;
			while ((line = br.readLine()) != null) {
				if (line.length() > 0) {
					line = line.trim().toLowerCase();
					System.out.println(line);
					stopList.put(line, 1);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private OutputDocument removeNotAllowedTags(Source source) {

		OutputDocument outputDocument = new OutputDocument(source);
		List<Element> elements = source.getAllElements();

		for (Element element : elements) {
			// System.out.println("Element Name  " + element.getName());
			if (DISALLOWED_HTML_TAGS.contains(element.getName())) {
				/*
				 * outputDocument.remove(element.getStartTag()); if
				 * (!element.getStartTag().isSyntacticalEmptyElementTag()) {
				 * if(element.getEndTag()!= null)
				 * outputDocument.remove(element.getEndTag()); }
				 */
				outputDocument.remove(element);
			}

		}

		return outputDocument;
	}

	public String activateStemmer(String data) {

		String your_steemed_String = "";

		RemoveStopWord rm = new RemoveStopWord(stopList);
		String result = "";
		result += rm.doRemove(data);
		String lang = "english";
		Class stemClass;
		try {
			stemClass = Class.forName("org.tartarus.snowball.ext." + lang
					+ "Stemmer");

			SnowballStemmer stemmer = (SnowballStemmer) stemClass.newInstance();

			String word[] = result.split("\\s+");
			String stemmedWord = "";
			for (String w : word) {
				stemmer.setCurrent(w);
				stemmer.stem();
				String st = stemmer.getCurrent().trim();
				if (st.length() > 0)
					stemmedWord += st + " ";
			}

			your_steemed_String += stemmedWord;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return your_steemed_String;
	}

	public String htmlRemove(String body) {
		String result = "";
		Source htmlSource = new Source(body);
		List<StartTag> sTag = new ArrayList<StartTag>();
		htmlSource.getAllStartTags();
		OutputDocument outputDocument = removeNotAllowedTags(htmlSource);
		Source source = new Source(outputDocument.toString());
		Segment htmlSeg = new Segment(source, 0, outputDocument.toString()
				.length());

		Renderer htmlRend = new Renderer(htmlSeg);
		//result = activateStemmer(htmlRend.toString());
		return htmlRend.toString();
	}
	
	
	
	public void insertNegativeDataV2(){
		
		String path="/home/amee/Documents/SO/testing.arff";
		BufferedWriter bw;
		
		try{
			
			bw = new BufferedWriter(new FileWriter(path));
			
		}catch(Exception e){
			e.printStackTrace();
			
		}
		
		
		
		
		GenerateFeature gf = GenerateFeature.getGenObject();
		
		int range = 200;
		
		for( int i = 0 ; i < 100 ; i ++ ){
			
			String dId = testingD.get(i);
			
			
			if (!dupPosts.containsKey(dId)) {
				continue;
			}
			
			int v = range + 1;
			range ++ ;
			posts dPost = dupPosts.get(dId).get(0);
			
			//for ( int j = 101 ; j < 130 ; j ++ ){
				
				String sId = testingD.get(v);
				
				if (!dupPosts.containsKey(sId)) {
					continue;
				}
				
				postFeature pf = new postFeature();
				posts rPost = dupPosts.get(sId).get(0);
				
				String t1;
				String t2;

				t1 = htmlRemove(dPost.getTitle());
				t2 = htmlRemove(rPost.getTitle());
/*
				t1 = htmlRemove(dPost.getBody());
				t2 = htmlRemove(rPost.getBody());
*/					
				pf.setLavDistance(gf.getLavDistance(activateStemmer(t1),activateStemmer(t2)));
				pf.setOverLapTerm(gf.getTermOverlap(activateStemmer(t1),activateStemmer(t2)));
				
				pf.setOverlapEntity(gf.getEntityOverlap(t1, t2));
				pf.setOverlapEntityType(gf.getEntityTypeOverlap(t1, t2));
				
				pf.setOverLapTags(gf.getTagOverlap(dPost.getTags(),rPost.getTags()));
				pf.setViewDifference(gf.getDifference(dPost.getViewCount(),rPost.getViewCount()));
				pf.setScoreDifference(gf.getDifference(dPost.getScore(), rPost.getScore()));
				pf.setCommentDifference(gf.getDifference(dPost.getCommentCount(), rPost.getCommentCount()));
				pf.setDateDifference(gf.getDateDiffence(dPost.getCreationDate(),rPost.getCreationDate()));
				
				pf.setDuplicate("no");
				
				testingFeature.add(pf);
			
			//}
		}
		// Testing Yes
		
		for (int i = 201; i < 900; i++) {
			String dId = testingD.get(i);

			if (!dupPosts.containsKey(dId)) {
				continue;
			}

			posts dPost = dupPosts.get(dId).get(0);
			
			ArrayList<String> listRel = dupRelatedList.get(dId);
			int ind = 0;
			
			
			for (String relId : listRel) {

				if (!dupPosts.containsKey(relId)) {
					continue;
				}
				
				posts rPost = dupPosts.get(relId).get(0);

				postFeature pf = new postFeature();

				String t1;
				String t2;

				t1 = htmlRemove(dPost.getTitle());
				t2 = htmlRemove(rPost.getTitle());

				/*t1 = htmlRemove(dPost.getBody());
				t2 = htmlRemove(rPost.getBody());
				*/
				pf.setLavDistance(gf.getLavDistance(activateStemmer(t1),activateStemmer(t2)));
				pf.setOverLapTerm(gf.getTermOverlap(activateStemmer(t1),activateStemmer(t2)));
				
				pf.setOverlapEntity(gf.getEntityOverlap(t1, t2));
				pf.setOverlapEntityType(gf.getEntityTypeOverlap(t1, t2));
				
				pf.setOverLapTags(gf.getTagOverlap(dPost.getTags(),rPost.getTags()));
				pf.setViewDifference(gf.getDifference(dPost.getViewCount(),rPost.getViewCount()));
				pf.setScoreDifference(gf.getDifference(dPost.getScore(), rPost.getScore()));
				pf.setCommentDifference(gf.getDifference(dPost.getCommentCount(), rPost.getCommentCount()));
				pf.setDateDifference(gf.getDateDiffence(dPost.getCreationDate(),rPost.getCreationDate()));
				pf.setDuplicate("yes");
				
				
				
				//System.out.println(dPost.getCreationDate() +" " + rPost.getCreationDate() + " " +pf.getDateDifference());
				
				testingFeature.add(pf);
				//System.out.println("Working...");
			}

		}		
		
	}
	
	public void insertNegativeData(){
		
		GenerateFeature gf = GenerateFeature.getGenObject();
		Random rn = new Random();
		Map<Integer,Integer> flag = new HashMap<Integer,Integer>();
		int range = 6000;
		for(int i = 1001; i < 5990 ; i ++ ){
			
			
			//for(int j = 1 ; j < 70 ; j ++ ){
				
			//	int v = rn.nextInt(14000)+1001;
				
			int v = range + 1;
			range++;
			
				if(flag.containsKey(v)) continue;
				
				flag.put(v, 1);
				
				String dId = duplicateId.get(i);
				//String rId = duplicateId.get(v);
				String rId = allPostId.get(v);
			
				if (!dupPosts.containsKey(dId)) {
					continue;
				}
				
				posts dPost = dupPosts.get(dId).get(0);
				
				if(!dupRelatedList.get(dId).get(0).equalsIgnoreCase(rId)){
					
					
					if (!dupPosts.containsKey(rId)) {
						continue;
					}
					
					posts rPost = dupPosts.get(rId).get(0);

					postFeature pf = new postFeature();

					String t1;
					String t2;

					t1 = htmlRemove(dPost.getTitle());
					t2 = htmlRemove(rPost.getTitle());
/*
					t1 = htmlRemove(dPost.getBody());
					t2 = htmlRemove(rPost.getBody());
*/					
					pf.setLavDistance(gf.getLavDistance(activateStemmer(t1),activateStemmer(t2)));
					pf.setOverLapTerm(gf.getTermOverlap(activateStemmer(t1),activateStemmer(t2)));
					
					pf.setOverlapEntity(gf.getEntityOverlap(t1, t2));
					pf.setOverlapEntityType(gf.getEntityTypeOverlap(t1, t2));
					
					pf.setOverLapTags(gf.getTagOverlap(dPost.getTags(),rPost.getTags()));
					pf.setViewDifference(gf.getDifference(dPost.getViewCount(),rPost.getViewCount()));
					pf.setScoreDifference(gf.getDifference(dPost.getScore(), rPost.getScore()));
					pf.setCommentDifference(gf.getDifference(dPost.getCommentCount(), rPost.getCommentCount()));
					pf.setDateDifference(gf.getDateDiffence(dPost.getCreationDate(),rPost.getCreationDate()));
					pf.setDuplicate("no");
					
					
					negativeFeatureList.add(pf);

				}
			//}
		}
		
		System.out.println("Complete Inserting Negative Features");
	}
	
	boolean checkAtribute(postFeature f){
		boolean flag = false;
		int total = 0;
		if(Double.toString(f.getLavDistance()).trim().length()>0){
			total++;
		}
		if(Double.toString(f.getOverLapTags()).trim().length()>0){
			total++;
		}
		if(Double.toString(f.getOverLapTerm()).trim().length()>0){
			total++;
		}
		if(Double.toString(f.getScoreDifference()).trim().length()>0){
			total++;
		}
		if(Double.toString(f.getViewDifference()).trim().length()>0){
			total++;
		}
		if(Double.toString(f.getCommentDifference()).trim().length()>0){
			total++;
		}
		if(f.getDuplicate().length()>0){
			total++;
		}
		if(Double.toString(f.getDateDifference()).trim().length()>0){
			total++;
		}
	
		if(total == 8 ) 
			return true;
		
		
		return flag;
	}
	
	public void writeInfo( BufferedWriter bw, ArrayList<postFeature>flist){
		try{
			
			
			
			for( postFeature f : flist){
				bw.write(Double.toString(f.getLavDistance()));
				bw.write(",");
				bw.write(Double.toString(f.getOverLapTerm()));
				bw.write(",");
				bw.write(Double.toString(f.getOverLapTags()));
				bw.write(",");
				bw.write(Double.toString(f.getOverlapEntity()));
				bw.write(",");
				bw.write(Double.toString(f.getOverlapEntityType()));
				bw.write(",");
				bw.write(Double.toString(f.getViewDifference()));
				bw.write(",");
				bw.write(Double.toString(f.getScoreDifference()));
				bw.write(",");
				System.out.println(f.getDateDifference() +" **  " + Long.toString(f.getDateDifference()));
				bw.write(Double.toString(f.getCommentDifference()));
				bw.write(",");
				bw.write(Long.toString(f.getDateDifference()));
				bw.write(",");
				
				
				bw.write(f.getDuplicate());
				bw.newLine();
				//bw.write(",");
			}
			
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void writeInitial(BufferedWriter bw){
		
		try{
			
			bw.write("@relation duplicate.feature");
			bw.newLine();
			bw.newLine();
			bw.newLine();
			
			bw.write("@attribute LavDifference numeric");
			bw.newLine();
			bw.write("@attribute TermOverlap numeric");
			bw.newLine();
			bw.write("@attribute TagOverlap numeric");
			bw.newLine();
			bw.write("@attribute EntityOverlap numeric");
			bw.newLine();
			bw.write("@attribute EntityTypeOverlap numeric");
			bw.newLine();
			bw.write("@attribute viewDifference numeric");
			bw.newLine();
			bw.write("@attribute scoreDifference numeric");
			bw.newLine();
			bw.write("@attribute commentDifference numeric");
			bw.newLine();
			bw.write("@attribute dateDifference numeric");
			bw.newLine();
			bw.write("@attribute duplicate {yes,no}");
			bw.newLine();
			
			bw.newLine();
			bw.newLine();
			bw.write("@data");
			bw.newLine();
			
			
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public void writeData(){
		
		
		try{
			String path="/home/amee/Documents/SO/training.arff";
			String path2="/home/amee/Documents/SO/testing.arff";
			FileWriter fw = new FileWriter(path);
			BufferedWriter bw = new BufferedWriter(fw);
	
			BufferedWriter bw2 = new BufferedWriter(new FileWriter(path2));
			
			
	
			
			//@attribute outlook {sunny, overcast, rainy}@relation weather.symbolic
			
			
			
			
			writeInitial(bw);
			writeInitial(bw2);
			writeInfo(bw,featureList);
			writeInfo(bw,negativeFeatureList);
			writeInfo(bw2,testingFeature);	
			
			
			
			
			
			bw.close();bw2.close();
			fw.close();
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	public void generateFeature() {

		GenerateFeature gf = GenerateFeature.getGenObject();

		int total = 0;
		int l = 0;
		
		
		
		try{
			String path2="/home/amee/Documents/SO/questions";
			BufferedWriter bw2 = new BufferedWriter(new FileWriter(path2));
			
			
			
			
		for (Map.Entry<String, ArrayList<String>> m : dupRelatedList.entrySet()) {

			String dupId = m.getKey();

			if (!dupPosts.containsKey(dupId))
				continue;

			posts dPost = dupPosts.get(dupId).get(0);

			//System.out.println("T " + total);
			total++;
			if(total < 1000){
				testingD.add(dupId);
				continue;
			}else if( total < 12000){
				testingP.add(dupId);
				continue;
			}
			
			ArrayList<String> listRel = m.getValue();
			int ind = 0;
			
			
			
			bw2.write(dupId +" " + dPost.getCreationDate());
			bw2.newLine();
			
			
			
			for (String relId : listRel) {

				if (!dupPosts.containsKey(relId)) {
					continue;
				}
				
				posts rPost = dupPosts.get(relId).get(0);

				postFeature pf = new postFeature();

				String t1;
				String t2;

				t1 = htmlRemove(dPost.getTitle());
				t2 = htmlRemove(rPost.getTitle());

				/*t1 = htmlRemove(dPost.getBody());
				t2 = htmlRemove(rPost.getBody());
				*/
				pf.setLavDistance(gf.getLavDistance(activateStemmer(t1),activateStemmer(t2)));
				pf.setOverLapTerm(gf.getTermOverlap(activateStemmer(t1),activateStemmer(t2)));
				
				pf.setOverlapEntity(gf.getEntityOverlap(t1, t2));
				pf.setOverlapEntityType(gf.getEntityTypeOverlap(t1, t2));
				
				pf.setOverLapTags(gf.getTagOverlap(dPost.getTags(),rPost.getTags()));
				pf.setViewDifference(gf.getDifference(dPost.getViewCount(),rPost.getViewCount()));
				pf.setScoreDifference(gf.getDifference(dPost.getScore(), rPost.getScore()));
				pf.setCommentDifference(gf.getDifference(dPost.getCommentCount(), rPost.getCommentCount()));
				pf.setDateDifference(gf.getDateDiffence(dPost.getCreationDate(),rPost.getCreationDate()));
				pf.setDuplicate("yes");
				
				
				
				
				
				featureList.add(pf);
				
			 l++;
			}

			
			/*if (total == 18000)
				break;*/
		}
		
		bw2.close();
		
		}
		catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("Complete Positive Training");
		insertNegativeData();
		System.out.println("Complete Negative Training");
		insertNegativeDataV2();
		System.out.println("Complete Testing");
		writeData();
		
		for(int i = 0 ; i < 12 ; i ++ ){
			System.out.println(i +" " +tagSim[i]);
		}

	}

	Main() {
		String path = "/home/amee/Downloads/Dup_Related_Java.csv";
		String path2 = "/home/amee/Downloads/relatedPosts_java.csv.csv";
		readCSV(path, true);
		readCSV(path2, false);
		int total = 0;
		for (Map.Entry<String, ArrayList<String>> m : dupRelatedList.entrySet()) {
			// System.out.println(m.getKey());
			ArrayList<String> list = m.getValue();
			// System.out.println(m.getKey()+ " :");
			for (String s : list) {
				if (dupPosts.containsKey(s)) {
					// System.out.printf("# " + s+" # ");
					total++;
				}

			}
			// System.out.println();
		}
		System.out.println(total);
		
		generateFeature();
	
	}

	public static void main(String arg[]) {
		
		System.setProperty("wordnet.database.dir", "/home/amee/workspace/WordNet-3.0/dict");
		new Main();
	}

}
