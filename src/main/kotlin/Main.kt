
import com.sun.tools.javac.Main
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import model.Point
import model.Polynomial
import model.TaskJson
import java.math.BigInteger
import java.security.SecureRandom

@OptIn(ExperimentalSerializationApi::class)
private fun loadTestData(): Array<Point> {
    val fileName = "secrets.json"
    val classLoader = Main::class.java.classLoader
    val resourceStream = classLoader.getResourceAsStream(fileName)
        ?: throw IllegalArgumentException("No filed named $fileName")

    return Json.decodeFromStream<TaskJson>(resourceStream).getShares()
}

fun main(args: Array<String>) {
    val secret = "hello there u mother fucker"
    val secretBigInt = Utils.messageToBigInteger(secret)

    val task = Cryptography.constructShares(secret, 5, 3)

    val subShares = task.getShares().filterIndexed {index, _ -> index in 1..3 }.toTypedArray()

    val reconstructedSecret = Cryptography.reconstructSecret(task.getField(), subShares)
    println("secret was: '$reconstructedSecret'")
}

fun testPoly() {
    val secret = BigInteger.valueOf(1234)

    val random = SecureRandom()
    val coefficients = (1 until 3).map { BigInteger(secret.bitLength(), random) }.toTypedArray()

    val poly = Polynomial(arrayOf(secret).plus(coefficients))

    val points = arrayOf(poly.calculate(1), poly.calculate(2), poly.calculate(3)).mapIndexed {i, b -> Point(i+1, b)}.toTypedArray()

    println(poly.toString())
    println("f(" + 1 + ") = " + points[0].y)
    println("f(" + 2 + ") = " + points[1].y)
    println("f(" + 3 + ") = " + points[2].y)
    println()
    println("f(" + 0 + ") = " + poly.calculate(0))
    println("f(" + 0 + ") = " + Cryptography.interpolatePolynomial(points, 0))
}