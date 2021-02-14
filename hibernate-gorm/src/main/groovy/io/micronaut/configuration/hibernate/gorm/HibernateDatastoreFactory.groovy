/*
 * Copyright 2017-2020 original authors
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
package io.micronaut.configuration.hibernate.gorm

import grails.gorm.annotation.Entity
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.micronaut.configuration.gorm.configuration.GormPropertyResolverAdapter
import io.micronaut.configuration.gorm.event.ConfigurableEventPublisherAdapter
import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Context
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Primary
import io.micronaut.inject.qualifiers.Qualifiers
import org.grails.datastore.mapping.services.Service
import org.grails.orm.hibernate.HibernateDatastore
import org.grails.orm.hibernate.connections.HibernateConnectionSource
import org.hibernate.SessionFactory
import org.springframework.transaction.PlatformTransactionManager

import javax.sql.DataSource
import java.util.stream.Stream

/**
 * <p>A factory for configuring GORM for Hibernate 5 within Micronaut</p>.
 *
 * @author Graeme Rocher
 * @since 1.0
 */
@Factory
@CompileStatic
@Slf4j
class HibernateDatastoreFactory {

    final ApplicationContext applicationContext

    HibernateDatastoreFactory(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext
    }

    @Bean(preDestroy = "close")
    @Context
    HibernateDatastore hibernateDatastore() {
        log.info("Starting GORM for Hibernate")
        Stream<Class> entities = applicationContext.environment.scan(Entity)
        Class[] classes = entities.toArray() as Class[]
        HibernateDatastore datastore = new HibernateDatastore(
            new GormPropertyResolverAdapter(applicationContext, applicationContext),
            new ConfigurableEventPublisherAdapter(applicationContext),
            classes
        )
        SessionFactory sessionFactory = datastore.sessionFactory
        DataSource dataSource = ((HibernateConnectionSource) datastore.getConnectionSources().defaultConnectionSource).getDataSource()
        PlatformTransactionManager platformTransactionManager = datastore.getTransactionManager()
        applicationContext.registerSingleton(SessionFactory, sessionFactory)
        applicationContext.registerSingleton(DataSource, dataSource)
        applicationContext.registerSingleton(PlatformTransactionManager, platformTransactionManager, Qualifiers.byName("hibernate"))
        applicationContext.registerSingleton(PlatformTransactionManager, platformTransactionManager, Qualifiers.byStereotype(Primary))

        Collection<Collection<Service>> gormServices = datastore.getServices().split {
            !it.class.isAnnotationPresent(grails.gorm.services.Service)
        }
        for(services in gormServices) {
            for (o in services) {
                applicationContext.registerSingleton(o, false)
            }
            for (o in services) {
                applicationContext.inject(o)
            }
        }

        return datastore
    }

}
