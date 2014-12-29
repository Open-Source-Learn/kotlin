/*
 * Copyright 2010-2014 JetBrains s.r.o.
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

package org.jetbrains.kotlin.utils.serializer

import com.intellij.openapi.Disposable
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.io.FileUtil
import com.intellij.psi.search.GlobalSearchScope
import java.io.ByteArrayOutputStream
import java.io.File
import javax.xml.bind.DatatypeConverter.printBase64Binary
import kotlin.platform.platformStatic
import org.jetbrains.kotlin.builtins.BuiltInsSerializationUtil
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.load.kotlin.DeserializedResolverUtils
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.JetFile
import org.jetbrains.kotlin.resolve.scopes.DescriptorKindFilter
import org.jetbrains.kotlin.serialization.AnnotationSerializer
import org.jetbrains.kotlin.serialization.builtins.BuiltInsProtoBuf
import org.jetbrains.kotlin.serialization.DescriptorSerializer
import org.jetbrains.kotlin.serialization.NameSerializationUtil
import org.jetbrains.kotlin.serialization.ProtoBuf
import org.jetbrains.kotlin.serialization.SerializerExtension
import org.jetbrains.kotlin.serialization.StringTable
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.utils.LibraryUtils

private object BuiltInsSerializerExtension : SerializerExtension() {
    override fun serializeClass(descriptor: ClassDescriptor, proto: ProtoBuf.Class.Builder, stringTable: StringTable) {
        for (annotation in descriptor.getAnnotations()) {
            proto.addExtension(BuiltInsProtoBuf.classAnnotation, AnnotationSerializer.serializeAnnotation(annotation, stringTable))
        }
    }

    override fun serializePackage(
            packageFragments: Collection<PackageFragmentDescriptor>,
            proto: ProtoBuf.Package.Builder,
            stringTable: StringTable
    ) {
        val classes = packageFragments.flatMap {
            it.getMemberScope().getDescriptors(DescriptorKindFilter.CLASSIFIERS).filterIsInstance<ClassDescriptor>()
        }

        for (descriptor in DescriptorSerializer.sort(classes)) {
            proto.addExtension(BuiltInsProtoBuf.className, stringTable.getSimpleNameIndex(descriptor.getName()))
        }
    }

    override fun serializeCallable(
            callable: CallableMemberDescriptor,
            proto: ProtoBuf.Callable.Builder,
            stringTable: StringTable
    ) {
        for (annotation in callable.getAnnotations()) {
            proto.addExtension(BuiltInsProtoBuf.callableAnnotation, AnnotationSerializer.serializeAnnotation(annotation, stringTable))
        }
    }

    override fun serializeValueParameter(
            descriptor: ValueParameterDescriptor,
            proto: ProtoBuf.Callable.ValueParameter.Builder,
            stringTable: StringTable
    ) {
        for (annotation in descriptor.getAnnotations()) {
            proto.addExtension(BuiltInsProtoBuf.parameterAnnotation, AnnotationSerializer.serializeAnnotation(annotation, stringTable))
        }
    }
}

public class Serializer() {

    public fun serialize(moduleName: String, moduleDescriptor: ModuleDescriptor, files: List<JetFile>, metaFile: File) {
        serialize(moduleDescriptor, files) {
            (packageSet, content) -> LibraryUtils.writeMetadata(moduleName, packageSet, content, metaFile)
        }
    }

    public fun serialize(moduleDescriptor: ModuleDescriptor, files: List<JetFile>, saveFun: (Set<String>, Map<String, ByteArray>) -> Unit) {
        val packageSet = hashSetOf<String>()
        val map = hashMapOf<String, ByteArray>()
        serialize(moduleDescriptor, files, { packageSet.add(it.asString()) }) {
            (fileName, stream) -> map[fileName] = stream.toByteArray()
        }
        saveFun(packageSet, map)
    }

    public fun serialize(
            moduleDescriptor: ModuleDescriptor,
            files: List<JetFile>,
            packageFun: (FqName) -> Unit,
            writeFun: (String, ByteArrayOutputStream) -> Unit
    ) {
        val packageSet = hashSetOf<FqName>()
        for( file in files) {
            var fqName = file.getPackageFqName()
            packageSet.add(fqName)
            while(!fqName.isRoot()) {
                packageSet.add(fqName)
                fqName = fqName.parent()
            }
        }

        packageSet.forEach { fqName ->
            packageFun(fqName)
            serializePackage(moduleDescriptor, fqName, writeFun)
        }
    }

    fun serializePackage(module: ModuleDescriptor, fqName: FqName, writeFun: (String, ByteArrayOutputStream) -> Unit) {
        val packageView = module.getPackage(fqName) ?: error("No package resolved in $module")

        // TODO: perform some kind of validation? At the moment not possible because DescriptorValidator is in compiler-tests
        // DescriptorValidator.validate(packageView)

        val skip: (DeclarationDescriptor) -> Boolean = { DescriptorUtils.getContainingModule(it) != module} //{ isBuiltin(it) }

        val serializer = DescriptorSerializer.createTopLevel(BuiltInsSerializerExtension)

        val classifierDescriptors = DescriptorSerializer.sort(packageView.getMemberScope().getDescriptors(DescriptorKindFilter.CLASSIFIERS))

        ClassSerializationUtil.serializeClasses(classifierDescriptors, serializer, object : ClassSerializationUtil.Sink {
            override fun writeClass(classDescriptor: ClassDescriptor, classProto: ProtoBuf.Class) {
                val stream = ByteArrayOutputStream()
                classProto.writeTo(stream)
                writeFun(getFileName(classDescriptor), stream)
            }
        }, skip)

        val packageStream = ByteArrayOutputStream()
        val fragments = module.getPackageFragmentProvider().getPackageFragments(fqName)
        val packageProto = serializer.packageProto(fragments, skip).build() ?: error("Package fragments not serialized: $fragments")
        packageProto.writeTo(packageStream)
        writeFun(BuiltInsSerializationUtil.getPackageFilePath(fqName), packageStream)

        val nameStream = ByteArrayOutputStream()
        NameSerializationUtil.serializeStringTable(nameStream, serializer.getStringTable())
        writeFun(BuiltInsSerializationUtil.getStringTableFilePath(fqName), nameStream)
    }

    fun getFileName(classDescriptor: ClassDescriptor): String {
        return BuiltInsSerializationUtil.getClassMetadataPath(DeserializedResolverUtils.getClassId(classDescriptor))!!
    }
}
