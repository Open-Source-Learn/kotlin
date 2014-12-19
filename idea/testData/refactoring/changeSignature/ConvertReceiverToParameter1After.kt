fun <caret>foo(xx: X, s: String, k: Int): Boolean {
    return xx.k + s.length() - k > 0
}

class X(val k: Int)

fun <T, R> with(receiver: T, f: T.() -> R): R = receiver.f()

fun test() {
    foo(X(0), "1", 2)
    with(X(0)) {
        foo(this, "1", 2)
    }
}