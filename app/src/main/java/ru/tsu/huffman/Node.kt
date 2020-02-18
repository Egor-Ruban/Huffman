package ru.tsu.huffman

class Node(
    letter : Byte?,
    freq : Int
) {
    val letter = letter
    val frequency = freq
    var leftNode : Node? = null
    var rightNode : Node? = null
}