package fr.istic.pdl1819_grp5;


import info.bliki.wiki.model.WikiModel;
import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;




	 /**
	 * b>Converter interface</b> implementation
	 * @see Converter
	 */
public class ConverterToCsv implements Converter {

	public static  Logger logger=Logger.getLogger("ConverterToCsv.class");
	 //faire une fonction pour desactiver les loggers

		 /**
		  * *Cells spreads on more than one row/column
		  * colspan: column length
		  * rowspan: row length
		  * row & column: where the cell begin
		  */

		static class PriorityCell{
			private  int colspan;
	        private  int rowspan;
	        private final int row;
	        private final int column;

		/**
		 * @param rowspan length of the cell in row
		 * @param colspan length of the in column
		 * @param row	number of where the cell is
		 * @param column umber of the column where the cell is
		 */
        public PriorityCell(String rowspan, String colspan, int row, int column) {

            try {
                this.colspan = Integer.parseInt(colspan);
            }catch (NumberFormatException nException){
                this.colspan = -1;
            }
            try {
                this.rowspan = Integer.parseInt(rowspan);
            }catch (NumberFormatException nException){
                this.rowspan = -1;
            }
            this.row = row;
            this.column = column;
        }
    }

	/**
	 * Browse from the bottom to the top for a table inside the one in parameter
	 * mode bottom_up
	 * @param table
	 * @return true if table contains another table
	 */
    public boolean isNested(Element table){

		logger.entering(ConverterToCsv.class.getName(),"isNested", table);
		boolean hasTableParent = false;
		Elements parents = table.parents();
		for (Element parent : parents) {
			if(parent.nodeName().equalsIgnoreCase("table")){
				hasTableParent =true;
				break;
			}
		}
		boolean hasTablechild = table.getElementsByTag("table").size()>1;
		logger.exiting(ConverterToCsv.class.getName(),"isNested",hasTablechild||hasTableParent);
		return hasTableParent || hasTablechild;
	}

	/**
	 * Check the table tags looking for "wikitable" tag
	 * @param table
	 * @return true if "table" has a "wikitable" class
	 */
	public static boolean isRelevant(Element table) {
		logger.entering(ConverterToCsv.class.getName(),"isRelevent",table);
		boolean isRelevant = table.selectFirst("[class*=\"nv-\"]")==null  || table.selectFirst("[class*=\"box\"]")==null
				|| !table.className().contains("box") || !table.className().contains("nv-");
		logger.exiting(ConverterToCsv.class.getName(),"isRelevent",table.className().contains("wikitable")&& isRelevant);

		return table.className().contains("wikitable") && isRelevant;
	}


    private static List<PriorityCell> listOfCells= new ArrayList<PriorityCell>();
	private static int numberOfcsv;
	private static String separateur=",";
	private int nbRelev;
	private int nbNotRelev;
	private int wikiRelev;
	private int wikiNotRelev;

	/**
	 * class initializer
	 */
	static{
		numberOfcsv = 0;
	}




	public ConverterToCsv(){
		this.nbRelev=0;
		this.nbNotRelev=0;
		this.wikiRelev=0;
		this.wikiNotRelev=0;
	}

	public HashMap<String, Integer> getRelev(){
		HashMap<String, Integer> hm=new HashMap<String, Integer>();
		hm.put("nbRelev", nbRelev);
		hm.put("nbNotRelev", nbNotRelev);
		hm.put("wikiRelev", wikiRelev);
		hm.put("wikiNotRelev", wikiNotRelev);
		return hm;

	}

		/**
		 * Browse for each child of the table and if find "colspan" tag,
		 * implements the counter of the number of column : nbCol
		 * @param table
		 * @return nbCol, number of column of table in parameter
		 */
	private static int NumberOfColumn(Element table){
		logger.entering(ConverterToCsv.class.getName(),"NumberOfColumn",table);

		Elements els=table.select("tr").first().children();
		int nbCol=0;
		for (Element el: els) {
			String colspan=el.attr("colspan");
			if(!colspan.equals("")){//??????
				nbCol+=Integer.parseInt(colspan);
			}else{
				nbCol++;
			}
		}
		logger.exiting(ConverterToCsv.class.getName(),"NumberOfColumn",nbCol);
		return nbCol;
	}

