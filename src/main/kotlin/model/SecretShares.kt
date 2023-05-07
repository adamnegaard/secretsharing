package model

import Utils
import kotlinx.serialization.Serializable
import java.math.BigInteger

@Serializable
data class SecretShares(private val field: String, private val shares: Array<String?>) {

    companion object {
        fun ofValues(field: BigInteger, shares: List<Point>): SecretShares {
            val stringField = Utils.bigIntegerToHex(field)
            val stringShares: Array<String?> = shares.sortedBy { point -> point.x }
                .map { point -> Utils.bigIntegerToHex(point.y) }
                .toTypedArray()

            return SecretShares(stringField, stringShares)
        }
    }

    fun getField(): BigInteger {
        return Utils.hexToBigInteger(field)
    }

    fun getShares(): Array<Point> {
        return shares
            .mapIndexed { shareIndex, shareValue -> shareAsPoint(shareIndex + 1, shareValue)  }
            .filterNotNull()
            .toTypedArray()
    }

    private fun shareAsPoint(shareIndex: Int, shareValue: String?): Point? {
        if(shareValue == null) return null

        val bigShare = Utils.hexToBigInteger(shareValue)

        return Point(shareIndex, bigShare)
    }

}