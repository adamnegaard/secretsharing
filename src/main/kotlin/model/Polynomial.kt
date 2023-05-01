package model

import java.math.BigInteger

data class Polynomial(val coefficients: Array<BigInteger>) {

    private val degree = coefficients.size

    init {
        if (coefficients.isEmpty()) {
            throw IllegalArgumentException("Polynomial must have more than one degree")
        }
    }
/*
    fun calculate(point: Point): BigInteger {
        var result = BigInteger.ZERO
        var currentDegree = degree - 1

        for (i in coefficients.indices) {

            var stepResult = coefficients[i]
            if(currentDegree > 0) {
                val pow = point.x.pow(currentDegree)
                stepResult = stepResult.multiply(pow)
            }

            currentDegree--
            result = result.plus(stepResult)
        }

        return result
    }*/

    override fun toString(): String {
        val sb = StringBuilder()

        var currentDegree = degree - 1
        for (i in coefficients.indices) {

            sb.append(coefficients[i])
            if(currentDegree > 0) {
                sb.append("*x^").append(currentDegree).append(" + ")
            }

            currentDegree--
        }

        return sb.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Polynomial

        if (!coefficients.contentEquals(other.coefficients)) return false
        return degree == other.degree
    }

    override fun hashCode(): Int {
        var result = coefficients.contentHashCode()
        result = 31 * result + degree
        return result
    }
}