	/**
	 * Check if the row or the column length is spread on more than one colspan or one rowspan
	 * @param row
	 * @param column
	 * @return true if row & column are in a PriorityCell
	 */
    private static boolean hasPriorityCell(int row, int column){
        logger.entering(ConverterToCsv.class.getName(),"hasPriorityCell",new Object[]{row,column});
        boolean found=false;
        for (PriorityCell p: listOfCells) {
            if(p.row==row && ((column<=p.column + p.colspan-1) || p.colspan==0) ){
                found=true;
                break;
            }else if( p.column==column && ((row<=p.row+p.rowspan-1) || p.rowspan==0) ){
                found=true;
                break;
            }
        }
        return found;
    }

	/**
	 * Create a set <b>csvSet</b> of <b>FileMatrix</b>,
	 * take all the tables on the page
	 * check if the tables on the URL are in Wikitext class
	 * and don't have another table inside them, then convert them in CSV
	 * then call <b>convertHtmlTable</b> which create a file containing the result and add it to cvsSet
	 * @param url
	 * @return csvSet, set of files with all the tables from the URL page write in CSV
	 * @throws IOException
	 * @see FileMatrix
	 * @see #isRelevant(Element)
	 * @see #isNested(Element)
	 * @see #convertHtmlTable(Element)
	 */
	public Set<FileMatrix> convertFromHtml(String url) throws IOException {

		Set<FileMatrix> csvSet = new HashSet<FileMatrix>();

		try {
			Document doc = Jsoup.connect(url).get();
			Elements tables = doc.getElementsByTag("table"); 

			for(int i =0; i<tables.size();i++){
				if(isRelevant(tables.get(i))  &&  !isNested(tables.get(i)) ){
					csvSet.add(convertHtmlTable(tables.get(i)));
					nbRelev++;
				}
				else{
					nbNotRelev++;
				}
			}
		}catch (HttpStatusException e){
		}
		return csvSet;
	}

	/**
	 * From a HTML table, count the number of column of the first row,
	 * first line always have the exact number of row
	 * Look for the tags composing the table: thead tr, tbody tr and tfoot tr
	 * and write the counterpart in CSV using the writeInCsv method
	 * Finally, create a CSV file with the result
	 * @param htmlTable table write in HTML
	 * @return a CSV file containing the CSV version of the table in parameter
	 * @throws IndexOutOfBoundsException
	 * @see #writeInCsv(Elements, StringBuilder, int)
	 * @see #NumberOfColumn(Element)
	 */
	public static FileMatrix convertHtmlTable(Element htmlTable) throws IndexOutOfBoundsException{

		final int nbCol=NumberOfColumn(htmlTable);

		StringBuilder csvBuilder = new StringBuilder("");

		Elements trh=htmlTable.select("thead tr");
		writeInCsv(trh, csvBuilder, nbCol);

		listOfCells.clear();

        Elements trs = htmlTable.select("tbody tr");
		writeInCsv(trs, csvBuilder, nbCol);

		listOfCells.clear();

		Elements trf=htmlTable.select("tfoot tr");
		writeInCsv(trf, csvBuilder, nbCol);
		
		numberOfcsv++;
	    Csv csv = new Csv("csv"+numberOfcsv);
	    csv.setText(csvBuilder.toString());

		return csv;
	}

	/**
	 * 
	 * @param trs
	 * @param csvBuilder string block that contains the translation of the arrays into CSV language
	 * @param nbCol number total of column of the table to build
	 */
	private static void writeInCsv(Elements trs, StringBuilder csvBuilder, int nbCol){

		for (int i =0; i<trs.size();i++) {
			Element tr = trs.get(i);
			Elements tds = tr.children();
			int index=0;

			for(int j=0;j<nbCol;j++) {
				
				if(hasPriorityCell(i,j)){
					csvBuilder.append(separateur);
					
				}else {
					if(index>=tds.size()){
						csvBuilder.append(separateur);
						
					}else {
						String rowSpan = "";
						String columnSpan= "";
						rowSpan=tds.get(index).attr("rowspan");
						columnSpan=tds.get(index).attr("colspan");
						
						if(!rowSpan.equals("") || !columnSpan.equalsIgnoreCase("")){
							PriorityCell p= new PriorityCell(rowSpan,columnSpan,i,j);
							listOfCells.add(p);
						}

						String textAjout = tds.get(index).text();

						if (textAjout.contains(separateur)){
							textAjout="\""+tds.get(index).text()+"\"";
						}
						if(textAjout.contains("{{")){
							textAjout = withoutTags(textAjout);
						}
						csvBuilder.append(index==0?textAjout:separateur+textAjout);
						index++;
					}
				}
			}
			csvBuilder.append("\n");
		}
	}

