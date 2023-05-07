fun main(args: Array<String>) {
    val secret = "Very secret message"

    val secretShares = Cryptography.constructShares(secret, 5, 3)

    val shares = secretShares.getShares()
    val subShares = arrayOf(shares[0], shares[1], shares[3])

    val reconstructedSecret = Cryptography.reconstructSecret(secretShares.getField(), subShares)
    println("secret was: '$reconstructedSecret'")
}