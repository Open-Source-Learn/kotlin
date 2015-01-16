/*
 * Copyright 2010-2013 JetBrains s.r.o.
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

package org.jetbrains.jet.plugin.quickfix;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.TokenType;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.diagnostics.Diagnostic;
import org.jetbrains.kotlin.psi.JetFile;
import org.jetbrains.kotlin.psi.JetParameter;
import org.jetbrains.kotlin.psi.JetPsiUtil;
import org.jetbrains.kotlin.plugin.JetBundle;

public class RemoveValVarFromParameterFix extends JetIntentionAction<JetParameter> {
    private final String varOrVal;

    public RemoveValVarFromParameterFix(@NotNull JetParameter element) {
        super(element);
        ASTNode valOrVarNode = element.getValOrVarNode();
        assert valOrVarNode != null : "Val or var node not found for " + JetPsiUtil.getElementTextWithContext(element);
        varOrVal = valOrVarNode.getText();
    }

    @NotNull
    @Override
    public String getText() {
        return JetBundle.message("remove.val.var.from.parameter", varOrVal);
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return JetBundle.message("remove.val.var.from.parameter", "val/var");
    }

    @Override
    protected void invoke(@NotNull Project project, Editor editor, JetFile file) throws IncorrectOperationException {
        ASTNode valOrVarNode = element.getValOrVarNode();
        if (valOrVarNode == null) return;

        ASTNode next = valOrVarNode.getTreeNext();
        ASTNode firstNodeToKeep = next.getElementType() == TokenType.WHITE_SPACE ? next.getTreeNext() : next;
        element.getNode().removeRange(valOrVarNode, firstNodeToKeep);
    }


    public static JetSingleIntentionActionFactory createFactory() {
        return new JetSingleIntentionActionFactory() {
            @Nullable
            @Override
            public IntentionAction createAction(@NotNull Diagnostic diagnostic) {
                return new RemoveValVarFromParameterFix((JetParameter) diagnostic.getPsiElement().getParent());
            }
        };
    }
}
