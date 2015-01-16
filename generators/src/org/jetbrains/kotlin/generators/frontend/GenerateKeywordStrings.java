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

package org.jetbrains.kotlin.generators.frontend;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.generators.di.GeneratorsFileUtil;
import org.jetbrains.kotlin.lexer.JetKeywordToken;
import org.jetbrains.kotlin.lexer.JetTokens;
import org.jetbrains.kotlin.utils.Printer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GenerateKeywordStrings {
    public static final File DEST_FILE = new File("core/descriptors/src/org/jetbrains/kotlin/renderer/KeywordStringsGenerated.java");

    @NotNull
    public static String generate() throws IOException {
        StringBuilder sb = new StringBuilder();
        Printer p = new Printer(sb);

        p.println(FileUtil.loadFile(new File("generators/injector-generator/copyright.txt")));
        p.println("package org.jetbrains.kotlin.renderer;");
        p.println();
        p.println("import java.util.Arrays;");
        p.println("import java.util.Set;");
        p.println("import java.util.HashSet;");
        p.println();
        p.println("/** This class is generated by {@link \"org.jetbrains.kotlin.generators.frontend.GenerateKeywordStrings\"}. DO NOT MODIFY MANUALLY */");
        p.println("public class KeywordStringsGenerated {");
        p.pushIndent();
        p.println("private KeywordStringsGenerated() {}");
        p.println();
        p.println("public static final Set<String> KEYWORDS = new HashSet<String>(Arrays.asList(");
        p.pushIndent();

        List<String> strings = new ArrayList<String>();
        for (IElementType type : JetTokens.KEYWORDS.getTypes()) {
            assert type instanceof JetKeywordToken : "Not a keyword in JetTokens.KEYWORDS: " + type;
            JetKeywordToken keyword = (JetKeywordToken) type;
            assert !keyword.isSoft() : "Soft keyword in JetTokens.KEYWORDS: " + keyword.getValue();
            if (keyword != JetTokens.AS_SAFE && keyword != JetTokens.NOT_IN && keyword != JetTokens.NOT_IS) {
                strings.add(keyword.getValue());
            }
        }

        for (Iterator<String> iterator = strings.iterator(); iterator.hasNext(); ) {
            String string = iterator.next();
            p.println("\"" + string + "\"" + (iterator.hasNext() ? "," : ""));
        }

        p.popIndent();
        p.println("));");
        p.popIndent();
        p.println("}");

        return sb.toString();
    }

    public static void main(String[] args) throws IOException {
        GeneratorsFileUtil.writeFileIfContentChanged(DEST_FILE, generate());
    }
}
