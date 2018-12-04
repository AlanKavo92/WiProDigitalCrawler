# WiProDigitalCrawler
## WiProDigital Web Crawler


### Requires Java 1.6+
https://www.java.com/en/download/

### Requires Maven
https://maven.apache.org/

### Requires JSoup and JAXB DOM (available via Maven)
```mvn clean install```

### How to install and execute
- Clone the repository
- Open the project in any interpreter that supports Java 1.6+ (Eclipse/IntelliJ)
- Run the project as a Java Application
- Refresh the root folder to get the sitemap.xml

### Reasoning and Tradeoffs
I decided to use Java to complete the assessment as it's currently my primary programming langauge.
If I had more time to complete this assessment (without distractions) I would have revised the scrapy module
and completed this assessment using Python as I feel the solution would have been more efficient.

### What would I have done with more time?
Studied the scrapy module and built a more efficient bot using Python

For this solution:
Unit Tests

- [x] Java
- [x] Couple of hours
- [x] Limited to one domain
- [x] Visit all pages within the domain
- [x] Doesnt follow external links
- [x] Output is Structured sitemap (XML)
- [x] Sitemap includes internal links
- [x] Sitemap includes external links
- [x] Sitemap includes static content (images+video)


- [x] Working webcrawler
- [x] README.md
- [x] How to build and run
- [x] Reasoning and tradeoffs
- [x] Explanation of what can be done with more time
- [x] Project builds/runs/tests as per instructions
