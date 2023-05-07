
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import model.SecretShares
import org.junit.jupiter.api.Test
import java.math.BigInteger
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
        val secret = Cryptography.reconstructSecretToBigInt(secretShares.getField(), shares)
        val expectedSecret = BigInteger("852354025062733139249869211569471773584941555886188619879846439859466991819838177232074815445973896836833615721977415635528038674548339024864115063221528470921809535225986307655394642160551551220062420627699112194609778851541071901592735542083071737623578833068699586558919994147274269087389745478344797728006422415960247595649997668723939952906185028300881967343001027476879955852687962313706732633407600190197483694192037747659182665573220309395143061752225271873842084706978976653841753202844615281285862683555908677970118115636888817938533574272398794991122243772799049680591059650643681364789297676938408363821689521868570643559338185581330466157268004322232980023929250358031355316360229749754782567530339901429536098145080263")

        // ASSERT
        assertEquals(expectedSecret, secret)
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

