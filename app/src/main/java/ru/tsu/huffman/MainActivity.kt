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


class MainActivity : AppCompatActivity() {

    val LOG_TAG = "myLogs"

    var fileToRead = "default.txt"
    var fileToWrite = fileToRead

    override fun onCreate(savedInstanceState: Bundle?) {
        fileToRead = "$filesDir/$fileToRead"
        fileToWrite = fileToRead
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mainl)

        btn_code.setOnClickListener {
            val data = byteArrayOf(48, 49, 50, -1, 0, 51, 10, 52) // пример данных
            writeFile(byteArrayToCharArray(data))
        }

        btn_decode.setOnClickListener {
            readFile()
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
                    Log.i("MY", path) // что делать с выбранным элементом
                    fileToRead = path
                    val a = getDir("Huffman", Context.MODE_PRIVATE).createNewFile()
                    Log.d(LOG_TAG, "${File(path).name} $path and $a")
                    fileToWrite = "${File(path).parent}/${File(path).name.replace(".txt", ".hfm")}"
                    readFile()

                } else {
                    Toast.makeText(this,"wrong type",Toast.LENGTH_SHORT).show()
                }
                // e.g /storage/emulated/0/Documents/file.txt
                //добавить обработку файлов


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
        return ba
    }

    private fun charArrayToByteArray(data : CharArray) : ByteArray{
        val ba = ByteArray(data.size)
        for(i in data.indices){
            ba[i] = data[i].toByte()
        }
        for(i in ba.indices){
            //Log.d(LOG_TAG, "ba is ${ba[i]}")
        }
        return ba
    }

    fun writeFile(data : CharArray) {
        //а можно было не любиться, а поменять вывод на DataOutputStream
        //возможно в следующей жизни...
        try { // отрываем поток для записи
            val f = File("$fileToWrite")
            f.createNewFile()
            val bw = BufferedWriter(
                OutputStreamWriter(
                    FileOutputStream(f, false)
                )
            )
            //bw.write(et_input.text.toString())
            bw.write(data)
            // закрываем поток
            bw.close()
            Log.d(LOG_TAG, "Файл записан $fileToWrite")
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
            Log.d(LOG_TAG, "psh")
            val f = File(fileToRead)
            val br = BufferedReader(
                InputStreamReader(
                    FileInputStream(f)
                )
            )
            Log.d(LOG_TAG, "psh")
            var str: String = br.readText()
            // читаем содержимое
            Log.d(LOG_TAG, "read all $str")
            tv_file.visibility = View.VISIBLE
            tv_file.text = str
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
