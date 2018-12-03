
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;

/**
 * 
 * @author Alan Kavanagh
 *
 */
public class WiProCrawler {

	private HashSet<String> internal_links;
	private HashSet<String> external_links;
	private HashSet<String> static_content;

	private String allowed_domain;

	public WiProCrawler(String allowed_domain) {
		this.allowed_domain = allowed_domain;
		this.internal_links = new HashSet<String>();
		this.external_links = new HashSet<String>();
		this.static_content = new HashSet<String>();
	}

	/**
	 * Recursively gets all links on a page
	 * 
	 * @param URL: URL to parse
	 */
	public void getPageLinks(String URL) 
	{
		if (!internal_links.contains(URL))
		{
			try
			{
				internal_links.add(URL);

				Document document = Jsoup.connect(URL).get();
				Elements linksOnPage = document.select("a[href]");

				for (Element page : linksOnPage)
				{
					if (page.attr("abs:href").startsWith(allowed_domain))
					{
						getPageLinks(page.attr("abs:href"));
					} 
					else
					{
						// [^\\s]+ - 1 or more anything	
						// (?i) - Ignore case sensitive for following patterns
						// (jpg|png|gif) - Image formats to match
						if (page.attr("abs:href").matches("([^\\s]+(\\.(?i)(jpg|png|gif))$)"))
						{
							static_content.add(page.attr("abs:href"));
						} 
						else
						{
							external_links.add(page.attr("abs:href"));
						}
					}
				}
			} 
			catch (IOException e)
			{
				System.err.println("For '" + URL + "': " + e.getMessage());
			}
		}
	}
	
	public void getInternalLinks()
	{
		for (String s : internal_links)
		{
			System.out.println(String.format("Internal: %s", s));
		}
	}
	
	public void getExternalLinks()
	{
		for (String s : external_links)
		{
			System.out.println(String.format("External: %s", s));
		}
	}
	
	public void getStaticContent()
	{
		for (String s : static_content)
		{
			System.out.println(String.format("Static Content: %s", s));
		}
	}

	public static void main(String[] args) {
		System.out.println("Application started");
		String domain = "https://wiprodigital.com";
		WiProCrawler wpc = new WiProCrawler(domain);
		wpc.getPageLinks(domain);
		wpc.getInternalLinks();
		wpc.getExternalLinks();
		wpc.getStaticContent();
	}
}