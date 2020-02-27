package ru.tsu.huffman

data class Node(
    val letter : Byte?,
    val frequency : Int,
    var leftNode: Node? = null,
    var rightNode: Node? = null
)