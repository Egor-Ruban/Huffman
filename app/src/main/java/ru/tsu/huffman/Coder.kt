package ru.tsu.huffman

fun getFrequency(data : ByteArray) : Array<Int>{
    val freq = Array(256){0}
    for(byte in data){
        freq[byte.toInt()]++
    }
    return freq
}

//todo сделать дерево