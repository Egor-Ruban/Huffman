package ru.tsu.huffman

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.codekidlabs.storagechooser.StorageChooser
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*


class MainActivity : AppCompatActivity() {

    val LOG_TAG = "myLogs"

    val FILENAME = "testFile"

    val DIR_SD = "MyFiles"
    val FILENAME_SD = "fileSD"



    /** Called when the activity is first created.  */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(LOG_TAG, "start")

        btnWrite.setOnClickListener {
            val data = byteArrayOf(48, 49, 50, -1, 0, 51, 10, 52)
            writeFile(byteArrayToCharArray(data))
        }
        btnRead.setOnClickListener {
            readFile()
        }
        btnFilePicker.setOnClickListener {
            val chooser = StorageChooser.Builder() // Specify context of the dialog
                .withActivity(this)
                .withFragmentManager(fragmentManager)
                .withMemoryBar(true)
                .allowCustomPath(true) // Define the mode as the FILE CHOOSER
                .setType(StorageChooser.FILE_PICKER)
                .build()
            chooser.setOnSelectListener { path ->
                if(".txt" in path){
                    Toast.makeText(this, "txt", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this,"wrong type",Toast.LENGTH_SHORT).show()
                }
                // e.g /storage/emulated/0/Documents/file.txt
                Log.i("MY", path) // что делать с выбранным элементом
            }
            chooser.show()
        }
    }

    private fun byteArrayToCharArray(data : ByteArray) : CharArray{
        val ca = CharArray(data.size)
        for(i in data.indices){
            ca[i] = (data[i].toChar())
        }
        return ca
    }

    private fun stringToByteArray(data : String) : ByteArray{
        val ba = ByteArray(data.length)
        for(i in data.indices){
            ba[i] = data[i].toByte()
        }
        for(i in ba.indices){
            Log.d(LOG_TAG, "ba is ${ba[i]}")
        }
        return ba
    }

    private fun charArrayToByteArray(data : CharArray) : ByteArray{
        val ba = ByteArray(data.size)
        for(i in data.indices){
            ba[i] = data[i].toByte()
        }
        for(i in ba.indices){
            Log.d(LOG_TAG, "ba is ${ba[i]}")
        }
        return ba
    }

    fun writeFile(data : CharArray) {
        try { // отрываем поток для записи
            val bw = BufferedWriter(
                OutputStreamWriter(
                    openFileOutput(FILENAME, Context.MODE_APPEND)
                )
            )
            bw.write(et_input.text.toString())
            //bw.write(data)
            // закрываем поток
            bw.close()
            Log.d(LOG_TAG, "Файл записан")
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun readFile() {
        try {

            //открываем поток для чтения
          // val fileData = ByteArray(file.length().toInt())
            val br = BufferedReader(
                InputStreamReader(
                    openFileInput(FILENAME)
                )
            )
            var str: String = br.readText()
            // читаем содержимое
            Log.d(LOG_TAG, "read all $str")
            stringToByteArray(str)
            //while (br.readLine().also({ str = it }) != null) {
            //  Log.d(LOG_TAG, str)
            //}
        } catch (e: FileNotFoundException) {
            Log.d(LOG_TAG, "not found")
            e.printStackTrace()
        } catch (e: IOException) {
            Log.d(LOG_TAG, "IOE")
            e.printStackTrace()
        }
    }
}
