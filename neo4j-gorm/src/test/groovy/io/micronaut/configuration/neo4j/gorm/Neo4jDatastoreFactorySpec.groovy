/*
 * Copyright 2017-2019 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.configuration.neo4j.gorm

import grails.gorm.annotation.Entity
import grails.gorm.transactions.Rollback
import io.micronaut.inject.qualifiers.Qualifiers
import org.grails.datastore.gorm.neo4j.Neo4jDatastoreTransactionManager
import org.grails.datastore.mapping.validation.ValidationException
import io.micronaut.configuration.neo4j.bolt.Neo4jBoltSettings
import io.micronaut.context.ApplicationContext
import org.springframework.transaction.PlatformTransactionManager
import spock.lang.AutoCleanup
import spock.lang.Requires
import spock.lang.Shared
import spock.lang.Specification

import javax.validation.constraints.NotBlank

/**
 * @author graemerocher
 * @since 1.0
 */
@Requires({ !jvm.isJava9Compatible() })
class Neo4jDatastoreFactorySpec extends Specification {

    @Shared @AutoCleanup ApplicationContext applicationContext = ApplicationContext.run('neo4j.uri': Neo4jBoltSettings.DEFAULT_URI)

    void "verify Neo4jDatastoreTransactionManager is qualified with name neo4j"() {
        expect:
        applicationContext.containsBean(Neo4jDatastoreTransactionManager)

        and:
        applicationContext.containsBean(Neo4jDatastoreTransactionManager, Qualifiers.byName("neo4j"))
    }

    @Rollback
    void "test configure GORM for Neo4j"() {
        when:
        new Team(name: "United").save(flush:true)

        then:
        Team.count() == 1
        Team.first().name == "United"
        Team.first().dateCreated != null

    }

    @Rollback
    void "test validation errors"() {
        when:
        new Team(name: "").save(failOnError:true)

        then:
        thrown(ValidationException)

    }
}

@Entity
class Team {
    @NotBlank
    String name
    Date dateCreated
}
