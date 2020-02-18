package ru.tsu.huffman

import android.util.Log
import java.util.ArrayList

object Coder {
    fun getFrequency(data : ByteArray) : IntArray{
        val frequencyTable = IntArray(256){0}
        for(byte in data){
            frequencyTable[byte.toInt()]++
        }
        return frequencyTable
    }

    fun minimizeFrequencyTable(fullTable : IntArray) : Array<Pair<Byte, Int>>{
        var smallTable = arrayOf<Pair<Byte, Int>>()
        for(byte in fullTable.indices){
            if(fullTable[byte] > 0) {
                smallTable = smallTable.plusElement(byte.toByte() to fullTable[byte])
            }
        }
        return smallTable
    }

    fun sortFrequencyTable(table : Array<Pair<Byte, Int>>){
        table.sortBy { it.second }
    }

    fun createNodeArray(table : Array<Pair<Byte, Int>>) : ArrayList<Node>{
        var nodeArray = arrayListOf<Node>()
        for(element in table){
            nodeArray.add(Node(letter = element.first, freq = element.second))
        }
        return nodeArray
    }

}