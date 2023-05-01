import model.Point
import java.math.BigInteger
import java.security.SecureRandom

object Cryptography {

    fun generateShares(secret: BigInteger, threshold: Int, numShares: Int): Map<Int, BigInteger> {
        val random = SecureRandom()
        val prime = BigInteger.probablePrime(secret.bitLength() + 1, random)
        val coeffecients = (1 until threshold).map { BigInteger(secret.bitLength(), random) }.toTypedArray()

        val shares = mutableMapOf<Int, BigInteger>()
        for (i in 1..numShares) {
            val x = BigInteger.valueOf(i.toLong())
            var y = secret
            for (j in 0 until threshold - 1) {
                y += coeffecients[j] * x.modPow(BigInteger.valueOf(j + 1L), prime)
            }
            shares[i] = y.mod(prime)
        }

        return shares
    }

    fun reconstructSecret(shares: Array<Point>): BigInteger {
        var secret = BigInteger.ZERO

        for ((shareIndexI, shareValueI) in shares) {

            var numerator = BigInteger.ONE
            var denominator = BigInteger.ONE

            for ((shareIndexJ, shareValueJ) in shares) {
                if (shareIndexI == shareIndexJ) continue

                numerator = numerator.multiply(shareValueJ.negate())
                denominator = denominator.multiply(shareValueI.minus(shareValueJ))
            }
            secret += secret.plus(shareValueI.multiply(numerator.multiply(denominator.modInverse(shareValueI))))
        }

        return secret
    }


}