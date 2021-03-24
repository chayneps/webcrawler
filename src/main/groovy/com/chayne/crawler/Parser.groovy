package com.chayne.crawler

interface Parser {
    Set<Resource> parse(def html)
}

class CompositeParser implements Parser {

    List<Parser> parsers

    CompositeParser(List<Parser> parsers) {
        this.parsers = parsers
    }

    @Override
    Set<Resource> parse(def html) {
        Set<Resource> links = new LinkedHashSet<>()

        for (parser in parsers) {
            links.addAll(parser.parse(html))
        }

        return links
    }
}

class ResourceParser implements Parser {

    private final ResourceType linkConfig

    ResourceParser(ResourceType linkConfig) {
        this.linkConfig = linkConfig
    }

    @Override
    Set<Resource> parse(def html) {
        return html."**".findAll{
                    it instanceof Node
                }.findAll(linkConfig.filter)
                .collect(linkConfig.extract) as Set<Resource>

    }
}
