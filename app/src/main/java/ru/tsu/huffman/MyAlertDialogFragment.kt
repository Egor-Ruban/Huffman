package ru.tsu.huffman

import android.R
import android.app.AlertDialog
import android.app.Dialog
import android.app.PendingIntent.getActivity
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment


class MyAlertDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = getArguments()!!.getString("title")
        return AlertDialog.Builder(getActivity())
            .setTitle(title)
            .setPositiveButton("OK"){dialog, which ->  }
            .setMessage("Replace User and then Click OK")
            .create()
    }

    companion object {
        fun newInstance(): MyAlertDialogFragment {
            val frag = MyAlertDialogFragment()
            val args = Bundle()
            args.putString("title", "User Error")
            frag.setArguments(args)
            return frag
        }
    }
}