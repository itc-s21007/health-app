package com.example.healthmanagement

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.SyncStateContract.Constants
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.SimpleAdapter
import android.widget.Toast
import com.example.healthmanagement.DB.HealthDatabaseHelper
import com.example.healthmanagement.Dialog.DialogFood
import com.example.healthmanagement.databinding.ActivityHistoryDetailBinding


class HistoryDetail : AppCompatActivity(), DialogFood.FoodCallBack {
    private lateinit var binding: ActivityHistoryDetailBinding

    private var helper = HealthDatabaseHelper(this)

    //private val FOOD: MutableList<Map<String, Any>> = mutableListOf()
    private var FOOD: Array<String> = arrayOf()

    //lateinit var adapter: SimpleAdapter
    lateinit var adapter: ArrayAdapter<String>

    // TIMEテーブル FkDate の外部キーを設定するための変数
    private var FkDate: Int = 0

    private var diadata: MutableList<String> = mutableListOf()

    // カロリーを計算、表示させるための変数
    var addcalorie: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // HistoryActivity から来た値
        val txtName = intent.getIntExtra("history_list", 0)

        // getIntExtra したIDを代入
        val Data = getDate(txtName)

        // ↑ の値を代入
        FkDate = Data["id"] as Int

        binding.getDate.text = "${Data["date"]}"



        // 登録ボタンを押したときダイアログを表示
        binding.add.setOnClickListener {
            val dialog = DialogFood()
            dialog.show(supportFragmentManager, "dialog_basic")
        }
        // ホームに戻る
        binding.homeback.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }


        loadDate()


        adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            diadata

        )

        binding.foodList.adapter = adapter

        // 合計カロリーを表示させるカロリー
        binding.TotalKcal.text = "合計： ${addcalorie.toString()} Kcal"

        // おすすめの筋トレメニューを表示
        if (addcalorie < 2600) {
            binding.Tryining.text = "平均摂取カロリーまであと${2600 - addcalorie}"
        } else if (addcalorie > 2600) {
            binding.Tryining.text = getString(R.string.try3000)
        }
    }

    // TextView のためのデータベース検索機能
    val table = arrayOf("IdDate", "Date")
    private fun getDate(id: Int): Map<String, Any> {
        helper.readableDatabase.let {
            val c = it.query(
                "DATE", table, "IdDate = ?", arrayOf(id.toString()),
                null, null, null
            )
            if (c.moveToNext()) {
                return mapOf<String, Any>(
                    "id" to c.getInt(c.getColumnIndexOrThrow("IdDate")),
                    "date" to c.getString(c.getColumnIndexOrThrow("Date"))
                )
            }
            c.close()
        }
        return mapOf()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadDate(){
        val sql ="""
            select TIME.Time, FOOD.Food, FOOD.Calorie from TIME
            left outer join DATE on TIME.FkDate = DATE.IdDate 
            left outer join FOOD on TIME.FkFood = FOOD.IdFood
            where DATE.IdDate = ${FkDate}
        """
        helper.readableDatabase.let {
            it.rawQuery(sql,null).let {it ->
                if (it.moveToFirst()){
                    do {
                            val time = it.getString(it.getColumnIndexOrThrow("Time"))
                            val food = it.getString(it.getColumnIndexOrThrow("Food"))
                            val kcal = it.getString(it.getColumnIndexOrThrow("Calorie"))
                        diadata += "${time}   ${food}   ${kcal} Kcal"
                        addcalorie += kcal.toInt()
                    }while (it.moveToNext())
                } else mutableListOf<String>()
                it.close()
            }
        }
    }

    override fun onData(time: String, data: String, calorei: String) {
        diadata += "${time}   ${data}   ${calorei} Kcal"
//         ListView の更新
        adapter?.notifyDataSetChanged()
        // TextViewに更新をかける処理
        addcalorie += calorei.toInt()
        binding.TotalKcal.post { binding.TotalKcal.text = "合計： ${addcalorie.toString()} Kcal" }
        // TryingViewに更新をかける処理
        binding.Tryining.post {
            if (addcalorie < 2600) {
                binding.Tryining.text = "平均摂取カロリーまであと${2600 - addcalorie}"
            } else if (addcalorie > 2600) {
                binding.Tryining.text = getString(R.string.try3000)
            }
        }

        //データベースに食べ物内容をとろく
        val idFood = helper.writableDatabase.let {
            val FOOD = ContentValues().apply {
                put("Food", data)
                put("Calorie", calorei)
            }
            it.insert("FOOD", null, FOOD)
        }


        // TIME テーブルの値の追加
        helper.writableDatabase.let {
            val TIME = ContentValues().apply {
                put("Time", time)
                put("FkDate", FkDate)
                put("FkFood", idFood)
            }
            it.insert("TIME", null, TIME)
        }
    }
}


