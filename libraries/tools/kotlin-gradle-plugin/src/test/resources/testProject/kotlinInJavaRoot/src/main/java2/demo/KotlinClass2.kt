package kotlinPackage2

import testProject.kotlinInJavaRoot.src.main.java.javaPackage.*
import testProject.kotlinInJavaRoot.src.main.java.javaPackage2.*

public class KotlinClass2() {
    fun foo() {}
}

fun main() {
    JavaClass().foo()
    JavaClass2().foo()
}

