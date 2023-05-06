
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
    val secret = "hello there"
    val secretBit = Utils.messageToBigInteger(secret)

    val task = Cryptography.constructShares(secret, 5, 3)

    println("f(" + 0 + ") = " + Cryptography.interpolatePolynomial(task.getShares().filterIndexed {index, _ -> index in 0..4 }.toTypedArray(), 0))
    println(secretBit)
}

fun testPoly() {
    val secret = BigInteger.valueOf(1234)

    val random = SecureRandom()
    val coefficients = (1 until 3).map { BigInteger(secret.bitLength(), random) }.toTypedArray()

    val poly = Polynomial(arrayOf(secret).plus(coefficients))

    println(poly.toString())
    println("f(" + 3 + ") = " + poly.calculate(3))
}