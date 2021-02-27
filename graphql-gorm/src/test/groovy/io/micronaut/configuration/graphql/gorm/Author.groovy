package io.micronaut.configuration.graphql.gorm

import grails.gorm.annotation.Entity
import org.grails.gorm.graphql.entity.dsl.GraphQLMapping
import org.grails.gorm.graphql.fetcher.impl.ClosureDataFetchingEnvironment

@Entity
class Author {

    String name

    static graphql = true

    static constraints = {
    }
}
