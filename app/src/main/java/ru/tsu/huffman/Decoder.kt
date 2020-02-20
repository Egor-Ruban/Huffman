package ru.tsu.huffman

import android.util.Log
import java.util.ArrayList

object Decoder {
    fun decode(inputText : ByteArray) : ByteArray{
        // 1 байт - кол-во символов
        // 2 байт - кол-во свободных бит
        // 3, 6, 9, 12.. - символы
        // 4,5 - частота
        var currentByte = 2
        val symbols = inputText[0]
        val freeBits = inputText[1]
        var symbol : Byte = 0
        var freq = 0
        var nodeArray = arrayListOf<Node>()
        for(i in inputText){
            Log.d("my", "input $i")
        }
        while(currentByte<2 + 3*symbols){
            if(currentByte % 3 == 2){ //заменить на when
                symbol = inputText[currentByte]
            } else if (currentByte % 3 == 0){
                freq = inputText[currentByte].toInt() shl 8
            } else {
                freq = freq or (inputText[currentByte].toInt())
                nodeArray.add(Node(symbol, freq))
                freq = 0
            }
            currentByte++
        }
        logNodeArray(nodeArray)
        var outputText = byteArrayOf()
        val root = createTree(nodeArray)
        var curNode = root
        while(currentByte < inputText.size){
            var curItem = inputText[currentByte]

            for(i in 0..7){
                //Log.d("my", "$i")
                if(currentByte == inputText.size - 1 && 7-i < freeBits){
                    break
                } else {
                    val bit = curItem.toInt().shr(7 - i) and 1
                    if (bit == 0) {
                        curNode = curNode.leftNode!!
                    } else {
                        curNode = curNode.rightNode!!
                    }
                    if (curNode.letter != null) {
                        outputText = outputText.plus(curNode.letter!!)
                        Log.d("my", "${curNode.letter!!.toChar()}")
                        curNode = root
                    }
                }
            }
            currentByte++

        }
        return outputText
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
}