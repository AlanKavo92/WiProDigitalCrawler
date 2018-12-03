
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


/**
 * 
 * @author Alan Kavanagh
 *
 */
public class WiProCrawler {

	private static final Logger LOGGER = Logger.getLogger(WiProCrawler.class.getName());
	
	private static final String DOMAIN = "https://wiprodigital.com";
	private static final String SITEMAP = "sitemap.xml";

	private LinkedHashSet<String> internal_links;
	private HashSet<String> external_links;
	private HashSet<String> static_content;

	public WiProCrawler() {
		this.internal_links = new LinkedHashSet<String>();
		this.external_links = new HashSet<String>();
		this.static_content = new HashSet<String>();
		LOGGER.fine("WiProCrawler: WiProCrawler initialised");
	}

	/**
	 * Recursively gets all links on a page
	 * 
	 * @param URL: URL to parse
	 */
	private void getPageLinks(String URL) {
		if (!internal_links.contains(URL)) {
			try {
				LOGGER.fine(String.format("WiProCrawler: Found new internal link: %s", URL));
				internal_links.add(URL);

				org.jsoup.nodes.Document document = Jsoup.connect(URL).get();
				org.jsoup.select.Elements linksOnPage = document.select("a[href]");

				for (org.jsoup.nodes.Element page : linksOnPage) {
					if (page.attr("abs:href").startsWith(DOMAIN)) {
						getPageLinks(page.attr("abs:href"));
					} 
					else {
						/*
						 * [^\\s]+ - 1 or more anything 
						 * (?i) - Ignore case sensitive for following patterns 
						 * (jpg|png|gif|mp4|jpeg) - Image/Video formats to match
						 */
						if (page.attr("abs:href").matches("([^\\s]+(\\.(?i)(jpg|png|gif|mp4|jpeg))$)")) {
							LOGGER.fine(String.format("WiProCrawler: Found new static content: %s", page.attr("abs:href")));
							static_content.add(page.attr("abs:href"));
						} 
						else {
							LOGGER.fine(String.format("WiProCrawler: Found new external link: %s", page.attr("abs:href")));
							external_links.add(page.attr("abs:href"));
						}
					}
				}
			} 
			catch (IOException e) {
				LOGGER.warning("WiProCrawler: For '" + URL + "': " + e.getMessage());
			}
		}
	}

	/**
	 * Writes a sitemap file to path of sitemap param
	 * @param sitemap: Path to write the sitemap
	 */
	private void writeSitemapXML(String sitemap) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	
			org.w3c.dom.Document doc = docBuilder.newDocument();

			org.w3c.dom.Element rootElement = doc.createElement("urlset");
			doc.appendChild(rootElement);
			
			org.w3c.dom.Attr attr = doc.createAttribute("xmlns");
			attr.setValue("http://www.sitemaps.org/schemas/sitemap/0.9");
			rootElement.setAttributeNode(attr);
	
			/*
			 * Internal Links
			 */
			for (String url: internal_links) {
				org.w3c.dom.Element ele = doc.createElement("url");
				rootElement.appendChild(ele);
				attr = doc.createAttribute("loc");
				org.w3c.dom.Element loc = doc.createElement("loc");
				loc.appendChild(doc.createTextNode(url));
				ele.appendChild(loc);
			}
				
			/*
			 * External Links
			 */
			for (String url: external_links) {
				org.w3c.dom.Element ele = doc.createElement("external-url");
				rootElement.appendChild(ele);
				attr = doc.createAttribute("loc");
				org.w3c.dom.Element loc = doc.createElement("loc");
				loc.appendChild(doc.createTextNode(url));
				ele.appendChild(loc);
			}
			
			/*
			 * Static Content
			 */
			for (String url: static_content) {
				org.w3c.dom.Element ele = doc.createElement("static-content");
				rootElement.appendChild(ele);
				attr = doc.createAttribute("loc");
				org.w3c.dom.Element loc = doc.createElement("loc");
				loc.appendChild(doc.createTextNode(url));
				ele.appendChild(loc);
			}
	
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(sitemap));
	
			transformer.transform(source, result);
			LOGGER.info("WiProCrawler: Sitemap saved to sitemap.xml");
		} 
		catch (ParserConfigurationException pce) {
			LOGGER.warning(String.format("WiProCrawler: ParserConfigurationException: %s", pce.getStackTrace().toString()));
		} 
		catch (TransformerException tfe) {
			LOGGER.warning(String.format("WiProCrawler: Transformerexception: %s", tfe.getStackTrace().toString()));
		}
	}

	public static void main(String[] args) {
		LOGGER.setLevel(Level.FINE);
		LOGGER.info("WiProCrawler: Application started");
		LOGGER.info(String.format("WiProCrawler: Root domain set to: %s", DOMAIN));
		WiProCrawler wpc = new WiProCrawler();
		wpc.getPageLinks(DOMAIN);
		wpc.writeSitemapXML(SITEMAP);
	}
}