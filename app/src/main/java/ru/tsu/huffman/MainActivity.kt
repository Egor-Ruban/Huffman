package ru.tsu.huffman

import android.content.Context
import android.content.Intent
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

    val EXCEPTION = "file not found"

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
            tv_file.visibility = View.INVISIBLE
            val chooser = StorageChooser.Builder()
                .withActivity(this)
                .withFragmentManager(fragmentManager)
                .withMemoryBar(true)
                .allowCustomPath(true)
                .setType(StorageChooser.FILE_PICKER)
                .disableMultiSelect()
                .build()

            chooser.setOnSelectListener { path ->
                compressText(path)
            }
            chooser.show()
        }

        btn_decode.setOnClickListener {
            tv_file.visibility = View.INVISIBLE
            val chooser = StorageChooser.Builder()
                .withActivity(this)
                .withFragmentManager(fragmentManager)
                .withMemoryBar(true)
                .allowCustomPath(true)
                .setType(StorageChooser.FILE_PICKER)
                .disableMultiSelect()
                .build()

            chooser.setOnSelectListener { path ->
                decompressHfm(path)
            }
            chooser.show()
        }

        btn_read.setOnClickListener {
            tv_file.visibility = View.INVISIBLE
            val chooser = StorageChooser.Builder()
                .withActivity(this)
                .withFragmentManager(fragmentManager)
                .withMemoryBar(true)
                .allowCustomPath(true)
                .setType(StorageChooser.FILE_PICKER)
                .disableMultiSelect()
                .build()

            chooser.setOnSelectListener { path ->
                showText(path)
            }
            chooser.show()
        }

        btn_compare.setOnClickListener {
            tv_file.visibility = View.INVISIBLE
            val chooser = StorageChooser.Builder()
                .withActivity(this)
                .withFragmentManager(fragmentManager)
                .withMemoryBar(true)
                .allowCustomPath(true)
                .setType(StorageChooser.FILE_PICKER)
                .build()

            chooser.setOnCancelListener {
                Toast.makeText(this, "выбери два файла", Toast.LENGTH_LONG).show()
            }

            chooser.setOnMultipleSelectListener {selectedFilePaths ->
                fileToRead = selectedFilePaths[0]
                val firstText = readFile()
                var counter = 0
                var resultString = ""
                for(path in selectedFilePaths){
                    resultString += path
                    resultString += "\n"
                    fileToRead = path
                    val secondText = readFile()
                    if(! (firstText.equals(secondText))){
                        Toast.makeText(this, "не равны", Toast.LENGTH_LONG).show()
                        tv_file.visibility = View.VISIBLE
                        tv_file.text = resultString + "не равны"
                        break
                    }
                    counter ++
                }
                if(counter == selectedFilePaths.size){
                    Toast.makeText(this, "равны", Toast.LENGTH_LONG).show()
                    tv_file.visibility = View.VISIBLE
                    tv_file.text = resultString + "равны"
                }
            }
            chooser.show()
        }
    }

    private fun compressText(path : String){
        if(".txt" in path){
            if(checkIfExist(path)) {
                CoderService.startCompressing(baseContext, path, byteArrayOf())
            } else {
                tv_file.visibility = View.VISIBLE
                tv_file.text = EXCEPTION
                Toast.makeText(this, EXCEPTION, Toast.LENGTH_LONG).show()
            }
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
            if(checkIfExist(path)) {
                CoderService.startDecompressing(this, path, byteArrayOf())
            } else {
                tv_file.visibility = View.VISIBLE
                tv_file.text = EXCEPTION
                Toast.makeText(this, EXCEPTION, Toast.LENGTH_LONG).show()
            }
        } else {
            showError()
        }
    }

    private fun showText(path: String){
        if(".txt" in path){
            if(checkIfExist(path)) {
                fileToRead = path
                tv_file.text = readFile()
                tv_file.visibility = View.VISIBLE
            } else {
                tv_file.visibility = View.VISIBLE
                tv_file.text = EXCEPTION
                Toast.makeText(this, EXCEPTION, Toast.LENGTH_LONG).show()
            }
        } else {
            showError()
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
            stream.close()
            return str
        } catch (e: FileNotFoundException) {
            Log.d(LOG_TAG, "not found")
            e.printStackTrace()
            return "file not found"
        } catch (e: IOException) {
            Log.d(LOG_TAG, "IOE")
            e.printStackTrace()
            return "IOException"
        }
    }

    private fun checkIfExist(path : String) : Boolean{
        try {
            val stream = BufferedReader(
                InputStreamReader(
                    FileInputStream(File(fileToRead))
                )
            ).close()
            return true
        } catch (e: FileNotFoundException) {
            Log.d(LOG_TAG, "not found")
            e.printStackTrace()
            return false
        } catch (e: IOException) {
            Log.d(LOG_TAG, "IOE")
            e.printStackTrace()
            return false
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