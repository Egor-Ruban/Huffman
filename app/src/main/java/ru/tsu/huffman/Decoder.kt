package ru.tsu.huffman

import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.ArrayList
import kotlin.experimental.and

object Decoder {

    private var codedSymbolsAmount: Byte = 0
    private var emptyBits: Byte = 0
    private var nodeArray: ArrayList<Node> = arrayListOf()
    private var currentByte = 0
    lateinit var root: Node
    private var type = 0

    fun decode(inputText: ByteArray) {
        CoderService.createNotification(inputText.size)
        init()
        parseHeader(inputText)
        Log.d("my", "header was parsed")
        decodeText(inputText)
    }

    private fun init() {
        codedSymbolsAmount = 0
        emptyBits = 0
        nodeArray = arrayListOf()
        currentByte = 0
    }

    private fun decodeText(inputText: ByteArray){
        var outputText = byteArrayOf()
        createTree()
        Log.d("my", "decoder tree was created")
        var curNode = root
        var counter = 0
        while (currentByte < inputText.size) {
            val curItem = inputText[currentByte]
            for (i in 0..7) {
                if (currentByte == inputText.size - 1 && 7 - i < emptyBits) {
                    break
                } else {
                    val bit = curItem.toInt().shr(7 - i) and 1
                    curNode = if (bit == 0) {
                        curNode.leftNode!!
                    } else {
                        curNode.rightNode!!
                    }

                    if (curNode.letter != null) {
                        outputText = outputText.plus(curNode.letter!!)
                        counter++
                        if(counter % 10000 == 0) {
                            Log.d("my", "$counter symbols were decoded")
                        }
                        curNode = root
                    }
                }
            }
            currentByte++
            if(currentByte % 50000 == 0){
                CoderService.updateInfo(outputText, currentByte, inputText.size)
                outputText = byteArrayOf()

            }
        }

        CoderService.updateInfo(outputText, 0, 0)
        CoderService.sendLastNotification()
        Log.d("my", "text was decoded")
    }

    @UseExperimental(ExperimentalUnsignedTypes::class)
    private fun parseHeader(inputText: ByteArray) {
        codedSymbolsAmount = inputText[0]
        var emptyBitsAndType = inputText[1]
        emptyBits = (emptyBitsAndType.toInt() shr 4).toByte()
        type = emptyBitsAndType.toInt() and 15
        Log.d("my", "байт шифрует частоту каждого символа: $type")
        when (type) {
            1 -> {
                createNodeArrayFirstType(inputText)
            }
            2 -> {
                createNodeArraySecondType(inputText)
            }
            3 -> {
                createNodeArrayThirdType(inputText)
            }
            4 -> {
                createNodeArrayFourthType(inputText)
            }
        }
    }

    private fun logNodeArray(nodeArray: ArrayList<Node>) {
        for (element in nodeArray) {
            with(element) {
                if (letter != null) {
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

    private fun sortNodeArray() {
        nodeArray.sortBy { it.frequency }
    }

    private fun mergeNodes() {
        val new = Node(null, nodeArray[0].frequency + nodeArray[1].frequency).apply {
            leftNode = nodeArray[0]
            rightNode = nodeArray[1]
        }
        nodeArray[1] = new
        nodeArray.removeAt(0)
    }

    private fun createTree() {
        while (nodeArray.size > 1) {
            mergeNodes()
            sortNodeArray()
        }
        root = nodeArray[0]
    }


    private fun createNodeArrayFirstType(inputText: ByteArray) {
        currentByte = 2
        var symbol: Byte = 0
        var freq: Int = 0
        while (currentByte < 2 + 2 * codedSymbolsAmount) {
            when (currentByte % 2) {
                0 -> symbol = inputText[currentByte]
                1 -> {
                    freq = inputText[currentByte].toUByte().toInt()
                    nodeArray.add(Node(symbol, freq))
                    freq = 0
                }
            }
            currentByte++
        }
    }

    private fun createNodeArraySecondType(inputText: ByteArray){
        currentByte = 2
        var symbol: Byte = 0
        var freq: Int = 0
        while (currentByte < 2 + 3 * codedSymbolsAmount) {
            when (currentByte % 3) {
                2 -> symbol = inputText[currentByte]
                0 -> freq = freq or (inputText[currentByte].toUByte().toInt() shl 8)
                1 -> {
                    freq = freq or (inputText[currentByte].toUByte().toInt())
                    nodeArray.add(Node(symbol, freq.toInt()))
                    freq = 0
                }
            }
            currentByte++
        }
    }

    private fun createNodeArrayThirdType(inputText: ByteArray){
        currentByte = 2
        var symbol: Byte = 0
        var freq: Int = 0
        while (currentByte < 2 + 4 * codedSymbolsAmount) {
            when (currentByte % 4) {
                2 -> symbol = inputText[currentByte]
                3 -> freq = freq or (inputText[currentByte].toUByte().toInt() shl 16)
                0 -> freq = freq or (inputText[currentByte].toUByte().toInt() shl 8)
                1 -> {
                    freq = freq or (inputText[currentByte].toUByte().toInt())
                    nodeArray.add(Node(symbol, freq.toInt()))
                    freq = 0
                }
            }
            currentByte++

        }
    }

    private fun createNodeArrayFourthType(inputText: ByteArray){
        currentByte = 2
        var symbol: Byte = 0
        var freq: Int = 0
        while (currentByte < 2 + 5 * codedSymbolsAmount) {
            when (currentByte % 5) {
                2 -> symbol = inputText[currentByte]
                3 -> freq = (freq or inputText[currentByte].toUByte().toInt() shl 24)
                4 -> freq = (freq or inputText[currentByte].toUByte().toInt() shl 16)
                0 -> freq = (freq or inputText[currentByte].toUByte().toInt() shl 8)
                1 -> {
                    freq = freq or (inputText[currentByte].toUByte().toInt())
                    nodeArray.add(Node(symbol, freq.toInt()))
                    freq = 0
                }
            }

            currentByte++
        }
    }
}
