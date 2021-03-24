# Crawler

Using Groovy with SpringBoot's CommandLine runner.

# Usage

`java -jar webcrawler.jar <URL> <optional:outputHtml>`

for example

`java -jar webcrawler.jar https://www.sedna.com sitemap.html` or
`java -jar webcrawler.jar https://www.sedna.com`

# Feature
- It can handle 426: Too many requests or other kind of network error.


# Limitation & Bugs

- Cannot follow any kind of HTTP redirect, e.g. 302 permanent
- Cannot parse dynamic HTML page.

