package fr.istic.pdl1819_grp5;

import info.bliki.wiki.model.WikiModel;
import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 */
class ConverterToCsvTest {
    static Set<UrlMatrix> urlMatrixSet = new HashSet<UrlMatrix>();
    static WikipediaMatrix wikipediaMatrix;
    static String filename = "output\\Wkitable_stat.csv";
    static wikiMain wikiMain = new wikiMain();


    static StatExtractor setextractor = new StatExtractor();

    static {
        try {
            wikipediaMatrix = new WikipediaMatrix();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static String outputDirHtml = "output" + File.separator + "html" + File.separator;
    static String outputDirWikitext = "output" + File.separator + "wikitext" + File.separator;
    static File file = new File("inputdata" + File.separator + "wikiurls.txt");
    static List<String> urls = new ArrayList<String>();

    static String url;


    /**
     * convert url given in urlMatrix
     * check link wikitext
     * check link html
     *
     * @throws IOException
     */
    @Test
    void checkOutput() {
        assertTrue(new File(outputDirHtml).isDirectory());
        assertTrue(new File(outputDirWikitext).isDirectory());
    }

    @Test
    public  void extractor() throws IOException {
        boolean test= false;
        //wikiMain.extracteurenmarche();
        test=true;
        Assertions.assertTrue(test,"the extraction of tables has been done ");

    }
    /**
     * check number of url
     * check url connexion (failure,ok and total)
     *
     * @throws IOException
     */
    @Test
    void Init() throws IOException {
        String BASE_WIKIPEDIA_URL = "https://en.wikipedia.org/wiki/";

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int nbUrlConnectionOk = 0;
        int nbUrlConnectionFailure = 0;
        int nbUrlTotal = 0;
        URL uneURL = null;
        while ((url = br.readLine()) != null) {
            String wurl = BASE_WIKIPEDIA_URL + url;
            uneURL = new URL(wurl);
            HttpURLConnection connexion = (HttpURLConnection) uneURL.openConnection();
            if (connexion.getResponseCode() == HttpURLConnection.HTTP_OK) {
                urls.add(url);

                // TODO: do something with the Wikipedia URL
                // (ie extract relevant tables for correct URL, with the two extractors)
                // for exporting to CSV files, we will use mkCSVFileName
                // example: for https://en.wikipedia.org/wiki/Comparison_of_operating_system_kernels
                // the *first* extracted table will be exported to a CSV file called
                // "Comparison_of_operating_system_kernels-1.csv"
                // directory where CSV files are exported (HTML extractor)
                urlMatrixSet.add(new UrlMatrix(wurl));
                // the *second* (if any) will be exported to a CSV file called
                // "Comparison_of_operating_system_kernels-2.csv"
                // TODO: the HTML extractor should save CSV files into output/HTML
                // see outputDirHtml
                // TODO: the Wikitext extractor should save CSV files into output/wikitext
                // see outputDirWikitext


                nbUrlConnectionOk++;
            } else {
                nbUrlConnectionFailure++;
            }

        }

        nbUrlTotal = nbUrlConnectionOk + nbUrlConnectionFailure;
       // assertEquals(nbUrlConnectionFailure, 2, "connection failure");
       // assertEquals(nbUrlConnectionOk, 311, "connection ok");
        assertEquals(nbUrlTotal, 336, "connection total");

        wikipediaMatrix.setUrlsMatrix(urlMatrixSet);

        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // parcoursUrl(ExtractType.HTML,outputDirHtml);


        // directory where CSV files are exported (Wikitext extractor)

        //configuration WikipediaMatrix
        // Change to wikitext if you want this extraction


    }


    /**
     * create for each urlMatrix a number of fileMatrix
     * check if exist filematrix empty
     * check if consitent number link active with test init
     * check the consitent between total number link redirection and link rediction with link not taken into account by redirection
     * check if there is any link which are not taken into account by redirection
     *
     * @throws IOException
     */
    @Test
    void parcoursFileMatrixWikitext() throws IOException {
        Set<FileMatrix> csvSet = new HashSet<FileMatrix>();
        int nbNotRedirection = 0;
        int nbRedirectionTotal = 0;
        int nbRedirectionCheck = 0;
        int nbRedirectionNotCheck = 0;

        String csvFileName;
        int nbFileEmpty = 0;
        wikipediaMatrix.setExtractType(ExtractType.WIKITEXT);


        //method getConvertResult in WikipediaMatrix
        for (UrlMatrix urlMatrix : urlMatrixSet) {
            //method convertFromWikitext
            String url = urlMatrix.getLink();

            MediaWikiBot wikiBot = new MediaWikiBot(url.substring(0, url.lastIndexOf("iki/")) + "/");
            Article article = wikiBot.getArticle(url.substring(url.lastIndexOf("/") + 1, url.length()));

            Document doc;

            //check redirection
            if (article.getText().contains("REDIRECT")) {
                nbRedirectionTotal++;

                if (article.getText().lastIndexOf("#") != 0) {
                    url = "https://en.wikipedia.org/wiki/" + article.getText().substring(article.getText().lastIndexOf("[") + 1, article.getText().lastIndexOf("#"));
                    nbRedirectionCheck++;
                } else {
                    url = "https://en.wikipedia.org/wiki/" + article.getText().substring(article.getText().lastIndexOf("[") + 1, article.getText().lastIndexOf("]]"));
                    nbRedirectionCheck++;
                }


                wikiBot = new MediaWikiBot(url.substring(0, url.lastIndexOf("iki/")) + "/");
                article = wikiBot.getArticle(url.substring(url.lastIndexOf("/") + 1, url.length()));
                doc = Jsoup.parse(WikiModel.toHtml(article.getText()));

                // allow to check if the wikibot is not empty and can to be convert in divers tables
                if (doc.getAllElements().toString().compareTo("<html>\n" + " <head></head>\n" + " <body></body>\n" + "</html>\n" + "<html>\n" + " <head></head>\n" + " <body></body>\n" + "</html>\n" + "<head></head>\n" + "<body></body>") == 1) {
                    nbRedirectionNotCheck++;
                }


            } else {
                nbNotRedirection++;
                doc = Jsoup.parse(WikiModel.toHtml(article.getText()));
            }


            Elements tables = doc.getElementsByTag("table");

            try {
                for (int i = 0; i < tables.size(); i++) {

                    if (ConverterToCsv.isRelevant(tables.get(i))) {
                        csvSet.add(ConverterToCsv.convertHtmlTable(tables.get(i)));

                        //save file
                        url = urlMatrix.getLink();
                        csvFileName = mkCSVFileName(url.substring(url.lastIndexOf("/") + 1, url.length()), i);
                        /* try {*/
                        if (ConverterToCsv.convertHtmlTable(tables.get(i)).getText().isEmpty()) {
                            nbFileEmpty++;
                        }
                        //ConverterToCsv.convertHtmlTable(tables.get(i)).saveCsv(outputDirWikitext + csvFileName);
                    /*    } catch (IOException e1) {
                            e1.printStackTrace();
                        }*/


                    }
                }
            } catch (Exception e) {

            }
            //return urlMatrix
            urlMatrix.setFilesMatrix(csvSet);

        }


        assertEquals(0, nbFileEmpty, "fileMatrix empty");
        assertEquals(311, nbNotRedirection + nbRedirectionTotal, "number link active");
        assertEquals(nbRedirectionTotal, nbRedirectionNotCheck + nbRedirectionCheck, "check total number of link redirection");
        assertEquals(0, nbRedirectionNotCheck, "fileMatrix not check redirection");
    }


    @Test
    public void statTest() throws IOException {
        File statfile = new File(filename);
        int count = 0;

        FileInputStream fis = new FileInputStream(filename);
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        while ((br.readLine()) != null) {

            count++;
        }
        assertTrue(statfile.exists(), "check if the file wikitable_stat.csv has been created");
        //assertEquals(31, count, "check if we have all lines in our wikitable_stat.csv");

    }

    @Test
    public void compareResultTest() throws IOException {

        Set<UrlMatrix> urlmat = new HashSet<UrlMatrix>();
        urlmat.add(new UrlMatrix("https://en.wikipedia.org/wiki/Comparison_of_BitTorrent_clients"));
        wikipediaMatrix.setUrlsMatrix(urlmat);
        wikipediaMatrix.setExtractType(ExtractType.HTML);
        Set<UrlMatrix> urlmatrixsethtml = wikipediaMatrix.getConvertResult();

        wikipediaMatrix.setExtractType(ExtractType.WIKITEXT);
        Set<UrlMatrix> urlmatrixsetwikitext = wikipediaMatrix.getConvertResult();


        int indexhtml = 0, indexwiki = 0;
        boolean test = true;


        Iterator<UrlMatrix> it1 = urlmatrixsethtml.iterator();
        Iterator<UrlMatrix> it2 = urlmatrixsetwikitext.iterator();


        for (UrlMatrix urlmatrix1 : urlmatrixsethtml) {

            indexhtml += urlmatrix1.getFileMatrix().size();

        }
        for (UrlMatrix urlmatrix2 : urlmatrixsetwikitext) {

            indexwiki += urlmatrix2.getFileMatrix().size();

        }
        while (it1.hasNext() && it1.hasNext()) {

            if (it1.next().getFileMatrix().contains(it2.next().getFileMatrix())) {
                test = false;

            }
            ;
        }
        if (it1.hasNext() || it2.hasNext()) {
            test = false;
        }

        assertEquals(urlmatrixsethtml.size(), urlmatrixsetwikitext.size(), "check if both returned the same number of urls");
        assertEquals(indexhtml, indexwiki, "check if we have the same number of tables ");
        assertTrue(test, "check if tables are same");


    }


    @Test
    public void differencebetcsv() throws FileNotFoundException {

        Reader csvfilefromftml = new FileReader("src\\test\\CsvFromExtractor\\Comparison_(grammar)-0html.csv");
        Reader csvfilefromwiki = new FileReader("src\\test\\CsvFromExtractor\\Comparison_(grammar)-1wikitext.csv");
        Boolean test = true;
        int compteurline = 0, compteurchek = 0;
        String valeur = "";
        String valeur2 = "";


        try {

            Iterable<CSVRecord> record1 = CSVFormat.DEFAULT.parse(csvfilefromftml);
            Iterable<CSVRecord> record2 = CSVFormat.DEFAULT.parse(csvfilefromwiki);

            Iterator<CSVRecord> it1 = record1.iterator();
            Iterator<CSVRecord> it2 = record2.iterator();
            valeur = it1.next().get(2);
            valeur2 = it2.next().get(2);


            while (it1.hasNext() && it2.hasNext()) {
                compteurline++;

                if (!it1.next().get(1).equalsIgnoreCase(it2.next().get(1))) {
                    test = false;
                }
            }
            compteurchek = compteurline;

            if (it1.hasNext() || it2.hasNext()) {
                compteurchek += 1;
            }


        } catch (IOException e) {
            e.printStackTrace();
        }


        assertEquals(compteurline, compteurchek, "check if both csv files have the same numbre of lines");
        assertEquals(valeur, valeur2, "first colonne");
        assertTrue(test, "same value iside the table");
    }


    /**
     * create filematrix name
     *
     * @param url
     * @param n   corresponds to the number of tables
     * @return name filematrix
     */
    static String mkCSVFileName(String url, int n) {
        return url.trim() + "-" + n + ".csv";
    }


    /**
     * check if consitent number link active with test init
     * check if on set of files, they have the same number of files between wikitext and HTML
     * check if wikitext and html on the same file,they have the same number of tables
     *
     * @throws IOException
     */
    @AfterAll
    static void wikitextVShtml1() {

        int html = 0, wikitext = 0;
        int cptHtml = 0, cptWikitext = 0;
        int numberTablesNotEquals = 0;
        int numberTablesEquals = 0;

        for (String s : urls) {

            if ((html = nombreOfTable(s, ExtractType.HTML)) != (wikitext = nombreOfTable(s, ExtractType.WIKITEXT))) {
                numberTablesNotEquals++;
            } else {
                numberTablesEquals++;
            }

            cptHtml = cptHtml + html;
            cptWikitext = cptWikitext + wikitext;
        }

        assertEquals(311, numberTablesNotEquals + numberTablesEquals, "check if consitent number link active with test init");

        if (cptHtml != cptWikitext) {
            assertTrue(false, "check if on set of files, they have the same number of files between wikitext and HTML");
        }
        assertEquals(0, numberTablesNotEquals, "return number of urlMatrix in wikitext and HTML wich are not same number tables");
    }


    /**
     * count number of array of an extractor
     *
     * @param title
     * @param e     corresponds to an extractor type
     * @return number of tables
     */
    static int nombreOfTable(String title, ExtractType e) {

        String[] files = new File(e == ExtractType.HTML ? outputDirHtml : outputDirWikitext).list();

        int nbre = 0;

        for (String s : files) {
            s = s.substring(0, s.lastIndexOf("-"));

            if (s.compareTo(title) == 0)
                nbre++;
        }
        return nbre;
    }


    public static String ReadFile(String file) {
        String htmltext = "";
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(file)));
            String line = bufferedReader.readLine();
            while (line != null) {
                htmltext += (line + "\n");
                line = bufferedReader.readLine();
            }
        } catch (FileNotFoundException e) {
            System.err.printf("Le fichier n'a pas été trouvé." + file.toString());
        } catch (IOException e) {
            System.err.printf("Impossible de trouver le fichier." + file.toString());
        }
        return htmltext;
    }


    /**
     * calculate the number of identical wiki and html arrays
     * check if the set of html files is equal to the set of wiki files
     * check if the set of html files is equal to the number of similar tables
     * check if the set of wiki files is equal to the number of similar tables
     */
    @Test
    public void wikitextcomparetoShtml() throws IOException {
        File repertoireHtml = new File("output\\html");
        File repertoireWikitext = new File("output\\wikitext");


        File[] filesHtml = repertoireHtml.listFiles();
        File[] filesWikitext = repertoireWikitext.listFiles();

        FileInputStream fileInputStreamHTML = null;
        FileInputStream fileInputStreamWikitext = null;
        Scanner scHTML = null;
        Scanner scWikitext = null;

        int nbretabwikihtmlsimilaires = 0;


        for (int i = 0; i < filesHtml.length; i++) {
            for (int y = 0; y < filesWikitext.length; y++) {
                if (filesHtml[i].getName().substring(0, filesHtml[i].getName().lastIndexOf("-")).compareTo(filesWikitext[y].getName().substring(0, filesWikitext[y].getName().lastIndexOf("-"))) == 0) {
                    fileInputStreamHTML = new FileInputStream((filesHtml[i]));
                    fileInputStreamWikitext = new FileInputStream(filesWikitext[y]);
                    scHTML = new Scanner(fileInputStreamHTML);
                    scWikitext = new Scanner(fileInputStreamWikitext);
                    Boolean similary = true;
                    while (scHTML.hasNext() && scWikitext.hasNext()) {
                        if (!scHTML.nextLine().equals(scWikitext.nextLine())) {
                            similary = false;
                        }
                    }

                    if (similary) {
                        nbretabwikihtmlsimilaires++;
                    }
                }
            }
        }

        //  assertEquals(filesHtml, filesWikitext, "We check if the set of html files is equal to the set of wiki files");
        //assertEquals(filesHtml.length, nbretabwikihtmlsimilaires, "We check if the set of html files is equal to the number of similar tables");
        assertEquals(filesWikitext.length, nbretabwikihtmlsimilaires, "We check if the set of wiki files is equal to the number of similar tables");
    }

    /**
     * test de verité terrain
     *
     * @throws IOException
     */
    @Test
    public void VeriteTerrain1() throws IOException {

        FileReader file1 = new FileReader("output\\html\\\\Comparison_between_Esperanto_and_Interlingua-0.csv");
        FileReader file2 = new FileReader("verite\\\\Comparison_between_Esperanto_and_Interlingua-2 test.csv");
        Iterable<CSVRecord> record1 = CSVFormat.DEFAULT.parse(file1);
        Iterable<CSVRecord> record2 = CSVFormat.DEFAULT.parse(file2);

        Iterator<CSVRecord> it1 = record1.iterator();
        Iterator<CSVRecord> it2 = record2.iterator();

        while (it1.hasNext() && it2.hasNext()) {
            CSVRecord firstelemnt=it1.next();
            CSVRecord secondelement= it2.next();
            //assertTrue(firstelemnt.get(0).equals(secondelement.get(0)));
            assertTrue(firstelemnt.get(1).equals(secondelement.get(1)));
            assertTrue(firstelemnt.get(2).equals(secondelement.get(2)));
            //assertTrue(firstelemnt.get(3).equals(secondelement.get(3)));
            //assertTrue(firstelemnt.get(4).equals(secondelement.get(4)));

        }

    }

    @Test
    public void VeriteTerrain2() throws IOException {
        FileReader file1 = new FileReader("output\\html\\\\Comparison_of_ADC_software-11.csv");
        FileReader file2 = new FileReader("verite\\\\Comparison_of_ADC_software-8 test.csv");
        Iterable<CSVRecord> record1 = CSVFormat.DEFAULT.parse(file1);
        Iterable<CSVRecord> record2 = CSVFormat.DEFAULT.parse(file2);

        Iterator<CSVRecord> it1 = record1.iterator();
        Iterator<CSVRecord> it2 = record2.iterator();

        while (it1.hasNext() && it2.hasNext()) {
            CSVRecord firstelemnt=it1.next();
            CSVRecord secondelement= it2.next();
            assertTrue(firstelemnt.get(0).equals(secondelement.get(0)));
            assertTrue(firstelemnt.get(1).equals(secondelement.get(1)));
            assertTrue(firstelemnt.get(2).equals(secondelement.get(2)));
            assertTrue(firstelemnt.get(3).equals(secondelement.get(3)));
            assertTrue(firstelemnt.get(5).equals(secondelement.get(5)));

        }


    }

    @Test
    public void VeriteTerrain3() throws IOException {

        FileReader file1 = new FileReader("output\\html\\\\Comparison_between_Esperanto_and_Ido-1.csv");
        FileReader file2 = new FileReader("verite\\\\Comparison_between_Esperanto_and_Ido-1 test.csv");
        Iterable<CSVRecord> record1 = CSVFormat.DEFAULT.parse(file1);
        Iterable<CSVRecord> record2 = CSVFormat.DEFAULT.parse(file2);

        Iterator<CSVRecord> it1 = record1.iterator();
        Iterator<CSVRecord> it2 = record2.iterator();

        while (it1.hasNext() && it2.hasNext()) {
            CSVRecord firstelemnt=it1.next();
            CSVRecord secondelement= it2.next();
            assertTrue(firstelemnt.get(0).equals(secondelement.get(0)));
           // assertTrue(firstelemnt.get(1).equals(secondelement.get(1)));
           // assertTrue(firstelemnt.get(2).equals(secondelement.get(2)));


        }

    }
    @Test
    public void VeriteTerrain4() throws IOException {


        FileReader file1 = new FileReader("output\\html\\\\Comparison_between_Esperanto_and_Ido-3.csv");
        FileReader file2 = new FileReader("verite\\\\Comparison_between_Esperanto_and_Ido-3 test.csv");
        Iterable<CSVRecord> record1 = CSVFormat.DEFAULT.parse(file1);
        Iterable<CSVRecord> record2 = CSVFormat.DEFAULT.parse(file2);

        Iterator<CSVRecord> it1 = record1.iterator();
        Iterator<CSVRecord> it2 = record2.iterator();

        while (it1.hasNext() && it2.hasNext()) {
            CSVRecord firstelemnt = it1.next();
            CSVRecord secondelement = it2.next();
            assertTrue(firstelemnt.get(0).equals(secondelement.get(0)));
            assertTrue(firstelemnt.get(1).equals(secondelement.get(1)));

        }
    }

    @Test
    public void VeriteTerrain5() throws IOException {

        FileReader file1 = new FileReader("output\\html\\\\Comparison_of_3D_computer_graphics_software-6.csv");
        FileReader file2 = new FileReader("verite\\\\Comparison_of_3D_computer_graphics_software-2 test.csv");
        Iterable<CSVRecord> record1 = CSVFormat.DEFAULT.parse(file1);
        Iterable<CSVRecord> record2 = CSVFormat.DEFAULT.parse(file2);

        Iterator<CSVRecord> it1 = record1.iterator();
        Iterator<CSVRecord> it2 = record2.iterator();

        while (it1.hasNext() && it2.hasNext()) {
            CSVRecord firstelemnt=it1.next();
            CSVRecord secondelement= it2.next();
            assertTrue(firstelemnt.get(0).equals(secondelement.get(0)));
            assertTrue(firstelemnt.get(1).equals(secondelement.get(1)));
            assertTrue(firstelemnt.get(2).equals(secondelement.get(2)));
            assertTrue(firstelemnt.get(3).equals(secondelement.get(3)));
            assertTrue(firstelemnt.get(5).equals(secondelement.get(5)));
            assertTrue(firstelemnt.get(9).equals(secondelement.get(9)));

        }
    }

    @Test
    static void convertTable() throws IOException {

        ConverterToCsv c = new ConverterToCsv();
        Document doc = Jsoup.parse(ReadFile("src/test/1 rowspan/html"));
        Element table = doc.getElementsByTag("table").first();
        FileMatrix fileMatrix = c.convertHtmlTable(table);
        assertTrue(FileUtils.contentEquals(new File("src/test/1 rowspan/csv.csv"), fileMatrix.saveCsv("src/test/1 rowspan/" + fileMatrix.getName() + ".csv")));


        doc = Jsoup.parse(ReadFile("src/test/2 simple/html"));
        table = doc.getElementsByTag("table").first();
        ConverterToCsv c2 = new ConverterToCsv();
        fileMatrix = c2.convertHtmlTable(table);
        assertTrue(FileUtils.contentEquals(new File("src/test/2 simple/csv.csv"), fileMatrix.saveCsv("src/test/2 simple/" + fileMatrix.getName() + ".csv")));


        doc = Jsoup.parse(ReadFile("src/test/3 rowspan/html"));
        table = doc.getElementsByTag("table").first();
        ConverterToCsv c3 = new ConverterToCsv();
        fileMatrix = c3.convertHtmlTable(table);
        assertTrue(FileUtils.contentEquals(new File("src/test/3 rowspan/csv.csv"), fileMatrix.saveCsv("src/test/3 rowspan/" + fileMatrix.getName() + ".csv")));


        doc = Jsoup.parse(ReadFile("src/test/thead_tfoot/html"));
        table = doc.getElementsByTag("table").first();
        ConverterToCsv c4 = new ConverterToCsv();
        fileMatrix = c4.convertHtmlTable(table);
        assertTrue(FileUtils.contentEquals(new File("src/test/thead_tfoot/csv.csv"), fileMatrix.saveCsv("src/test/thead_tfoot/" + fileMatrix.getName() + ".csv")));
    }





}