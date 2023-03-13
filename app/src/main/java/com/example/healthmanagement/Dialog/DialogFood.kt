package com.example.healthmanagement.Dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.healthmanagement.HistoryDetail
import com.example.healthmanagement.R



class DialogFood : DialogFragment() {

    // DialogFragmentのコールバック
    interface FoodCallBack {
        fun onData(time: String, data: String, calorie: String )
    }

    // 食べ物名のコールバック
    private var callback: DialogFood.FoodCallBack? = null


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater;
            val custom = inflater.inflate(R.layout.custom_dialog, null)

            builder.setView(custom)
                .setPositiveButton(
                    R.string.register_item,
                    DialogInterface.OnClickListener { dialog, id ->
                        val historyDetail: HistoryDetail = activity as HistoryDetail

                        // ダイアログから、食べ物名を取得する処理
                        val editText : EditText = custom.findViewById(R.id.add_food)
                        val food = editText.text.toString()

                        // カロリーのデータを取得
                        val editText2 : EditText = custom.findViewById(R.id.add_calorie)
                        val calorie = editText2.text.toString()

                        // 時間のデータを取得
                        val editText3: EditText = custom.findViewById(R.id.addTime)
                        val time = editText3.text.toString()

                        // callback 処理 null チェック
                        if ("" != time && "" != food && "" != calorie){
                            callback?.onData(time, food, calorie)
                        } else Toast.makeText(activity, "時間、食べもの、カロリーを入力してくだい！！", Toast.LENGTH_SHORT).show()


                    })
                .setNegativeButton(R.string.cancel,
                    DialogInterface.OnClickListener { dialog, id ->
                        dialog.cancel()
                    })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = context as? FoodCallBack
        if (callback == null) {
            throw java.lang.ClassCastException("$callback 実装されていません")
        }
    }
}