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

package kotlin.reflect.jvm.internal

import java.lang.reflect.*
import kotlin.reflect.*
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.name.Name

// TODO: properties of built-in classes

open class KMemberPropertyImpl<T : Any, out R>(
        override val name: String,
        protected val owner: KClassImpl<T>
) : KMemberProperty<T, R>, KPropertyImpl<R> {
    val descriptor: PropertyDescriptor by Delegates.lazySoft {
        val properties = owner.descriptor.getDefaultType().getMemberScope().getProperties(Name.identifier(name))
        // TODO: handle cases when many or no properties are found
        properties.single() as PropertyDescriptor
    }

    // TODO: extract, use our descriptors knowledge
    override val field: Field? by Delegates.lazySoft {
        try {
            owner.jClass.getDeclaredField(name)
        }
        catch (e: NoSuchFieldException) {
            null
        }
    }

    override val getter: Method? by Delegates.lazySoft {
        try {
            println("property $name\nnaiveGetterName ${naiveGetterName(name)}\ngetterName ${getterName(descriptor)}\ndescriptor $descriptor\n")
            owner.jClass.getMaybeDeclaredMethod(getterName(descriptor))
        }
        catch (e: NoSuchMethodException) {
            null
        }
    }

    ;{
        // TODO: this compromises laziness, consider not doing it on creation and instead support isValid()
        if (field == null && getter == null) throw NoSuchPropertyException()
    }

    override fun get(receiver: T): R {
        try {
            val getter = getter
            return (if (getter != null) getter(receiver) else field!!.get(receiver)) as R
        }
        catch (e: java.lang.IllegalAccessException) {
            throw kotlin.reflect.IllegalAccessException(e)
        }
    }

    override fun equals(other: Any?): Boolean =
            other is KMemberPropertyImpl<*, *> && name == other.name && owner == other.owner

    override fun hashCode(): Int =
            name.hashCode() * 31 + owner.hashCode()

    // TODO: include visibility, return type
    override fun toString(): String =
            "val ${owner.jClass.getName()}.$name"
}

class KMutableMemberPropertyImpl<T : Any, R>(
        name: String,
        owner: KClassImpl<T>
) : KMutableMemberProperty<T, R>, KMutablePropertyImpl<R>, KMemberPropertyImpl<T, R>(name, owner) {
    override val setter: Method? by Delegates.lazySoft {
        try {
            val getter = getter
            val returnType = if (getter != null) getter.getReturnType() else field!!.getType()
            owner.jClass.getMaybeDeclaredMethod(naiveSetterName(name), returnType)
        }
        catch (e: NoSuchMethodException) {
            null
        }
    }

    ;{
        if (field == null && setter == null) throw NoSuchPropertyException()
    }

    override fun set(receiver: T, value: R) {
        try {
            val setter = setter
            if (setter != null) setter(receiver, value) else field!!.set(receiver, value)
        }
        catch (e: java.lang.IllegalAccessException) {
            throw kotlin.reflect.IllegalAccessException(e)
        }
    }

    override fun toString(): String =
            "var ${owner.jClass.getName()}.$name"
}
