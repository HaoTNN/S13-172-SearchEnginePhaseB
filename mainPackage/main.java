package mainPackage;

import java.util.*;
import java.io.*;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.Directory;

public class main {
	static public void main(String[] args) throws IOException, ParseException{
		String indexPath = "testingIndexPath";
		String docFolder = "htmlsWorkFrom";
		
		//1. Creating index 
		Indexer index = new Indexer();
		index.index(docFolder, indexPath);
		
		//2. Loading and prepping the index to get data from.
		Directory indexDir = null;
		try{
			indexDir = FSDirectory.open(new File(indexPath));
		}
		catch(Exception e){
			System.err.println("Error in main: " + e.getMessage());
		}
		
		IndexReader reader = null;
		try{
			reader = DirectoryReader.open(indexDir);
		}
		catch(Exception e){
			System.err.println("Error in main: " + e.getMessage());
		}
		IndexSearcher indexSearcher = new IndexSearcher(reader);
		
		//3. Searching for query.
		String queryString = "Vagelis Hristidis";
		Searcher searcher = new Searcher();
				
		//4. Grab results and display them
		ScoreDoc[] docResults = searcher.doSearch(indexPath, queryString).scoreDocs;
		System.out.println("Found " + docResults.length + " results.");
		for( int i = 0; i < docResults.length; ++i ){
			int docId = docResults[i].doc;
			Document d = indexSearcher.doc(docId);
			System.out.println((i+1) + ": " + d.get("docid") + "\t" + docResults[i].score);
		}
	}
}
