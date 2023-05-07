
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import model.SecretShares
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class CryptographyTest {

    @OptIn(ExperimentalSerializationApi::class)
    private fun loadTestData(): SecretShares {
        val fileName = "secrets.json"
        val classLoader = CryptographyTest::class.java.classLoader
        val resourceStream = classLoader.getResourceAsStream(fileName)
            ?: throw IllegalArgumentException("No filed named $fileName")

        return Json.decodeFromStream<SecretShares>(resourceStream)
    }

    @Test
    fun provided_shares_and_field_reconstructs_message() {
        // ARRANGE
        val secretShares = loadTestData()
        val shares = secretShares.getShares()

        // ACT
        val secret = Cryptography.reconstructSecret(secretShares.getField(), shares)

        // ASSERT
        println(secret)
        assertEquals(1, 1)
    }

    @Test
    fun all_shares_constructs_correct_message() {
        // ARRANGE
        val message = "Very very secret message"

        val secretShares = Cryptography.constructShares(message, 5, 3)

        // ACT
        val reconstructedMessage = Cryptography.reconstructSecret(secretShares.getField(), secretShares.getShares())

        // ASSERT
        assertEquals(message, reconstructedMessage)

    }

    @Test
    fun all_combinations_of_treshold_shares_constructs_message() {
        // ARRANGE
        val message = "Not so secret message"
        val amountOfShares = 5
        val threshold = 3

        val secretShares = Cryptography.constructShares(message, amountOfShares, threshold)

        // ACT & ASSERT
        tryAllCombinationsOfThreshold3(message, amountOfShares, secretShares)
    }

    private fun tryAllCombinationsOfThreshold3(message: String, amountOfShares: Int, secretShares: SecretShares) {
        val shares = secretShares.getShares()

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

                    val reconstructedMessage = Cryptography.reconstructSecret(secretShares.getField(), subShares)
                    assertEquals(message, reconstructedMessage)
                }
            }
        }
    }
}

