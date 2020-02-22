package ru.tsu.huffman

import android.util.Log
import java.lang.StringBuilder
import java.util.ArrayList

object Decoder {

    private var codedSymbolsAmount : Byte = 0
    private var emptyBits : Byte = 0
    private var nodeArray : ArrayList<Node> = arrayListOf()
    private var currentByte = 0
    lateinit var root : Node

    fun decode(inputText : ByteArray) : ByteArray{
        init()
        parseHeader(inputText)
        return decodeText(inputText)
    }

    private fun init(){
        codedSymbolsAmount = 0
        emptyBits = 0
        nodeArray = arrayListOf()
        currentByte = 0
    }

    private fun decodeText(inputText: ByteArray) : ByteArray{
        var outputText = byteArrayOf()
        createTree()



        var curNode = root
        while(currentByte < inputText.size){
            val curItem = inputText[currentByte]
            for(i in 0..7){
                if(currentByte == inputText.size - 1 && 7-i < emptyBits){
                    break
                } else {
                    val bit = curItem.toInt().shr(7 - i) and 1
                    curNode = if(bit == 0) {
                        curNode.leftNode!!
                    } else {
                        curNode.rightNode!!
                    }

                    if (curNode.letter != null) {
                        outputText = outputText.plus(curNode.letter!!)
                        curNode = root
                    }
                }
            }
            currentByte++
        }

        return outputText
    }

    private fun parseHeader(inputText: ByteArray){
        codedSymbolsAmount = inputText[0]
        emptyBits = inputText[1]
        currentByte = 2
        var symbol : Byte = 0
        var freq : Int = 0
        while(currentByte < 2 + 3 * codedSymbolsAmount){
            when(currentByte % 3){
                2 -> symbol = inputText[currentByte]
                0 -> freq = inputText[currentByte].toUByte().toInt() shl 8
                1 ->{
                    freq = freq or (inputText[currentByte].toUByte().toInt())
                    nodeArray.add(Node(symbol, freq.toInt()))
                    freq = 0
                }
            }
            currentByte++
        }
    }

    private fun logNodeArray(nodeArray: ArrayList<Node>){
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
}