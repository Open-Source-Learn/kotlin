package kotlinPackage

import testProject.kotlinInJavaRoot.src.main.java.javaPackage.*
import testProject.kotlinInJavaRoot.src.main.java.javaPackage2.*

public class KotlinClass() {
    fun foo() {}
}

fun main() {
    JavaClass().foo()
    JavaClass2().foo()
}

