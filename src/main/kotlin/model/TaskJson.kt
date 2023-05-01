package model

import Utils
import kotlinx.serialization.Serializable
import java.math.BigInteger

@Serializable
data class TaskJson(private val field: String, private val shares: Array<String?>) {

    fun getField(): BigInteger {
        return Utils.bitStringToBigInteger(field)
    }

    fun getShares(): Array<Point> {
        return shares
            .mapIndexed { shareIndex, shareValue -> shareAsPoint(shareIndex, shareValue)  }
            .filterNotNull()
            .toTypedArray()
    }

    private fun shareAsPoint(shareIndex: Int, shareValue: String?): Point? {
        if(shareValue == null) return null

        val bigI = BigInteger.valueOf(shareIndex.toLong())
        val bigS = Utils.bitStringToBigInteger(shareValue)

        return Point(bigI, bigS)
    }

}
