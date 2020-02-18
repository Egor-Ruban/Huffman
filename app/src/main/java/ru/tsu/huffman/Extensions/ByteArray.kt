package ru.tsu.huffman.Extensions

fun ByteArray.toCharArray() : CharArray {
    val ca = CharArray(size)
    for(i in indices){

        ca[i] = (get(i).toChar())
    }
    return ca
}
