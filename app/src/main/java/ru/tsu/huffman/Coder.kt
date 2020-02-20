package ru.tsu.huffman

import android.util.ArrayMap
import android.util.Log
import android.widget.Toast
import java.util.ArrayList
import kotlin.experimental.or

object Coder {

    private var freeBits = 0
    private var size = 1
    private var frequencyTable : Array<Pair<Byte, Int>> = arrayOf()
    private var nodeArray : ArrayList<Node> = arrayListOf()
    private lateinit var root : Node
    private var codeTable: Map<Byte, String> = mapOf()

    private fun getFrequency(data : ByteArray) : IntArray{
        val fullFrequencyTable = IntArray(256){0}
        size = data.size
        for(byte in data){
            fullFrequencyTable[byte.toUByte().toInt()]++
        }
        return fullFrequencyTable
    }

    private fun minimizeFrequencyTable(fullTable : IntArray){
        for(byte in fullTable.indices){
            if(fullTable[byte] > 0) {
                frequencyTable = frequencyTable.plusElement(byte.toByte() to fullTable[byte])
            }
        }
    }

    private fun sortFrequencyTable(){
        frequencyTable.sortBy { it.second }
    }

    private fun createNodeArray(){
        for(element in frequencyTable){
            nodeArray.add(Node(letter = element.first, freq = element.second))
        }
    }

    private fun sortNodeArray(){
        nodeArray.sortBy { it.frequency }
    }

    private fun mergeNodes(){
        val new = Node(null, nodeArray[0].frequency + nodeArray[1].frequency).apply {
            leftNode = nodeArray[0]
            rightNode = nodeArray[1]
        }
        nodeArray[1] = new
        nodeArray.removeAt(0)
    }

    private fun createTree(){
        while(nodeArray.size > 1){
            mergeNodes()
            sortNodeArray()
        }
        root = nodeArray[0]
    }

    private fun logNodeArray(){
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

    private fun createCodeTable(){
        explorePath("", root)
    }

    private fun explorePath(currentCode : String, node : Node){
        if(node.leftNode == null){
            codeTable = codeTable.plus(node.letter!! to currentCode)
        } else {
            explorePath("${currentCode}0", node.leftNode!!)
            explorePath("${currentCode}1", node.rightNode!!)
        }
    }

    private fun codeArray(inputText : ByteArray) : ByteArray{
        var outputText = byteArrayOf()
        var currentByte = -1
        for(byte in inputText){
            val toCode = codeTable[byte] //кайф
            for(bit in toCode!!){
                if(freeBits == 0){
                    outputText = outputText.plus(0)
                    freeBits = 8
                    currentByte++
                }
                if(bit == '1') {
                    outputText[currentByte] = outputText[currentByte].or((1 shl(freeBits-1)).toByte())
                }
                freeBits--
            }
        }
        return outputText
    }

    private fun createHeader() : ByteArray{
        var header = byteArrayOf()
        header = header.plus(frequencyTable.size.toByte())
        header = header.plus(freeBits.toByte())
        for(pair in frequencyTable){
            header = header.plus(pair.first)
            header = header.plus((pair.second shr 8).toByte())
            header = header.plus((pair.second).toByte())
        }
        return header
    }

    fun compress(inputText: ByteArray) : ByteArray{
        init()
        val frequencyTable = getFrequency(inputText)
        minimizeFrequencyTable(frequencyTable)
        sortFrequencyTable()
        createNodeArray()
        createTree()
        createCodeTable()

        val compressedText = codeArray(inputText)
        val header = createHeader()
        var outputText = byteArrayOf()
        outputText = outputText.plus(header)
        outputText = outputText.plus(compressedText)
        val ratio : Double = size.toDouble()/outputText.size.toDouble()
        Toast.makeText(App.applicationContext(),"compress ratio $ratio", Toast.LENGTH_LONG ).show()
        return outputText
    }

    private fun init(){
        freeBits = 0
        size = 1
        frequencyTable = arrayOf()
        nodeArray = arrayListOf()
        codeTable = mapOf()
    }
}