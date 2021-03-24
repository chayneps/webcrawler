package com.chayne

import com.chayne.crawler.CompositeParser
import com.chayne.crawler.ResourceParser
import com.chayne.crawler.Crawler
import com.chayne.crawler.Parser
import com.chayne.crawler.ResourceType
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication



@SpringBootApplication
@Slf4j
class WebCrawler implements CommandLineRunner{

    static void main(String[] args){
        SpringApplication.run(WebCrawler,args)

    }

    @Override
    void run(String... args) throws Exception {

        if(args.size()==0 || StringUtils.isBlank(args[0])){
            log.error("Please provide URL")
            throw new IllegalArgumentException("Please provide URL");
        }

        List<Parser> resourceParsers = new ArrayList<>()
        for (ResourceType resourceType : ResourceType.values()) {
            Parser resourceParser = new ResourceParser(resourceType)
            resourceParsers << resourceParser
        }

        CompositeParser compositeLinkParser = new CompositeParser(resourceParsers)

        String result = new Crawler(compositeLinkParser).crawl(args[0])

        String outputFilename = args.size()>1?args[1]:'sitemap.html';

        FileWriter fw = new FileWriter("${outputFilename}",false)
        fw.write(result)
        fw.close()


        log.info("Sitemap is written in file ${outputFilename}")

    }
}
