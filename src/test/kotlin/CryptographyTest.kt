
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
    fun name() {
        val taskJson = loadTestData()

        val shares = taskJson.getShares()
        val secret = Cryptography.reconstructSecret(taskJson.getField(), shares)

        assertEquals(1, 1)
    }
}

