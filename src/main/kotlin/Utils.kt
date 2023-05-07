
import java.math.BigInteger
import java.util.*


object Utils {

    private val base = 16

    fun messageToBigInteger(message: String): BigInteger {
        val hex = HexFormat.of().formatHex(message.toByteArray(), )

        return hexToBigInteger(hex)
    }

    fun bigIntegerToMessage(bigInteger: BigInteger): String {
        val hex = bigIntegerToHex(bigInteger)

        return String(HexFormat.of().parseHex(hex))
    }

    fun hexToBigInteger(hex: String?): BigInteger {
        if (hex == null) throw IllegalArgumentException("Invalid hex string")

        return BigInteger(hex, base)
    }

    fun bigIntegerToHex(bigInteger: BigInteger): String {
        val string = bigInteger.toString(base)

        return String(string.toByteArray())
    }
}