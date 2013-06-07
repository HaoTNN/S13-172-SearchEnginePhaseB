package mainPackage;

import java.io.File;
import java.io.IOException;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.CachingTokenFilter;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.util.Version;

public class Searcher {
	public static TopDocs doSearch (String indexPath, String queryString) throws ParseException, IOException {
		//Multi Field Querying - MultiFieldQueryParser
		IndexReader indexReader = DirectoryReader.open(FSDirectory.open(new File(indexPath)) );
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		MultiFieldQueryParser queryparse = new MultiFieldQueryParser(Version.LUCENE_43, new String[]{"title","header","content"}, new StandardAnalyzer(Version.LUCENE_43));
		// TODO pre-processing the query a little bit..		
		Query query = queryparse.parse(queryString.trim());
		System.out.println(query.toString());
		TopDocs results = indexSearcher.search(query, 50);
		// TODO here you can get the information you "stored" in inverted index
		// you need to get the whole document and generate snippets for UI ..
		indexReader.close();
		return results;
	}
	
	public static String[] getFragments(String queryString, String fieldName, String fieldContents,
									int fragNum, int fragSize) throws IOException, InvalidTokenOffsetsException, ParseException{
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_43);
		MultiFieldQueryParser queryparse = new MultiFieldQueryParser(Version.LUCENE_43, new String[]{"title","header","content"}, analyzer);
		Query query = queryparse.parse(queryString.trim());
		QueryScorer scorer = new QueryScorer(query, fieldName, fieldContents);
		Fragmenter fragmenter = new SimpleSpanFragmenter(scorer, fragSize);
		Highlighter highlighter = new Highlighter(scorer);
		
		highlighter.setTextFragmenter(fragmenter);
		highlighter.setMaxDocCharsToAnalyze(Integer.MAX_VALUE);
		
		String[] fragments = highlighter.getBestFragments(analyzer, fieldName, fieldContents, fragNum);
		return fragments;
	}
}
