package ru.tsu.huffman

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
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
    }

    private fun byteArrayToCharArray(data : ByteArray) : CharArray{
        val ca = CharArray(data.size)
        for(i in data.indices){
            ca[i] = (data[i].toChar())
        }
        return ca
    }

    private fun stringtoByteArray(data : String) : ByteArray{
        val ba = ByteArray(data.length)
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
                    openFileOutput(FILENAME, Context.MODE_PRIVATE)
                )
            )
            bw.write(data)
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
            stringtoByteArray(str)
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
