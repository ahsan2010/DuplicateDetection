

//import MyNewException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.OutputDocument;
import net.htmlparser.jericho.Renderer;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;

import org.tartarus.snowball.SnowballStemmer;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class PostHandle extends DefaultHandler {

	private XMLReader xmlReader;
	static int countPost = 0;
	BufferedWriter wr;
	BufferedWriter tagWriter;
	BufferedWriter titleWriter;
	BufferedWriter bodyWriter;
	BufferedWriter info;
	BufferedWriter userData;

	BufferedReader br;
	int numOfPost = 0;
	final int MaxPost = 100;
	static private BufferedWriter out;
	
	Map<Integer, Integer> mp = new HashMap<Integer, Integer>();
	Map<Integer,Set<Integer>> ansQues = new HashMap<Integer,Set<Integer>>();
	
	boolean triggerEnd = true;
	boolean trigger = true;
	int total = 0;
	//boolean flag;
	Map<String, Integer> stopList = new HashMap<String, Integer>();
	
	
	
	int totalBody = 0;
	int totalTags = 0;
	int totalTtle = 0;
	
	//QuestAnswer qAns = new QuestAnswer();
	Map<Integer,Integer> question = new HashMap <Integer,Integer>();
	
	Map<Integer,Integer>questioners;

	Map<Integer,Posts> postData = new HashMap<Integer,Posts>();

	private static final Set<String> DISALLOWED_HTML_TAGS = new HashSet<String>(
			Arrays.asList(HTMLElementName.PRE, HTMLElementName.CODE,
					HTMLElementName.A, HTMLElementName.LINK));

	public PostHandle(XMLReader xmlReader) throws Exception {

		this.xmlReader = xmlReader;

		//Initialize();
		ReadStopWord();
		//	readVotes();
		//readUserQuestionAnswer();
		//readQuestioner();
	}
	
	public void readUserTag(){
		
	}

	public void readQuestioner(){
		try
	      {
	         FileInputStream fileIn = new FileInputStream("/home/amee/Documents/questionerList.ser");
	         ObjectInputStream in = new ObjectInputStream(fileIn);
	         
	         
	         questioners = (Map<Integer,Integer>)in.readObject();
	         System.out.println("Done Reading Questions...");
	         System.out.println("SIZE " + questioners.size());
	         in.close();
	         fileIn.close();
	      }catch(Exception e){
	    	  e.printStackTrace();
	      }
	}
	
	
	
	public void ReadDuplicateList() {
		try {

			br = new BufferedReader(new FileReader("/home/amee/Rep.txt"));
			String line = "";
			StringTokenizer token;
			boolean flag = false;
			while ((line = br.readLine()) != null) {
				token = new StringTokenizer(line, " ");
				int key = Integer.parseInt(token.nextToken());
				System.out.println(key + " " + line);
				mp.put(key, 1);
			}
			br.close();
			System.out.println("Read Finished");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void Initialize() {
		try {

			File file = new File("/home/amee/Post.txt");
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fl = new FileWriter(file.getAbsolutePath());
			wr = new BufferedWriter(fl);
			System.out.println("Start.....");

		} catch (Exception e) {
			e.printStackTrace();
		}
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
				if(st.length()>0)
					stemmedWord += st + " ";
			}

			your_steemed_String += stemmedWord;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return your_steemed_String;
	}

	private void emit(String s) throws SAXException {
		try {
			out.write(s);
			out.flush();
		} catch (IOException e) {
			throw new SAXException("I/O error", e);
		}
	}

	public void processElement(String sName, String qName, Attributes attrs)
			throws SAXException {
		{
			triggerEnd = true;
			String eName = sName; // element name
			if ("".equals(eName))
				eName = qName; // not namespace-aware
			emit("<" + eName);
			if (attrs != null) {
				System.out.println(attrs.getLength());
				for (int i = 0; i < attrs.getLength(); i++) {
					String aName = attrs.getLocalName(i); // Attr name
					if ("".equals(aName))
						aName = attrs.getQName(i);
					emit(" ");
					emit(aName + "=\"" + attrs.getValue(i) + "\"");
				}
			}
			emit(">");
		}

	}

	public String getTag ( String tempTag){
		
		if(tempTag.trim() == null ) return null;
		String allTag = tempTag.trim();
		String tags="";
		String temp="";
		for ( int i = 0 ; i < allTag.length() ; i ++ ){
			if(allTag.charAt(i)=='<'){
				temp = "";
			}
			else if( allTag.charAt(i)=='>'){
				tags+=temp+" ";
			}
			else temp+=allTag.charAt(i);
		}
		
		return tags;
		
	}	
	
	public String htmlRemove(String body){
		String result="";
		Source htmlSource = new Source(body);
		List<StartTag> sTag = new ArrayList<StartTag>();
		htmlSource.getAllStartTags();
		OutputDocument outputDocument = removeNotAllowedTags(htmlSource);
		Source source = new Source(outputDocument.toString());
		Segment htmlSeg = new Segment(source, 0, outputDocument
				.toString().length());

		Renderer htmlRend = new Renderer(htmlSeg);
		result =activateStemmer(htmlRend.toString());
		return result;
	}
	
	
	
	public void extractData(Posts p,Attributes atts) {
		
		if (atts.getValue("Body") != null) {
			p.setBody(htmlRemove(atts.getValue("Body")));

		} else
			p.setBody("");

		// Answer
		if (atts.getValue("AnserCount") != null)
			p.setAnswerCount(Integer.parseInt(atts.getValue("AnswerCount")));
		else
			p.setAnswerCount(0);
		// ViewCount
		if (atts.getValue("ViewCount") != null) {
			p.setViewCount(Integer.parseInt(atts.getValue("AnswerCount")));
		} else
			p.setViewCount(0);
		//Commment
		if(atts.getValue("CommentCount")!=null){
			p.setCommentCount(Integer.parseInt(atts.getValue("CommentCount")));
		}else{
			p.setCommentCount(-1);
		}
		
		// Closed Date
		if (atts.getValue("ClosedDate") != null) {
			p.setClosedDate(atts.getValue("ClosedDate"));
		} else
			p.setClosedDate(null);

		// Score
		if (atts.getValue("Score") != null) {
			p.setScore(Integer.parseInt(atts.getValue("Score")));
		} else
			p.setScore(0);
		// FavouriteCount
		if (atts.getValue("FavouriteCount") != null) {
			p.setViewCount(Integer.parseInt(atts.getValue("FavouriteCount")));
		} else
			p.setFavouriteCount(0);
		// OwnerUserId
		if (atts.getValue("OwnerUserId") != null) {
			p.setOwnerUserId(Integer.parseInt(atts.getValue("OwnerUserId")));
		} else
			p.setOwnerUserId(-1);
		//
		if(atts.getValue("ParentId") != null ) 	p.setParentId(Integer.parseInt(atts.getValue("ParentId")));
		else p.setParentId(-1);
		
		//
		p.setId(Integer.parseInt(atts.getValue("Id"))); // ID
		p.setPostTypeId(Integer.parseInt(atts.getValue("PostTypeId"))); // PostTypeId
		p.setTags(getTag(atts.getValue("Tags"))); // Tags
		p.setTitle(activateStemmer(atts.getValue("Title"))); // Title
		p.setCreationDate(atts.getValue("CreationDate")); // CreationDate

		if (atts.getValue("AcceptedAnswerId") != null) {
			p.setAcceptedAnswerId(Integer.parseInt(atts.getValue("AcceptedAnswerId")));
		} else
			p.setAcceptedAnswerId(-1);
	}
	
	
	
	
		
	

	
	
	
	
	
	
	public void ReadPost(String sName,String qName, Attributes atts,boolean pf) throws SAXException {
		try{
		if (qName.equalsIgnoreCase("row")) {
			
			int postTypeId = Integer.parseInt(atts.getValue("PostTypeId"));
			int id = Integer.parseInt(atts.getValue("Id"));
		
			if (postTypeId == 1 && (Parser.dq.contains(id) || Parser.oq.contains(id))) {
								
								
				Posts p = new Posts();
				p.setId(id);
				p.setPostTypeId(postTypeId);
				if(atts.getValue("AcceptedAnswerId")!=null)p.setAcceptedAnswerId(Integer.parseInt(atts.getValue("AcceptedAnswerId")));
				else p.setAcceptedAnswerId(-1);
				p.setCreationDate(atts.getValue("CreationDate"));
				p.setBody(atts.getValue("Body"));
				p.setTags(atts.getValue("Tags"));
				if(atts.getValue("AnswerCount")!=null)p.setAnswerCount(Integer.parseInt(atts.getValue("AnswerCount")));
				if(atts.getValue("CommentCount")!=null)p.setCommentCount(Integer.parseInt(atts.getValue("CommentCount")));
				if(atts.getValue("ViewCount")!=null)p.setViewCount(Integer.parseInt(atts.getValue("ViewCount")));
				if(atts.getValue("Score")!=null)p.setScore(Integer.parseInt(atts.getValue("Score")));
				if(atts.getValue("FavoriteCount")!=null)p.setFavouriteCount(Integer.parseInt(atts.getValue("FavoriteCount")));
				p.setClosedDate(atts.getValue("ClosedDate"));
				if(atts.getValue("OwnerUserId") != null)p.setOwnerUserId(Integer.parseInt(atts.getValue("OwnerUserId")));
				else p.setOwnerUserId(-1);
				postData.put(p.getId(),p);
				
								
				
				
			}

		}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	

	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		try{
		FileOutputStream fileOut = new FileOutputStream("/home/amee/Documents/Question.ser");
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(postData);
        out.close();
        fileOut.close();
        System.exit(0);
		}catch(Exception e){
			e.printStackTrace();
		}
		super.endDocument();
	}

	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {

		//ReadAnswer(localName, qName, atts);
		ReadPost(localName,qName, atts,false); // true -> create the questioner List
		//readAns(localName,qName, atts);      // false -> gather the question..
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
	
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {

	}

}