		 /**
		  *
		  * @param s
		  * @return String s without wikitext tags
		  */
         public static String withoutTags(String s){
			 String result ="";
			 boolean warning = false;
         	if(s.contains("font")){
				int cpt =0;
				for(int i = 0; i<s.length(); i++){
					if(s.charAt(i) == '{' ){
						warning = true;
					}
					else if(warning){
						if(s.charAt(i)=='|'){
							cpt = cpt+1;
						}
						else if(cpt==2){
							warning = false;
							result += s.charAt(i);
							cpt =0;
						}
					}
					else if(!warning && s.charAt(i)!='}'){
						result += s.charAt(i);
					}
				}
			}


         	else{
				//boolean warning = false;
				for(int i = 0; i<s.length(); i++){
					if(s.charAt(i) == '{' && s.charAt(i+1)=='{'){
						warning = true;
					}

					else if(s.charAt(i) == '|'){
						warning = false;
					}
					else if(!warning && s.charAt(i)!='}'){
						result += s.charAt(i);
					}
				}
				if (result.equals("") && !warning) {
					Map<String, Locale> localeMap;

					System.out.println(s);
					//Affect empty string to codeCountry if s.length under 5
					String codeCountry = (s.length() > 5) ? s.substring(2, 5) : "";

					String[] countries = Locale.getISOCountries();
					localeMap = new HashMap<String, Locale>(countries.length);

					for (String country : countries) {
						Locale locale = new Locale("", country);
						localeMap.put(locale.getISO3Country().toUpperCase(), locale);
					}

					if( localeMap.containsKey(codeCountry) ){
						String countryCode = localeMap.get(codeCountry).getCountry();
						Locale localeCountry = new Locale("", countryCode);
						result = localeCountry.getDisplayCountry(Locale.ENGLISH);

					}
				}
			}

             return result;
         }

	/**
	 * @param url page where Wikitext tables are
	 * @return csvSet, set of CSV file
	 * @see #isNested(Element) 
	 * @see #isRelevant(Element)
	 * @see #convertHtmlTable(Element)
	 */
	public Set<FileMatrix> convertFromWikitext(String url) {
		Set<FileMatrix> csvSet = new HashSet<FileMatrix>();
		try {

			MediaWikiBot wikiBot = new MediaWikiBot(url.substring(0,url.lastIndexOf("iki/"))+"/");
			Article article= wikiBot.getArticle(url.substring(url.lastIndexOf("/")+1,url.length()));

			Document doc;

			//check redirection
			if (article.getText().contains("REDIRECT")) {

				if(article.getText().lastIndexOf("#") !=0 ){
					url = "https://en.wikipedia.org/wiki/" + article.getText().substring(article.getText().indexOf("[")+1, article.getText().lastIndexOf("#"));
				}
				else {
					url = "https://en.wikipedia.org/wiki/" + article.getText().substring(article.getText().lastIndexOf("[")+1, article.getText().lastIndexOf("]]"));
				}

				wikiBot = new MediaWikiBot(url.substring(0, url.lastIndexOf("iki/")) + "/");
				article = wikiBot.getArticle(url.substring(url.lastIndexOf("/") + 1, url.length()));
				doc = Jsoup.parse(WikiModel.toHtml(article.getText()));
			}
			else {
				doc = Jsoup.parse(WikiModel.toHtml(article.getText()));
			}

			Elements tables = doc.getElementsByTag("table");

			try {
				for(int i =0; i<tables.size();i++){

					if(isRelevant(tables.get(i))){
						csvSet.add(convertHtmlTable(tables.get(i)));
						wikiRelev++;

					}
					else wikiNotRelev++;
				}
			}catch (Exception e){

			}


		}catch (Exception e){
			//e.printStackTrace();
		}
		return csvSet;
	}
}

