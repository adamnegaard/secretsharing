package model

import java.math.BigInteger

data class Polynomial(val coefficients: Array<BigInteger>) {

    private val degree = coefficients.size - 1

    init {
        if (coefficients.isEmpty()) {
            throw IllegalArgumentException("Polynomial must have more than zero degrees")
        }
    }

    fun calculate(x: Int): BigInteger {
        var result = coefficients[0]
        var currentDegree = 1

        val bigX = BigInteger.valueOf(x.toLong())

        for (i in 1 .. degree) {

            var stepResult = coefficients[i]
            if(currentDegree < coefficients.size + 1) {
                val pow = bigX.pow(currentDegree)
                stepResult = stepResult.multiply(pow)
            }

            currentDegree++
            result = result.plus(stepResult)
        }

        return result
    }

    override fun toString(): String {
        val sb = StringBuilder().append(coefficients[0]).append(" + ")

        var currentDegree = 1
        for (i in 1 .. degree) {

            sb.append(coefficients[i])
            if(currentDegree < coefficients.size) {
                sb.append("*x^").append(currentDegree)
                if(currentDegree < coefficients.size - 1) {
                    sb.append(" + ")
                }
            }

            currentDegree++
        }

        return sb.toString()
    }
}
