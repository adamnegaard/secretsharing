
import com.sun.tools.javac.Main
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import model.Point
import model.Polynomial
import model.TaskJson
import java.math.BigInteger

@OptIn(ExperimentalSerializationApi::class)
private fun loadTestData(): Array<Point> {
    val fileName = "secrets.json"
    val classLoader = Main::class.java.classLoader
    val resourceStream = classLoader.getResourceAsStream(fileName)
        ?: throw IllegalArgumentException("No filed named $fileName")

    return Json.decodeFromStream<TaskJson>(resourceStream).getShares()
}

fun main(args: Array<String>) {
    val shares = loadTestData()

    val point = Point(BigInteger.valueOf(10), BigInteger.valueOf(100))
    val polynomial = Polynomial(arrayOf(BigInteger.ONE, BigInteger.TWO, BigInteger.TEN))

}