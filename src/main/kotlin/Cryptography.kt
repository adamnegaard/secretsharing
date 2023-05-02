import model.Point
import model.TaskJson
import java.security.SecureRandom;
import java.math.BigInteger

object Cryptography {

    fun constructShares(secret: String, amountOfShares: Int, threshold: Int): TaskJson {

        val numSecret = Utils.bitStringToBigInteger(secret)

        val random = SecureRandom()
        val prime = BigInteger.probablePrime(numSecret.bitLength() + 1, random)

        val coefficients = (1 until threshold).map { BigInteger(numSecret.bitLength(), random) }.toTypedArray()

        val shares = mutableListOf<Point>()
        for (x in 0 until amountOfShares) {

            val bigX = BigInteger.valueOf(x.toLong())
            var y = numSecret

            for (i in 0 until threshold - 1) {
                val bigI = BigInteger.valueOf(i.toLong())
                y = y.plus(coefficients[i].times(bigX.modPow(bigI.plus(BigInteger.ONE), prime)))
            }

            shares.add(Point(x, y))
        }

        return TaskJson.ofValues(prime, shares)
    }

    fun reconstructSecret(prime: BigInteger, shares: Array<Point>): BigInteger {
        var secret = BigInteger.ZERO

        for ((i, share) in shares) {
            var currSecret = share
            val bigI = BigInteger.valueOf(i.toLong())

            var li = BigInteger.ONE

            for ((j, _) in shares) {
                if (i == j) continue

                val bigJ = BigInteger.valueOf(j.toLong())

                val sub = bigJ.subtract(bigI)
                li = li.multiply(sub).abs().modInverse(prime).multiply(bigJ).mod(prime)
                currSecret = currSecret.multiply(bigJ.subtract(bigI)).mod(prime)

            }

            secret = secret.add(currSecret.multiply(li)).mod(prime)
        }

        return secret
        /*
        var secret = BigInteger.ZERO

        for((i, share) in shares) {

            var numerator = BigInteger.ONE
            var denominator = BigInteger.ONE

            for((j, _) in shares) {
                if (i == j) continue;

                numerator = numerator.multiply(BigInteger.valueOf(-j.toLong()))
                denominator = denominator.multiply(BigInteger.valueOf((j - i).toLong()))

            }

            val here = denominator.modInverse(share)
            secret = secret.plus(share.times(numerator).times(here))

        }

        return secret

         */
    }

}


