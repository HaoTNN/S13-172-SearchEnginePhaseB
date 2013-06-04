package mainPackage;

import java.io.File;
import java.io.IOException;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Searcher {
	public static TopDocs doSearch (String indexPath, String queryString) throws ParseException, IOException {
		IndexReader indexReader = DirectoryReader.open(FSDirectory.open(new File(indexPath)) );
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		QueryParser queryparse = new QueryParser(Version.LUCENE_43, "content", new StandardAnalyzer(Version.LUCENE_43));
		// TODO pre-processing the query a little bit..		
		Query query = queryparse.parse(queryString.trim());
		System.out.println(query.toString());
		TopDocs results = indexSearcher.search(query, 50);
		// TODO here you can get the information you "stored" in inverted index
		// you need to get the whole document and generate snippets for UI ..
		indexReader.close();
		return results;
	}
}
