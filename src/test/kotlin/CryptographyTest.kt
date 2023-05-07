
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import model.TaskJson
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class CryptographyTest {

    @OptIn(ExperimentalSerializationApi::class)
    private fun loadTestData(): TaskJson {
        val fileName = "secrets.json"
        val classLoader = CryptographyTest::class.java.classLoader
        val resourceStream = classLoader.getResourceAsStream(fileName)
            ?: throw IllegalArgumentException("No filed named $fileName")

        return Json.decodeFromStream<TaskJson>(resourceStream)
    }

    @Test
    fun test_data_reconstructs_message() {
        // ARRANGE
        val taskJson = loadTestData()
        val shares = taskJson.getShares()

        // ACT
        val secret = Cryptography.reconstructSecret(taskJson.getField(), shares)

        // ASSERT
        println(secret)
        assertEquals(1, 1)
    }

    @Test
    fun try_all_combinations_of_threshold() {
        // ARRANGE
        val message = "blaaaa"
        val amountOfShares = 5
        val threshold = 3
        val taskJson = Cryptography.constructShares(message, amountOfShares, threshold)

        // ACT & ASSERT
        tryAllCombinations(message, amountOfShares, threshold, taskJson)
    }

    private fun tryAllCombinations(message: String, amountOfShares: Int, threshold: Int, taskJson: TaskJson) {
        val shares = taskJson.getShares()

        for (i in 0 until amountOfShares) {
            val shareI = shares[i]

            for (j in 0 until amountOfShares ) {
                if (i == j) continue
                val shareJ = shares[j]

                for (k in 0 until amountOfShares - 2) {
                    if (i == k || j == k) continue

                    val shareK = shares[k]

                    println("Verifying with i = $i, j = $j and k = $k")

                    val subShares = arrayOf(shareI, shareJ, shareK)

                    val reconstructedMessage = Cryptography.reconstructSecret(taskJson.getField(), subShares)
                    assertEquals(message, reconstructedMessage)
                }
            }
        }
    }
}

