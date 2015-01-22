/*
 * Copyright 2010-2015 JetBrains s.r.o.
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

package org.jetbrains.kotlin.resolve

import org.jetbrains.annotations.ReadOnly

import java.util.Collections
import kotlin.properties.Delegates
import org.jetbrains.kotlin.resolve.calls.checkers.*

private val DEFAULT_CALL_CHECKERS = listOf(CapturingInClosureChecker(), InlineCheckerWrapper(), ReifiedTypeParameterSubstitutionChecker())

public abstract class AdditionalCheckerProvider(
        public val annotationCheckers: List<AnnotationChecker>,
        additionalCallCheckers: List<CallChecker>
) {

    public val callCheckers: List<CallChecker> = DEFAULT_CALL_CHECKERS + additionalCallCheckers

    public object DefaultProvider : AdditionalCheckerProvider(listOf(), listOf()) {}
}
