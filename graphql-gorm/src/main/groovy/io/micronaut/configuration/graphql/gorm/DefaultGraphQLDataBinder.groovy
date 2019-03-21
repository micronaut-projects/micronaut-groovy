/*
 * Copyright 2017-2018 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.micronaut.configuration.graphql.gorm

import io.micronaut.core.bind.BeanPropertyBinder
import org.grails.gorm.graphql.binding.GraphQLDataBinder

/**
 * Default data binder that delegates to {@link BeanPropertyBinder}.
 *
 * @author James Kleeh
 * @since 1.1.0
 */
class DefaultGraphQLDataBinder implements GraphQLDataBinder {

    private final BeanPropertyBinder beanPropertyBinder

    /**
     * Default constructor.
     *
     * @param beanPropertyBinder The bean property binder
     */
    DefaultGraphQLDataBinder(BeanPropertyBinder beanPropertyBinder) {
        this.beanPropertyBinder = beanPropertyBinder
    }

    @Override
    void bind(Object object, Map data) {
        beanPropertyBinder.bind(object, data)
    }
}
