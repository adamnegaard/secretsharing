
import model.Point
import model.Polynomial
import model.TaskJson
import java.math.BigDecimal
import java.math.BigInteger
import java.security.SecureRandom

object Cryptography {

    fun constructShares(secret: String, amountOfShares: Int, threshold: Int): TaskJson {
        val numSecret = Utils.messageToBigInteger(secret)

        val random = SecureRandom()
        val prime = BigInteger.probablePrime(numSecret.bitLength() + 1, random)

        val coefficients = (1 until threshold).map { BigInteger(numSecret.bitLength(), random) }.toTypedArray()
        val poly = Polynomial(arrayOf(numSecret).plus(coefficients))

        val shares = mutableListOf<Point>()
        for (x in 1 until amountOfShares + 1) {

            val y = poly.calculate(x).mod(prime)

            shares.add(Point(x, y))
        }

        return TaskJson.ofValues(prime, shares)
    }

    fun reconstructSecret(prime: BigInteger, shares: Array<Point>): String {
        val y0 = interpolatePolynomial(shares, 0).mod(prime)

        return Utils.bigIntegerToMessage(y0)
    }

    fun interpolatePolynomial(points: Array<Point>, x: Int): BigInteger {
        var result = BigInteger.ZERO

        for (pointI in points) {

            var term = pointI.y.toBigDecimal()

            for (pointJ in points) {

                if (pointI.x != pointJ.x) {

                    val numerator = (x - pointJ.x).toDouble()
                    val denominator = (pointI.x - pointJ.x).toDouble()

                    val div = BigDecimal.valueOf(numerator  / denominator)
                    term = term.multiply(div)
                }
            }

            result = result.plus(term.toBigInteger())
        }

        return result
    }


}


