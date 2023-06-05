/*
 * Copyright 2017-2023 original authors
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
package io.micronaut.function.groovy;

import io.micronaut.function.FunctionBean;
import io.micronaut.http.annotation.Body;
import io.micronaut.inject.ast.ClassElement;
import io.micronaut.inject.ast.ElementQuery;
import io.micronaut.inject.ast.ParameterElement;
import io.micronaut.inject.visitor.TypeElementVisitor;
import io.micronaut.inject.visitor.VisitorContext;

public class FunctionTransformVisitor implements TypeElementVisitor<FunctionBean, Object> {
    @Override
    public void visitClass(ClassElement element, VisitorContext context) {
        element.getAnnotationMetadata().stringValue(FunctionBean.class, "method").ifPresent(m ->
            element.getEnclosedElement(ElementQuery.ALL_METHODS.onlyInstance().named(methodName -> methodName.equals(m)))
                .ifPresent(method -> {
                    if (method.hasParameters()) {
                        ParameterElement[] parameters = method.getParameters();
                        if (parameters.length == 1) {
                            parameters[0].annotate(Body.class);
                        }
                    }
                })
        );
    }
}
