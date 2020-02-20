package ru.tsu.huffman

import android.util.ArrayMap
import android.util.Log
import java.util.ArrayList
import kotlin.experimental.or

object Coder {
    var freeBits = 0
    fun getFrequency(data : ByteArray) : IntArray{
        val frequencyTable = IntArray(256){0}
        for(byte in data){
            frequencyTable[byte.toUByte().toInt()]++
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

    fun createCodeTable(root : Node) : Map<Byte, String>{
        return explorePath("",root, mapOf())
    }

    private fun explorePath(currentCode : String, node : Node, resMap : Map<Byte, String>) : Map<Byte, String>{
        var res = resMap
        if(node.leftNode == null){
            res = resMap.plus(node.letter!! to currentCode)
        } else {
            res = explorePath("${currentCode}0", node.leftNode!!, res)
            res = explorePath("${currentCode}1", node.rightNode!!, res)
        }
        return res
    }

    fun codeArray(inputText : ByteArray, codeTable: Map<Byte, String>) : ByteArray{
        var outputText = byteArrayOf()
        var usedBits = 0
        //var freeBits = 0
        var currentByte = -1
        freeBits = 0
        for(byte in inputText){

            var toCode = codeTable.get(byte) //кайф
            for(bit in toCode!!){
                if(freeBits == 0){
                    outputText = outputText.plus(0)
                    usedBits = 0
                    freeBits = 8
                    currentByte++
                }
                if(bit == '1') {
                    outputText[currentByte] = outputText[currentByte].or((1 shl(freeBits-1)).toByte())
                }
                usedBits++
                freeBits--
            }
        }
        return outputText
    }

    fun createHeader(nodeArray: Array<Pair<Byte, Int>>) : ByteArray{
        var header = byteArrayOf()
        header = header.plus(nodeArray.size.toByte())
        header = header.plus(freeBits.toByte())
        for(pair in nodeArray){
            header = header.plus(pair.first)
            header = header.plus((pair.second shr 8).toByte())
            header = header.plus((pair.second).toByte())
        }

        return header
    }

    fun compress(inputText: ByteArray) : ByteArray{
        val frequencyTable = Coder.getFrequency(inputText)
        val smallTable = Coder.minimizeFrequencyTable(frequencyTable)
        Coder.sortFrequencyTable(smallTable)
        val nodeArray = Coder.createNodeArray(smallTable)
        val root = Coder.createTree(nodeArray)
        val codeTable = Coder.createCodeTable(root)

        val compressedText = Coder.codeArray(inputText, codeTable)
        val header = Coder.createHeader(smallTable) //технически уже можно кодировать
        var outputText = byteArrayOf()
        outputText = outputText.plus(header)
        outputText = outputText.plus(compressedText)
        return outputText
    }
}