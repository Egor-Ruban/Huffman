package ru.tsu.huffman

import android.util.ArrayMap
import android.util.Log
import android.widget.Toast
import java.util.ArrayList
import kotlin.experimental.or

object Coder {

    private var freeBits = 5
    private var size = 1
    private var type = 0
    private var frequencyTable : Array<Pair<Byte, Int>> = arrayOf()
    private var nodeArray : ArrayList<Node> = arrayListOf()
    private lateinit var root : Node
    private var codeTable: Map<Byte, String> = mapOf()

    private fun getFrequency(data : ByteArray) : IntArray{
        val fullFrequencyTable = IntArray(256){0}
        size = data.size
        for(byte in data){
            @UseExperimental(kotlin.ExperimentalUnsignedTypes::class)
            fullFrequencyTable[byte.toUByte().toInt()]++
        }

         type = when(fullFrequencyTable.max()){
            null -> 0
            in 0..256 -> 1
            in 256..16000 -> 2
            in 16000..16000000-> 3
            else -> 4  //у войны и мир - 3
        }
        Log.d("my", "байт затрачено для передачи частоты каждого кода: $type")
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
        var counter = 0
        for(byte in inputText){
            if(counter % 60000 == 0) {
                CoderService.updateInfo(outputText, counter, inputText.size)
                outputText = byteArrayOf()
                Log.d("my", "$counter byte coded")
            }
            val toCode = codeTable[byte]
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
            counter++
        }
        Log.d("my", "$counter byte coded")
        CoderService.updateInfo(outputText, 0,0)
        return outputText
    }


    private fun createHeader() : ByteArray{
        var header = byteArrayOf()
        var typeAndFreeBits = 0
        typeAndFreeBits = typeAndFreeBits or type.toInt() or (freeBits shl 4)
        header = header.plus(frequencyTable.size.toByte())
        header = header.plus(typeAndFreeBits.toByte())
        when(type) {
            4 ->{
                for (pair in frequencyTable) {
                    header = header.plus(pair.first)
                    header = header.plus((pair.second shr 24).toByte())
                    header = header.plus((pair.second shr 16).toByte())
                    header = header.plus((pair.second shr 8).toByte())
                    header = header.plus((pair.second).toByte())
                }
            }
            3 -> {
                for (pair in frequencyTable) {
                    header = header.plus(pair.first)
                    header = header.plus((pair.second shr 16).toByte())
                    header = header.plus((pair.second shr 8).toByte())
                    header = header.plus((pair.second).toByte())
                }
            }
            2 -> {
                for (pair in frequencyTable) {
                    header = header.plus(pair.first)
                    header = header.plus((pair.second shr 8).toByte())
                    header = header.plus((pair.second).toByte())
                }
            }
            1 -> {
                for (pair in frequencyTable) {
                    header = header.plus(pair.first)
                    header = header.plus((pair.second).toByte())
                }
            }

        }
        return header
    }

    fun compress(inputText: ByteArray){
        init()
        CoderService.createNotification(inputText.size)
        val frequencyTable = getFrequency(inputText)
        minimizeFrequencyTable(frequencyTable)
        sortFrequencyTable()
        Log.d("my", "coder frequency table was sorted")
        createNodeArray()
        Log.d("my", "coder node array was created")
        createTree()
        Log.d("my", "the coder tree was created")
        createCodeTable()
        Log.d("my", "code table was created")

        val header = createHeader()
        CoderService.sendHeader(header)
        Log.d("my", "header was created")

        val compressedText = codeArray(inputText)
        Log.d("my", "text was compressed")

        var outputText = byteArrayOf()
        outputText = outputText.plus(header)
        outputText = outputText.plus(compressedText)
        val ratio : Double = size.toDouble()/outputText.size.toDouble()
        Toast.makeText(App.applicationContext(),"compress ratio $ratio", Toast.LENGTH_LONG ).show()
        Log.d("my_logs","ratio is $ratio")
    }

    private fun init(){
        freeBits = 0
        size = 1
        frequencyTable = arrayOf()
        nodeArray = arrayListOf()
        codeTable = mapOf()
    }
}