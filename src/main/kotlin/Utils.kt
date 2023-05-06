import java.math.BigInteger
import java.util.*


object Utils {

    private val base = 16

    fun messageToBigInteger(message: String): BigInteger {
        val hex = HexFormat.of().formatHex(message.toByteArray())

        return hexToBigInteger(hex)
    }

    fun bigIntegerToMessage(bigInteger: BigInteger): String {
        var hex = bigIntegerToHex(bigInteger)
        hex = hex.substring(0, hex.length - 1)

        return String(HexFormat.of().parseHex(hex))
    }

    fun hexToBigInteger(hex: String?): BigInteger {
        if (hex == null) throw IllegalArgumentException("Invalid hex string")

        return BigInteger(hex, base)
    }

    fun bigIntegerToHex(bigInteger: BigInteger): String {
        return bigInteger.toString(base)
    }
}