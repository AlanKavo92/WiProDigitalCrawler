
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;

public class WiProCrawler {

    private HashSet<String> links;
    private String allowed_domain;
    
    public WiProCrawler(String allowed_domain) {
    	this.allowed_domain = allowed_domain;
        this.links = new HashSet<String>();
    }

    public void getPageLinks(String URL) {
        if (!links.contains(URL)) {
            try {
                if (links.add(URL)) {
                    System.out.println(URL);
                }

                Document document = Jsoup.connect(URL).get();
                Elements linksOnPage = document.select("a[href]");

                for (Element page : linksOnPage) {
                	if(page.attr("abs:href").startsWith(allowed_domain))
                		getPageLinks(page.attr("abs:href"));
                }
            } catch (IOException e) {
                System.err.println("For '" + URL + "': " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
    	String domain = "https://wiprodigital.com";
    	WiProCrawler wpc = new WiProCrawler(domain);
    	wpc.getPageLinks(domain);
    }
}