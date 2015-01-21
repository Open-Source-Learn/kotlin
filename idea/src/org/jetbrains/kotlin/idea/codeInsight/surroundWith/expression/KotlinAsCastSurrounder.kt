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

package org.jetbrains.kotlin.idea.codeInsight.surroundWith.expression

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import org.jetbrains.kotlin.psi.JetExpression
import org.jetbrains.kotlin.psi.JetPsiFactory
import org.jetbrains.kotlin.psi.JetBinaryExpressionWithTypeRHS
import org.jetbrains.kotlin.psi.JetParenthesizedExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.idea.JetBundle
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.builtins.KotlinBuiltIns

public class KotlinAsCastSurrounder: KotlinExpressionSurrounder() {

    override fun isApplicable(expression: JetExpression): Boolean {
        if (!expression.isPhysical()) return false

        val context = expression.analyze()
        val type = context[BindingContext.EXPRESSION_TYPE, expression]
        return type != null && KotlinBuiltIns.getInstance().getUnit() != type
    }

    override fun surroundExpression(project: Project, editor: Editor, expression: JetExpression): TextRange? {
        val factory = JetPsiFactory(project)

        val parentCast = factory.createExpression("(expr as )") as JetParenthesizedExpression
        val cast = parentCast.getExpression() as JetBinaryExpressionWithTypeRHS
        cast.getLeft().replace(expression)

        val newExpr = expression.replace(parentCast) as JetParenthesizedExpression
        val newCast = newExpr.getExpression() as JetBinaryExpressionWithTypeRHS
        return newCast.getRight().getTextRange()
    }

    override fun getTemplateDescription(): String {
        return JetBundle.message("surround.with.as.cast.template")
    }
}
