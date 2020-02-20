package ru.tsu.huffman

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.codekidlabs.storagechooser.StorageChooser
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*

class MainActivity : AppCompatActivity() {

    private val LOG_TAG = "myLogs"

    private var fileToRead = "default.txt"
    private var fileToWrite = fileToRead

    val SHOWN_TEXT = "SHOWN_TEXT"
    val IS_VISIBLE_TEXT = "IS_VISIBLE_TEXT"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        tv_file.text = savedInstanceState?.getString(SHOWN_TEXT)
        tv_file.visibility = savedInstanceState?.getInt(IS_VISIBLE_TEXT) ?: View.INVISIBLE
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
        showError()
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
            showError()
        }
    }

    private fun showText(path: String){
        if(".txt" in path){
            fileToRead = path
            readFile()
        } else {
            showError()
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

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)

        outState.putString(SHOWN_TEXT, tv_file.text.toString())
        outState.putInt(IS_VISIBLE_TEXT, tv_file.visibility)
    }

    private fun showError(){
        val newDialog = MyAlertDialogFragment.newInstance()
        newDialog.show(supportFragmentManager, "dialog")
    }
}