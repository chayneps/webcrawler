package com.chayne.crawler

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString


@ToString
@EqualsAndHashCode
class Resource {
    ResourceType type
    String url
}

enum ResourceType {
    A("A",
            {
                    it.name() instanceof String && //Name sometime return QName Object
                        it.name().toUpperCase()=="A" &&
                        it.@href !=null &&
                        !it.@href.isEmpty() &&
                        !it.@href.startsWith("#")
            },
            {
                new Resource(type: A,
                        url: it.@href)
            }),

    IMAGE("IMG",
            {
                it.name() instanceof String &&
                        it.name().toUpperCase()=="IMG"
            },
            {
                new Resource(type: IMAGE,
                        url: it.@src)
            }),

    CSS("CSS",
            {
                it.name() instanceof String &&
                        it.name().toUpperCase()=="LINK" &&
                        it.@rel=="stylesheet"
            },
            {
                new Resource(type: CSS,
                        url: it.@href)
            })

    ;


    final String description
    final Closure filter
    final Closure extract

    ResourceType(String description,
                 Closure filter,
                 Closure extract) {

        this.description = description
        this.extract = extract
        this.filter = filter
    }
}
