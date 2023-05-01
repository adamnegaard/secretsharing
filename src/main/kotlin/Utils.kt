import java.math.BigInteger

object Utils {

    private val base = 16

    fun bitStringToBigInteger(bitString: String?): BigInteger {
        if (bitString == null) throw IllegalArgumentException("Invalid bit string")

        return BigInteger(bitString, base)
    }

    fun bigIntegerToBitString(bigInteger: BigInteger): String {
        return bigInteger.toString(base)
    }
}