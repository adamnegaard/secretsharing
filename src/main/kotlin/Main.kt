
import com.sun.tools.javac.Main
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import model.Point
import model.TaskJson

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

    val shares = task.getShares()
    val subShares = task.getShares().filterIndexed {index, _ -> index in 1..3 }.toTypedArray()

    val reconstructedSecret = Cryptography.reconstructSecret(task.getField(), subShares)
    println("secret was: '$reconstructedSecret'")
}