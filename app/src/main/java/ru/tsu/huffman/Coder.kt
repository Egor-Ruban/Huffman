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

    fun sortNodeArray(data : ArrayList<Node>){
        data.sortBy { it.frequency }
    }

    fun mergeNodes(data : ArrayList<Node>){
        val new = Node(null, data[0].frequency + data[1].frequency).apply {
            leftNode = data[0]
            rightNode = data[1]
        }
        data[1] = new
        data.removeAt(0)
    }

    fun createTree(nodeArray : ArrayList<Node>) : Node{
        while(nodeArray.size > 1){
            mergeNodes(nodeArray)
            sortNodeArray(nodeArray)
        }
        return nodeArray[0]
    }

    fun logNodeArray(nodeArray: ArrayList<Node>){
        for(element in nodeArray){
            with(element) {
                if(letter != null) {
                    Log.d(
                        "my",
                        "$letter ${letter.toChar()} $frequency $leftNode $rightNode"
                    )
                } else {
                    Log.d(
                        "my",
                        "$letter  $frequency $leftNode $rightNode"
                    )
                }
            }
        }
    }

}