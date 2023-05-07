package model

import Utils
import kotlinx.serialization.Serializable
import java.math.BigInteger

@Serializable
data class SharePoint(val x: Int, val y: String?)

@Serializable
data class SecretShares(private val field: String, private val shares: Array<SharePoint>) {

    companion object {
        fun ofValues(field: BigInteger, shares: List<Point>): SecretShares {
            val stringField = Utils.bigIntegerToHex(field)
            val sharePoints: Array<SharePoint> = shares.sortedBy { point -> point.x }
                .map { point -> SharePoint(point.x, Utils.bigIntegerToHex(point.y)) }
                .toTypedArray()

            return SecretShares(stringField, sharePoints)
        }
    }

    fun getField(): BigInteger {
        return Utils.hexToBigInteger(field)
    }

    fun getShares(): Array<Point> {
        return shares.mapNotNull { share -> shareAsPoint(share.x, share.y) }
            .toTypedArray()
    }

    private fun shareAsPoint(shareIndex: Int, shareValue: String?): Point? {
        if(shareValue == null) return null

        val bigShare = Utils.hexToBigInteger(shareValue)

        return Point(shareIndex, bigShare)
    }

}
