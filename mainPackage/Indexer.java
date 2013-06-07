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
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

import org.apache.commons.io.FileUtils;

import java.util.Scanner;

public class Indexer {	
	static void index(String docFolder, String indexPath) {
		
		// TODO check if indexPath is available (writable? already existed? etc ... )
		File indexPathFile = new File(indexPath);
		if( indexPathFile.exists() ){
			System.out.println("Index folder already exists. There may be outdated indexes. Rebuild indexes? (y/n)");
			Scanner userIn = new Scanner(System.in);
			if( userIn.nextLine().contentEquals("n") ){
				return;
			}
			try{
				FileUtils.deleteDirectory(indexPathFile);
			}
			catch(Exception e){
				System.err.println("Error in index(String, String): " + e.getMessage());
			}
		}

		
		System.out.println("Creating directory for index...");
		indexPathFile.mkdir();
		
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
			indexConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
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

	//Creates and returns a Document with the fields 'docid', 'title', 'header', and 'content',
	//where:
	//docid - the URL of the Document
	//title - title acquired from the <title> element on the page
	//header - headers acquired from <h1>,<h2>,...,<h6> elements
	//content - content acquired from <p> elements
	public static Document getDocument(File f) throws java.io.FileNotFoundException, IOException {
		Document doc = new Document();
		String content = "";
		String title = "";
		String headers = "";
		
		org.jsoup.nodes.Document htmlDoc = Jsoup.parse(f, "ISO-8859-1");

		content = htmlDoc.select("p").text();
		title = htmlDoc.select("title").text();
		headers = htmlDoc.select("h0, h1, h2, h3, h4, h5, h6").text();
		
		//Converting the html files' name back to normal URLs.
		String fixedName = f.getName().replaceAll(",", "/");
		fixedName = fixedName.replaceAll("\\[", ":");
		fixedName = fixedName.replaceAll("]", "[?]");
		fixedName = fixedName.substring(0, fixedName.length()-5);
		
		StringField docidField = new StringField("docid", fixedName, Field.Store.YES);
		TextField titleField = new TextField("title", title, Field.Store.YES);
		TextField headerField = new TextField("header", headers, Field.Store.YES);
		TextField contentField = new TextField("content", content, Field.Store.YES);
		titleField.setBoost(1.5f);
		headerField.setBoost(1.25f);
		contentField.setBoost(1.0f);

		doc.add( docidField );
		doc.add( titleField );
		doc.add( headerField );
		doc.add( contentField );
		
		return doc;
	}
}
