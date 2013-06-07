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
import org.apache.lucene.util.Version;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;

public class main {
	static public void main(String[] args) throws IOException, ParseException, InvalidTokenOffsetsException{
		String indexPath = "testingIndexPath";
		String docFolder = "htmlsToWorkFrom";
		
		//1. Creating index 
		//Indexer.index(docFolder, indexPath);
		
		//2. Loading and prepping the index to get data from.
		Directory indexDir = FSDirectory.open(new File(indexPath));
		
		IndexReader reader = DirectoryReader.open(indexDir);
		IndexSearcher indexSearcher = new IndexSearcher(reader);
		
		//3. Searching for query.
		String queryString = "sean cheng education";
				
		//4. Grab results and display them
		ScoreDoc[] docResults = Searcher.doSearch(indexPath, queryString).scoreDocs;
		System.out.println("Found " + docResults.length + " results.");
		for( int i = 0; i < docResults.length; ++i ){
			int docId = docResults[i].doc;
			Document d = indexSearcher.doc(docId);
			String[] fragments = Searcher.getFragments(queryString, "content", d.get("content"), 2, 75);
			
			System.out.println((i+1) + ": " + d.get("docid") + "\t" + docResults[i].score);
			
			for( String s : fragments ){
				System.out.print(s+" ");
			}
			System.out.println("");
		}
	}
}
