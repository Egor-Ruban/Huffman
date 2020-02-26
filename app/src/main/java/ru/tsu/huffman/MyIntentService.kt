package ru.tsu.huffman

import android.app.IntentService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.io.*


const val INPUT_TEXT = "inputText"
const val INPUT_CODE = "inputCode"
const val PATH = "path"

const val ACTION_DECOMPRESS = "ACTION_DECOMPRESS"
const val ACTION_COMPRESS = "ACTION_COMPRESS"

private val LOG_TAG = "myLogs"

private lateinit var fileToWork : String

private var currentID = -1

private var sendTimes = 0

class CoderService : IntentService("TextFileCoder") {

    override fun onHandleIntent(intent: Intent?) {
        createNotificationChannel()
        sendTimes = 0
        Log.d(LOG_TAG, "on handle")
        when (intent?.action) {
            ACTION_DECOMPRESS -> {
                val path = intent.getStringExtra(PATH)
                onHandleDecompress(path)
            }
            ACTION_COMPRESS -> {
                val path = intent.getStringExtra(PATH)
                onHandleCompress(path)
            }
        }
    }

    fun onHandleCompress(path : String ){
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        builder = NotificationCompat.Builder(this, "my_notes")
            .setSmallIcon(R.drawable.ic_notification_24dp)
            .setContentTitle("file compressing")
            .setContentText(path)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)


        val fileToRead = path
        val fileToWrite = "${File(path).parent}/${File(path).name.replace(".txt", "_coded.hfm")}"
        fileToWork = fileToWrite
        val inputText = readFileBin(fileToRead)
        Coder.compress(inputText)

        Log.d(LOG_TAG, "end of compressing")



    }

    fun onHandleDecompress(path : String){
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        builder = NotificationCompat.Builder(this, "CoderChannel")
            .setSmallIcon(R.drawable.ic_notification_24dp)
            .setContentTitle("file decompressing")
            .setContentText(path)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        val fileToRead = path

        val fileToWrite = "${File(path).parent}/${File(path).name.replace(".hfm", "_decoded.txt")}"
        fileToWork = fileToWrite
        val inputText = readFileBin(fileToRead)
        Decoder.decode(inputText)


        Log.d(LOG_TAG, "end of compressing")


        //with(NotificationManagerCompat.from(this)) {
        //    // notificationId is a unique int for each notification that you must define
        //    notify(2, builder.build())
        //}
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "my notifications"
            val descriptionText = "test notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("CoderChannel", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }



    private fun readFileBin(fileToRead: String) : ByteArray{
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


    companion object {

        lateinit var builder : NotificationCompat.Builder

        @JvmStatic
        fun startDecompressing(context: Context, path : String, inputCode : ByteArray) {
            val intent = Intent(context, CoderService::class.java).apply {
                action = ACTION_DECOMPRESS
                putExtra(INPUT_CODE, inputCode)
                putExtra(PATH, path)
            }
            context.startService(intent)
        }

        @JvmStatic
        fun startCompressing(context: Context, path : String, inputText : ByteArray) {
            Log.d(LOG_TAG, "comp start")
            val intent = Intent(context, CoderService::class.java).apply {
                action = ACTION_COMPRESS
                putExtra(INPUT_TEXT, inputText)
                putExtra(PATH, path)
            }
            context.startService(intent)
        }

        private fun writeBinFile(path: String, data : ByteArray) {
            try {
                val f = File(path)
                f.createNewFile()
                val stream = DataOutputStream(
                    FileOutputStream(f, false)
                )
                stream.write(data)
                stream.close()
                Log.d(LOG_TAG, "Файл записан $path")
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        fun appendBinFile(path: String, data : ByteArray) {
            try {
                val f = File(path)
                f.createNewFile()
                val stream = DataOutputStream(
                    FileOutputStream(f, true)
                )
                stream.write(data)
                stream.close()
                Log.d(LOG_TAG, "Файл обновлен $path")
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        fun updateInfo(toPrint : ByteArray, passed : Int, all : Int){
            if(sendTimes == 0){
                writeBinFile(fileToWork, toPrint)
            } else {
                appendBinFile(fileToWork, toPrint)
                with(NotificationManagerCompat.from(CoderService.builder.mContext)) {
                    CoderService.builder.setProgress(all, passed, false)
                    notify(currentID, CoderService.builder.build())
                }
            }
            sendTimes++
        }

        fun createNotification(all : Int){
            currentID++
            with(NotificationManagerCompat.from(CoderService.builder.mContext)){
                CoderService.builder.setProgress(all,0,false)
                notify(currentID, CoderService.builder.build())
            }
        }

        fun sendLastNotification(){
            with(NotificationManagerCompat.from(CoderService.builder.mContext)){
                builder.setProgress(0, 0,false)
                builder.setContentTitle("decompressing finished")
                notify(currentID, CoderService.builder.build())
            }
        }

        fun sendHeader(header : ByteArray){
            sendTimes++
            writeBinFile(fileToWork, header)
        }
    }

}