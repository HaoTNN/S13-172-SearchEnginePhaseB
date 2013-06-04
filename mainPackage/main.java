package mainPackage;

import java.util.*;
import java.io.*;
import org.jsoup.*;
import org.jsoup.examples.*;
import org.jsoup.nodes.*;
import org.jsoup.safety.*;
import org.jsoup.select.*;

public class main {
	static public void main(String[] args){
		String indexPath = "./testingIndexPath";
		
		File htmlFile = new File("htmlsWorkFrom/http[,,www.cs.ucr.edu,~vagelis.html");
		
		//Code block to get cleaned text from Document ( html)
		/*
		File htmlFile = new File("htmlsWorkFrom/http[,,www.cs.ucr.edu,~vagelis.html");
		
		
		if( !htmlFile.exists()){
			System.err.println("Something went wrong: htmlFile doesn't exist.");
		}	
		String charSet = "ISO-8859-1";
		ArrayList<String> docText = new ArrayList<String>();
		String innerHtml = "";
		Document doc = null;
		try{
			doc = Jsoup.parse(htmlFile,charSet);
		}
		catch(Exception e){
			System.err.println("Error in parsing: " + e.getMessage());
		}
		
		System.out.println(doc.text());
		System.out.println(doc.text().length());
		*/
		
		/*
		HtmlToPlainText ht = new HtmlToPlainText();
		innerHtml = ht.getPlainText(doc);
		*/
		
		//doc = new Cleaner(Whitelist.simpleText()).clean(doc);
		//innerHtml = Jsoup.parse( doc.body().toString().replaceAll("(?i)<br[^>]*>", "br2n") ).text();
		//innerHtml = innerHtml.replaceAll("br2n", "\n");
		
		
		//String docFolder;
	}
}
