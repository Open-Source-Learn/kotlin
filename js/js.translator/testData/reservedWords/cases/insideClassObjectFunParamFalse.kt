package foo

// NOTE THIS FILE IS AUTO-GENERATED by the generateTestDataForReservedWords.kt. DO NOT EDIT!

class TestClass {
    class object {
        fun foo(`false`: String) {
    assertEquals("123", `false`)
    testRenamed("false", { `false` })
}

        fun test() {
            foo("123")
        }
    }
}

fun box(): String {
    TestClass.test()

    return "OK"
}