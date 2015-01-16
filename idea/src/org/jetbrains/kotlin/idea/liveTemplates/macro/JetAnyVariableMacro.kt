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

package org.jetbrains.kotlin.idea.liveTemplates.macro

import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.idea.JetBundle
import org.jetbrains.kotlin.psi.JetExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.descriptors.ModuleDescriptor

public class JetAnyVariableMacro : BaseJetVariableMacro<Unit>() {
    override fun getName() = "kotlinVariable"

    override fun getPresentableName() = JetBundle.message("macro.variable.of.type")

    override fun initCache(project: Project, moduleDescriptor: ModuleDescriptor, bindingContext: BindingContext, position: JetExpression) {
    }

    override fun isSuitable(variableDescriptor: VariableDescriptor, cache: Unit): Boolean {
        return true
    }
}
