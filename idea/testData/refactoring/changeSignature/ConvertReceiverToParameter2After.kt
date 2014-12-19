fun <caret>foo(s: String, xx: X, k: Int): Boolean {
    return xx.k + s.length() - k > 0
}

class X(val k: Int)

fun <T, R> with(receiver: T, f: T.() -> R): R = receiver.f()

fun test() {
    foo("1", X(0), 2)
    with(X(0)) {
        foo("1", this, 2)
    }
}