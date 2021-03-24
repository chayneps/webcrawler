package com.chayne.crawler

import groovy.util.logging.Slf4j
import groovy.xml.MarkupBuilder
import groovy.xml.XmlParser
import org.cyberneko.html.parsers.SAXParser


@Slf4j
class Crawler {
    Parser parser
    XmlParser htmlParser
    int sleepMs = 1000

    Crawler(Parser parser) {
        this.parser = parser

        def saxParser = new SAXParser()
        saxParser.setFeature('http://xml.org/sax/features/namespaces', false)
        htmlParser= new XmlParser(saxParser)

    }

    String crawl(String rootUrl) {
        def writer = new StringWriter()
        def markup = new MarkupBuilder(writer)

        Set<Resource> visitedLinks = new HashSet<>()
        visitedLinks.add(new Resource(type: ResourceType.A, url: rootUrl))

        String pageTitle = "Map of the site $rootUrl"

        markup.html {
            title(pageTitle)
            body {
                h1(pageTitle)
                doCrawl(rootUrl, rootUrl,  visitedLinks, markup)
            }
        }

        return writer.toString()
    }

    String doCrawl(String url, String rootUrl,  Set<Resource> visitedLinks, MarkupBuilder markup) {

        Node html

        boolean success = false
        int retryCount = 0

        // Try to get resource, if encounter IOError, eg, 429: Too many request, sleep and retry
        while(!success && retryCount<10) {
            try {
                html = htmlParser.parse(url)
                success = true
            } catch (IOException ioe) {
                log.error(ioe.message)
                log.info("Encounter error. Slow down!!")
                sleep(sleepMs)
                retryCount++
            } catch (Exception e) { //Error rather than IOError indicate resource cannot be parse
                log.error(e.message)
                retryCount=11
            }
        }

        if(success){

            log.info("GET $url")

            Set<Resource> links = this.parser.parse(html)

            return markup.ul {

                links.forEach(link -> {

                    if (link.type == ResourceType.A) {
                        if (link.url.startsWith("/")) {
                            link.url = rootUrl + link.url
                        } else if(link.url=="rootUrl/")
                            link.url = rootUrl
                    }

                    li {
                        span("resource type: $link.type.description ")
                        a(href: "$link.url", "$link.url")
                    }

                    //If it is A and under the same host and not visited before
                    if (link.type == ResourceType.A &&
                            link.url.startsWith(rootUrl) &&
                            !visitedLinks.contains(link)) {

                        visitedLinks.add(link)

                        doCrawl(link.url, rootUrl, visitedLinks, markup)

                    }
                })
            }
        } else {
            log.error("Cannot GET $url. Skip!")
            return markup.ul{
                li {
                    span("skip $url ")
                }
            }
        }

    }


}

