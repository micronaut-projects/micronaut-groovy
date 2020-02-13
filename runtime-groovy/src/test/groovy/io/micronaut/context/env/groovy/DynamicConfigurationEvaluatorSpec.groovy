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
package io.micronaut.context.env.groovy

import io.micronaut.context.env.Environment
import io.micronaut.context.env.PropertySource
import io.micronaut.context.exceptions.ConfigurationException
import io.micronaut.core.io.ResourceLoader
import spock.lang.Specification
import spock.util.environment.RestoreSystemProperties

class DynamicConfigurationEvaluatorSpec extends Specification {

    @RestoreSystemProperties
    void "test groovy property source loader with dynamic configuration"() {
        given:
        System.setProperty(ConfigurationEvaluator.COMPILE_STATIC_PROPERTY, "false")
        GroovyPropertySourceLoader loader = new GroovyPropertySourceLoader() {
            @Override
            protected Optional<InputStream> readInput(ResourceLoader resourceLoader, String fileName) {
                return Optional.of(new ByteArrayInputStream('''
mapping = {
    id generator: 'identity'
}
dataSource.something = [1,2]
'''.bytes))
            }
        }

        when:
        Environment env = Mock(Environment)
        env.isPresent(_) >> true
        env.getActiveNames() >> ([] as Set)

        def result = loader.load(env)

        then:
        noExceptionThrown()
        result

        when:
        PropertySource propertySource = result.get()

        then:
        propertySource.get("dataSource.something") == [1,2]

    }

    void "test groovy property source loader with dynamic configuration should throw exception"() {
        given:
        GroovyPropertySourceLoader loader = new GroovyPropertySourceLoader() {
            @Override
            protected Optional<InputStream> readInput(ResourceLoader resourceLoader, String fileName) {
                return Optional.of(new ByteArrayInputStream('''
mapping = {
    id generator: 'identity'
}
'''.bytes))
            }
        }

        when:
        Environment env = Mock(Environment)
        env.isPresent(_) >> true
        env.getActiveNames() >> ([] as Set)

        def result = loader.load(env)

        then:
        thrown(ConfigurationException)

    }

    @RestoreSystemProperties
    void "test groovy property source loader with some invalid value does not break application"() {
        given:
        System.setProperty(ConfigurationEvaluator.COMPILE_STATIC_PROPERTY, "invalid")
        GroovyPropertySourceLoader loader = new GroovyPropertySourceLoader() {
            @Override
            protected Optional<InputStream> readInput(ResourceLoader resourceLoader, String fileName) {
                return Optional.of(new ByteArrayInputStream('''
dataSource.something = [1,2]
'''.bytes))
            }
        }

        when:
        Environment env = Mock(Environment)
        env.isPresent(_) >> true
        env.getActiveNames() >> ([] as Set)

        def result = loader.load(env)

        then:
        noExceptionThrown()
        result

        when:
        PropertySource propertySource = result.get()

        then:
        propertySource.get("dataSource.something") == [1,2]

    }
}
