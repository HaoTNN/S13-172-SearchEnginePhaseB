package mainPackage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.jsoup.*;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

public class Indexer {	
	static void index(String docFolder, String indexPath) {
		
		// TODO check if indexPath is available (writable? already existed? etc ... )
		File indexPathFile = new File(indexPath);
		if( !indexPathFile.exists() ){
			indexPathFile.mkdir();
		}
		
		// TODO check if docFolder is available (readable? exist? etc... ) - DONE
		File docFolderFile = new File(docFolder);
		if( docFolderFile.exists() ){
			if( !docFolderFile.canRead() ){
				System.err.println("Error in index(String, String): Can't read from " + docFolder );
			}
		}
		else{
			System.err.println("Error in index(String, String): Directory " + docFolder + " doesn't exist.");
		}
		
		
		IndexWriter writer = null;
		try {			
			IndexWriterConfig indexConfig = new IndexWriterConfig(Version.LUCENE_43, 
					new StandardAnalyzer(Version.LUCENE_43));
			writer = new IndexWriter(FSDirectory.open(new File(indexPath)),
					indexConfig);
			System.out.println("Indexing to directory " + indexPath + " from " + docFolder + "...");
			indexDocs(writer, new File(docFolder));
			writer.close();
			System.out.println("Done with indexing ... ");
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (writer !=null)
				try {
					writer.close();
				} catch (CorruptIndexException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
	static void indexDocs(IndexWriter writer, File file) throws IOException {				
			if (file.canRead()) { // do not try to index files that cannot be read
				if (file.isDirectory()) {
					String[] files = file.list(); 					
					if (files != null) { // an IO error could occur
						for (int i = 0; i < files.length; i++) {
							indexDocs(writer, new File(file, files[i]));
						}
					}
				} else {
					System.out.println("adding " + file);
					try {
						writer.addDocument(getDocument(file));
					} catch (FileNotFoundException ex) {
						// at least on windows, some temporary files raise this exception 
						// with an "access denied" message,checking if the file can be read doesn't help
						ex.printStackTrace();
					}
				}
			}
		
	}
	
	//public static Document get Document(File f) throws java.io.FileNotFoundException
	//
	//Creates and return a Lucene Document containing 
	public static Document getDocument(File f) throws java.io.FileNotFoundException {
		Document doc = new Document(); //NOTE: Document is from the Lucene library, whereas the 'Document' below is from Jsoup.
		String content = "";
		
		// TODO read content from File f. -DONE
		org.jsoup.nodes.Document htmlDoc = null;
		try{
			htmlDoc = Jsoup.parse(f, "ISO-8859-1");
		}
		catch(Exception e){
			System.err.println("Error in getDocument(): " + e.getMessage());
		}
		
		// TODO get text part of the HTML file -DONE
		content = htmlDoc.text();
		
		//Converting the html files' name back to normal URLs.
		String fixedName = f.getName().replaceAll(",", "/");
		fixedName = fixedName.replaceAll("\\[", ":");
		fixedName = fixedName.replaceAll("]", "[?]");
		fixedName = fixedName.substring(0, fixedName.length()-5);
		System.out.println(fixedName);
		
		doc.add(new StringField("docid", fixedName, Field.Store.YES) );
		
		//Store.YES will store the text content of the whole web page.
		//For now, we'll leave it at YES for debugging purposes.
		//TODO: change to Store.NO when implementation is ready.
		doc.add(new TextField("content", content, Field.Store.YES) );
		
		
		// TODO need to error check to see if we get the Lucene document we want.
		
		
		
		// TODO add the fields that your ranking strategy needs.
		// TODO be careful, that you need to find back the doc to show ...
		// Maybe a field that points to location of the file?
		
		return doc;
	}
	
}
