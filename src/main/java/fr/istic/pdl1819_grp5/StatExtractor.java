package fr.istic.pdl1819_grp5;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;

import static fr.istic.pdl1819_grp5.wikiMain.getListofUrls;

public class StatExtractor {
    public StatExtractor() {
    }

    public FileWriter statbeforeExtraction(File inputdata, FileWriter statcsv) throws IOException {


        Set<UrlMatrix> liturls = getListofUrls(inputdata);
        char separator = ',';
        int comptbox = 0, comptnav = 0, comptlinks = 0, comptother = 0;
        int totalbox = 0, totalnav = 0, totallinks = 0, totalothers = 0;
        statcsv.write("url" + separator + "box" + separator + "nav" + separator + "links" + separator + "others");
        statcsv.write("\n");
       //System.out.println(inputdata);
        for (UrlMatrix url : liturls) {
            try {
            	
            	Document doc = Jsoup.connect(URI.create(url.getLink()).toASCIIString())
                        .userAgent("PostmanRuntime/7.26.5")
                        .referrer("http://www.google.com")
                        .ignoreHttpErrors(true)
                        .get();
                Elements tables = doc.getElementsByTag("table");
                for (int i = 0; i < tables.size(); i++) {
                    if (tables.get(i).className().contains("box")) {
                        comptbox++;

                    }
                    if (tables.get(i).select(".nv-") != null) {
                        comptnav++;


                    }
                    if (tables.get(i).select(".nowraplinks").first() != null) {
                        comptlinks++;

                    }
                    if (tables.get(i).className().contains("wikitable") && (tables.get(i).selectFirst("[class*=\"nv-\"]") == null || tables.get(i).selectFirst("[class*=\"box\"]") == null
                            || !tables.get(i).className().contains("box") || !tables.get(i).className().contains("nv-"))) {

                        comptother++;
                    }


                }
            } catch (IOException e) {
                e.printStackTrace();

            }
            totalbox = totalbox + comptbox;
            totalnav = totalnav + comptnav;
            totallinks = totallinks + comptlinks;
            totalothers = totalothers + comptother;
            String scomptbox = Integer.toString(comptbox);
            String scomptnav = Integer.toString(comptnav);
            String scomptlinks = Integer.toString(comptlinks);
            String scomptother = Integer.toString(comptother);

            String urlmodif = "";
            if (url.getLink().contains(",")) {


                for (int i = 0; i < url.getLink().length(); i++) {

                    if (url.getLink().charAt(i) != ',') {
                        urlmodif += url.getLink().charAt(i);

                    }
                }
                statcsv.write(urlmodif + separator + scomptbox + separator + scomptnav + separator + scomptlinks + separator + scomptother);
                statcsv.write("\n");
            } else {
                statcsv.write(url.getLink() + separator + scomptbox + separator + scomptnav + separator + scomptlinks + separator + scomptother);
                statcsv.write("\n");

            }


            comptbox = 0;
            comptlinks = 0;
            comptnav = 0;
            comptother = 0;


        }
        statcsv.write("TOTAL" + separator + totalbox + separator + totalnav + separator + totallinks + separator + totalothers);
        statcsv.close();
        // logger.log(Level.INFO, "end of searching table by criteria");

        return statcsv;

    }
   //pour chaque url connaitre le nombre de tableau extrait de part et d'autres et mettre dans un fichier csv
    public  FileMatrix statafterextracting(ArrayList<String> urls, ArrayList<Integer> extractedHTML, ArrayList<Integer> extractedWikitext, ArrayList<String> urlsWikitext) {
        FileMatrix fm = new FileMatrix("C:\\Users\\emman\\IdeaProjects\\PDL_1920_groupe-7\\output\\statsExtractor.csv");
        fm.setText("URL,Tables_extracted_with_Html,Tables_extracted_with_Wikitext" + "\n");
        ArrayList<Integer> result = new ArrayList<Integer>();
        int sommehtml = 0;
        int sommewikitext = 0;
        //rangement de la liste des urls de wikitext de sorte a avoir la meme liste
        for (int i = 0; i < urls.size(); i++) {
            int index = urlsWikitext.indexOf(urls.get(i));
            result.add(extractedWikitext.get(index));
        }
        for (int i = 0; i < urls.size(); i++) {
            if (urls.get(i).contains(",")) {
                String urlmodif = "";
                for (int j = 0; j < urls.get(i).length(); j++) {
                    if (urls.get(i).charAt(j) != ',') {
                        urlmodif += urls.get(i).charAt(j);
                    }
                }
                urls.set(i, urlmodif);
            }
            fm.append(urls.get(i) + "," + extractedHTML.get(i) + "," + result.get(i) + "\n");
            sommehtml += extractedHTML.get(i);
            sommewikitext += result.get(i);
        }
        fm.append("Total," + sommehtml + "," + sommewikitext);
        return fm;
    }
}
