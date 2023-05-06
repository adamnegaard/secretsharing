import model.Point
import model.Polynomial
import model.TaskJson
import java.security.SecureRandom;
import java.math.BigInteger

object Cryptography {

    fun constructShares(secret: String, amountOfShares: Int, threshold: Int): TaskJson {
        val numSecret = Utils.messageToBigInteger(secret)

        val random = SecureRandom()
        val prime = BigInteger.probablePrime(numSecret.bitLength() + 1, random)

        val coefficients = (1 until threshold).map { BigInteger(numSecret.bitLength(), random) }.toTypedArray()
        val poly = Polynomial(arrayOf(numSecret).plus(coefficients))

        val shares = mutableListOf<Point>()
        for (x in 1 until amountOfShares + 1) {

            val y = poly.calculate(x)

            shares.add(Point(x, y))
        }

        return TaskJson.ofValues(prime, shares)
    }

    fun reconstructSecret(prime: BigInteger, shares: Array<Point>): String {
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

        return Utils.bigIntegerToMessage(secret)
    }

    fun interpolatePolynomial(points: Array<Point>, xValue: Int): BigInteger {
        var result = BigInteger.ZERO
        for (i in points.indices) {
            var term = points[i].y
            for (j in points.indices) {
                if (i != j) {
                    val numerator = BigInteger.valueOf((xValue - points[j].x).toLong())
                    val denominator = BigInteger.valueOf((points[i].x - points[j].x).toLong())
                    term = term.multiply(numerator.divide(denominator))
                }
            }
            result = result.plus(term)
        }
        return result
    }


}


