package com.example.healthmanagement

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.animation.AnimationUtils
import com.example.healthmanagement.DB.HealthDatabaseHelper
import com.example.healthmanagement.Dialog.DialogRegister
import com.example.healthmanagement.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity(), DialogRegister.DataCallBack {
    private lateinit var binding: ActivitySearchBinding

    private val helper = HealthDatabaseHelper(this)

    private val FoodMap: MutableList<Map<String, Any>> = mutableListOf()

    private var IdDate: Long = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dialog = DialogRegister()

        binding.day.setOnClickListener {
            dialog.show(supportFragmentManager,"dialog_basic")
        }

        binding.searchAdd.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        // EditTextから来た検索キーワード
        val a = intent.getStringExtra("food_search")

        val result = getFood(a)

        val food = result["Food"]

        val cal = result["Calorie"]

        val foodId = result["Id"]

        // 検索結果を TextView に表示
        if (food != null && cal != null) {
            binding.results.text = "${food}\n${cal}Kcal"
        } else binding.results.text = "'${a}'\nに関する情報がありません"



        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }


    override fun onData(data: String) {
// ダイアログクラスから来た値を、データベースに追加
        IdDate += helper.writableDatabase.let {
            val cv = ContentValues().apply {
                put("Date", data)
            }
            it.insert("DATE", null, cv)

        }
        startActivity(Intent(this, HistoryActivity::class.java))
    }


    // EditTextから来た文字列をデータベースから検索する
    private fun getFood(name: String?): Map<String, Any>{
        helper.readableDatabase.let {
            val a = it.query(
                "FOOD", arrayOf("IdFood","Food","Calorie"), "Food like ?", arrayOf("${name.toString()}%"),
                null, null, null
            )
            if (a.moveToNext()) {
                return mapOf<String, Any>(
                    "Id" to a.getInt(a.getColumnIndexOrThrow("IdFood")),
                    "Food" to a.getString(a.getColumnIndexOrThrow("Food")),
                    "Calorie" to a.getString(a.getColumnIndexOrThrow("Calorie"))
                )
            }
            a.close()
        }
        return mapOf()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            android.R.id.home->{
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}