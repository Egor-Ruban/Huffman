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
import java.io.*
import java.time.LocalDate


class MainActivity : AppCompatActivity() {

    private val LOG_TAG = "myLogs"

    private var fileToRead = "default.txt"
    private var fileToWrite = fileToRead

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mainl)

        fileToRead = "$filesDir/$fileToRead"
        fileToWrite = fileToRead
        initButtons()
    }

    private fun initButtons(){
        btn_code.setOnClickListener {
            val chooser = StorageChooser.Builder()
                .withActivity(this)
                .withFragmentManager(fragmentManager)
                .withMemoryBar(true)
                .allowCustomPath(true)
                .setType(StorageChooser.FILE_PICKER)
                .build()

            chooser.setOnSelectListener { path ->
                compressText(path)
            }
            chooser.show()
        }

        btn_decode.setOnClickListener {
            val chooser = StorageChooser.Builder()
                .withActivity(this)
                .withFragmentManager(fragmentManager)
                .withMemoryBar(true)
                .allowCustomPath(true)
                .setType(StorageChooser.FILE_PICKER)
                .build()

            chooser.setOnSelectListener { path ->
                decompressHfm(path)
            }
            chooser.show()
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
                showText(path)
            }
            chooser.show()
        }
    }

    private fun compressText(path : String){
        if(".txt" in path){
            fileToRead = path
            fileToWrite = "${File(path).parent}/${File(path).name.replace(".txt", ".hfm")}"
            val inputText = readFile().toByteArray()
            writeBinFile(Coder.compress(inputText))
        } else if(".hfm" in path){
            Toast.makeText(this,"maybe you wanted to decompress it?",Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this,"wrong type",Toast.LENGTH_SHORT).show() //replace User
        }
    }

    private fun decompressHfm(path: String){
        if(".txt" in path){
            Toast.makeText(this, "maybe you wanted to compress it?", Toast.LENGTH_SHORT).show()
        } else if(".hfm" in path){
            fileToRead = path
            fileToWrite = "${File(path).parent}/${File(path).name.replace(".hfm", ".txt")}"
            val inputText = readFileBin()
            val outputText = Decoder.decode(inputText)
            writeBinFile(outputText)
        } else {
            Toast.makeText(this,"wrong type",Toast.LENGTH_SHORT).show() //replace User
        }
    }

    private fun showText(path: String){
        if(".txt" in path){
            fileToRead = path
            readFile()
        } else {
            Toast.makeText(this,"wrong type",Toast.LENGTH_SHORT).show() // replace User
        }
    }

    private fun writeBinFile(data : ByteArray) {
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

    private fun readFileBin() : ByteArray{
        try {
            //открываем поток для чтения
            val stream = DataInputStream(FileInputStream(File(fileToRead)))
            val ba = stream.readBytes()
            stream.close()
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

    private fun readFile() : String{ //чисто для вывода txt
        try {
            val stream = BufferedReader(
                InputStreamReader(
                    FileInputStream(File(fileToRead))
                )
            )
            val str: String = stream.readText()
            // читаем содержимое
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
