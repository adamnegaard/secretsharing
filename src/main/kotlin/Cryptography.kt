
import model.Point
import model.Polynomial
import model.SecretShares
import java.math.BigInteger
import java.security.SecureRandom

object Cryptography {

    /**
     * Construct shares from a plaintext message
     */
    fun constructShares(secret: String, amountOfShares: Int, threshold: Int): SecretShares {
        // message represented as bitstring
        val numSecret = Utils.messageToBigInteger(secret)

        // choose a prime sized depending on the size of the secret
        val random = SecureRandom()
        val prime = BigInteger.probablePrime(numSecret.bitLength() + 1, random)

        // choose threshold - 1 coefficients and create a polynomial
        val coefficients = (1 until threshold).map { BigInteger(numSecret.bitLength(), random) }.toTypedArray()
        val poly = Polynomial(arrayOf(numSecret).plus(coefficients))

        // create shares 1..amountOfShares
        val shares = mutableListOf<Point>()
        for (x in 1 until amountOfShares + 1) {

            // calculate y as x given to the polynomial function
            val y = poly.calculate(x).mod(prime)

            shares.add(Point(x, y))
        }

        // return the prime (field) and the shares
        return SecretShares.ofValues(prime, shares)
    }

    /**
     * Reconstruct a BigInteger message from a prime and shares
     */
    fun reconstructSecretToBigInt(prime: BigInteger, shares: Array<Point>): BigInteger {
        return lagrangeInterpolate(prime, shares, 0)
    }

    /**
     * Reconstruct a String message from a prime and shares
     */
    fun reconstructSecret(prime: BigInteger, shares: Array<Point>): String {
        val y0 = reconstructSecretToBigInt(prime, shares)

        return Utils.bigIntegerToMessage(y0)
    }

    /**
     * Inspired by <a href="https://en.wikipedia.org/wiki/Shamir%27s_secret_sharing">wikipedia algorithm</a>
     */
    private fun lagrangeInterpolate(prime: BigInteger, points: Array<Point>, x: Int): BigInteger {
        fun divMod(num: BigInteger, den: BigInteger, m: BigInteger): BigInteger {
            val inv = den.modInverse(m)

            return num.multiply(inv)
        }

        fun product(arr: List<BigInteger>): BigInteger {
            var acc = BigInteger.ONE

            for (bigInteger in arr) {
                acc = acc.multiply(bigInteger)
            }

            return acc
        }

        val k = points.size
        val bigX = BigInteger.valueOf(x.toLong())

        val numerators = arrayListOf<BigInteger>()
        val denominators = arrayListOf<BigInteger>()

        val xs = points.map { p -> BigInteger.valueOf(p.x.toLong()) }

        for (i in 0 until k) {
            val others = xs.toMutableList()
            val curr = others.removeAt(i)

            numerators.add(product(others.map { o -> bigX.subtract(o) }))
            denominators.add(product(others.map { o -> curr.subtract(o) }))
        }

        val denominator = product(denominators)

        var numerator = BigInteger.ZERO
        for (i in 0 until k) {
            val mul = numerators[i].multiply(denominator).multiply(points[i].y)

            numerator = numerator.plus(divMod(mul, denominators[i], prime))
        }

        return divMod(numerator, denominator, prime).plus(prime).mod(prime)
    }
}


