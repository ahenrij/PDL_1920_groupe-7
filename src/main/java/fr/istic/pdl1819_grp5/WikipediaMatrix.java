package fr.istic.pdl1819_grp5;




import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class WikipediaMatrix
{

	
	private Set<UrlMatrix> urlMatrixSet;

	private Converter converter;

	private ExtractType extractType;

	public WikipediaMatrix(ExtractType type) throws IOException {

		this.urlMatrixSet = new HashSet<UrlMatrix>();
		this.converter = new ConverterToCsv();
		this.extractType =  type; // Default extraction

	}

	/*
	*@return
	* set of urlMatrix fill to csv.
	*/
	public Set<UrlMatrix> getConvertResult() throws IOException {

		
 		for (UrlMatrix urlMatrix : urlMatrixSet){
			if(extractType==ExtractType.HTML) {
				
				urlMatrix.setFilesMatrix(converter.convertFromHtml(urlMatrix.getLink()));
				
			} else 
			
			{
				urlMatrix.setFilesMatrix(converter.convertFromWikitext(urlMatrix.getLink()));
			}
 		}
 		
		return urlMatrixSet;
 	
	}


	public Set<UrlMatrix> getUrlsMatrix() {
		return urlMatrixSet;
	}

	public ExtractType getExtractType() {
		return extractType;
	}

	public void setUrlsMatrix(Set<UrlMatrix> urlMatrixSet) {
		this.urlMatrixSet = urlMatrixSet;
	}

	public void setExtractType(ExtractType extractType) {
		this.extractType = extractType;
	}

	public HashMap<String, Integer> getStats(){
		return converter.getRelev();
	}
}

