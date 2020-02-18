package ru.tsu.huffman

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.codekidlabs.storagechooser.StorageChooser
import kotlinx.android.synthetic.main.activity_mainl.*
import ru.tsu.huffman.Extensions.toCharArray
import java.io.*
import java.time.LocalDate


class MainActivity : AppCompatActivity() {

    val LOG_TAG = "myLogs"

    var fileToRead = "default.txt"
    var fileToWrite = fileToRead

    override fun onCreate(savedInstanceState: Bundle?) {
        fileToRead = "$filesDir/$fileToRead"
        fileToWrite = fileToRead
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mainl)
/*
        btn_code.setOnClickListener {
            val data = byteArrayOf(48, 49, 50, -1, 0, 51, 10, 52) // пример данных
            writeBinFile(data)
        }

        btn_decode.setOnClickListener {
            readFileBin()
        }

        btn_read.setOnClickListener {
            val chooser = StorageChooser.Builder()
                .withActivity(this)
                .withFragmentManager(fragmentManager)
                .withMemoryBar(true)
                .allowCustomPath(true)
                .setType(StorageChooser.FILE_PICKER)
                .build()

            chooser.setOnSelectListener { path ->
                if(".txt" in path){
                    Toast.makeText(this, "txt", Toast.LENGTH_SHORT).show()
                    fileToRead = path
                    fileToWrite = "${File(path).parent}/${File(path).name.replace(".txt", ".hfm")}"
                    readFile()
                } else if(".hfm" in path){
                    Toast.makeText(this,"hfm",Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this,"wrong type",Toast.LENGTH_SHORT).show()
                }
            }
            chooser.show()
        }
     */
        val test = "susie says it was easy"
        val frequencyTable = Coder.getFrequency(test.toByteArray())
        val smallTable = Coder.minimizeFrequencyTable(frequencyTable)
        Coder.sortFrequencyTable(smallTable)
        val nodeArray = Coder.createNodeArray(smallTable)
        Log.d(LOG_TAG, "${Coder.createTree(nodeArray).frequency}") //дерево готово
    }

    fun writeBinFile(data : ByteArray) {
        try {
            val f = File(fileToWrite)
            f.createNewFile()
            val stream = DataOutputStream(
                    FileOutputStream(f, false)
            )
            stream.write(data)
            stream.close()
            Log.d(LOG_TAG, "Файл записан $fileToWrite")
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun readFileBin() : ByteArray{
        try {
            //открываем поток для чтения
            val stream = DataInputStream(FileInputStream(File(fileToRead)))
            var ba = stream.readBytes()
            stream.close()
            // читаем содержимое
            Log.d(LOG_TAG, "read all ${ba[0]} ${ba[1]} ${ba[2]} ${ba[3]}")
            return ba
        } catch (e: FileNotFoundException) {
            Log.d(LOG_TAG, "not found")
            e.printStackTrace()
            return byteArrayOf()
        } catch (e: IOException) {
            Log.d(LOG_TAG, "IOE")
            e.printStackTrace()
            return byteArrayOf()
        }
    }

    fun readFile() : String{ //чисто для вывода txt
        try {
            val stream = BufferedReader(
                InputStreamReader(
                    FileInputStream(File(fileToRead))
                )
            )
            var str: String = stream.readText()
            // читаем содержимое
            Log.d(LOG_TAG, "read all $str")
            tv_file.visibility = View.VISIBLE
            tv_file.text = str
            stream.close()
            return str
        } catch (e: FileNotFoundException) {
            Log.d(LOG_TAG, "not found")
            e.printStackTrace()
            return ""
        } catch (e: IOException) {
            Log.d(LOG_TAG, "IOE")
            e.printStackTrace()
            return ""
        }
    }
}
