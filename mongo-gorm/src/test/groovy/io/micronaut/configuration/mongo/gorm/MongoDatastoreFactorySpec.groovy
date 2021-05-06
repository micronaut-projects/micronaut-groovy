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
package io.micronaut.configuration.mongo.gorm

import com.mongodb.client.MongoClient
import grails.gorm.annotation.Entity
import grails.gorm.transactions.Rollback
import io.micronaut.configuration.mongo.core.MongoSettings
import io.micronaut.context.ApplicationContext
import org.grails.datastore.mapping.mongo.MongoDatastore
import org.grails.datastore.mapping.validation.ValidationException
import org.testcontainers.containers.GenericContainer
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

import javax.validation.constraints.NotBlank

/**
 * @author graemerocher* @since 1.0
 */
class MongoDatastoreFactorySpec extends Specification {

    @Shared
    @AutoCleanup
    GenericContainer mongo =
            new GenericContainer("mongo:4.0")
                    .withExposedPorts(27017)

    @Shared
    @AutoCleanup
    ApplicationContext applicationContext

    def setupSpec() {
        mongo.start()
        applicationContext = ApplicationContext.build((MongoSettings.MONGODB_URI): "mongodb://${mongo.containerIpAddress}:${mongo.getMappedPort(27017)}")
                .mainClass(MongoDatastoreFactorySpec)
                .start()
    }

    @Rollback
    void "test configure GORM for MongoDB"() {
        when:
        new Team(name: "United").save(flush: true)

        then:
        Team.count() == old(Team.count()) + 1
        Team.last().name == "United"
        Team.last().dateCreated != null
    }

    @Rollback
    void "test validation errors"() {
        when:
        new Team(name: "").save(failOnError: true)

        then:
        thrown(ValidationException)
    }

    void "test MongoClient is available"() {
        expect:
        applicationContext.containsBean(MongoClient)
        applicationContext.getBean(MongoClient)
        applicationContext.getBean(MongoDatastore)
    }
}

@Entity
class Team {
    @NotBlank
    String name
    Date dateCreated
}
