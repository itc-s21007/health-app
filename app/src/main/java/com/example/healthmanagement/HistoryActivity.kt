package com.example.healthmanagement

import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.SimpleAdapter
import android.widget.TextView
import com.example.healthmanagement.DB.HealthDatabaseHelper
import com.example.healthmanagement.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding

    private val helper = HealthDatabaseHelper(this)

    lateinit var adapter: SimpleAdapter

    private val _id: MutableList<Map<String, Any>> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        loadData()

        // SimpleAdapter を使ってListView にdataを表示
        adapter = SimpleAdapter(
            this,
            _id,
            android.R.layout.simple_list_item_1,
            arrayOf("date"),
            intArrayOf(android.R.id.text1)
        )
        binding.historyList.adapter = adapter


        // ListView をタップ＆長押ししたときの処理
        binding.historyList.setOnItemClickListener { parent, view, position, id ->
            val itemTextView : TextView = view.findViewById(android.R.id.text1)
            startActivity(Intent(this, HistoryDetail::class.java).apply {
                // TIME テーブルと結びつけるための DATE の IdDate をおくる。
                putExtra("history_list", _id[position]["id"] as Int)
            })
        }
    }

    private fun loadData() {
        // 日付のテーブルを検索して、listView に表示する
        helper.readableDatabase.let { it ->
            val c = it.query(
                "DATE", arrayOf( "IdDate", "Date"), null,
                null, null, null, null)
            while(c.moveToNext()){
                val item = mapOf<String, Any>(
                    "id" to c.getInt(c.getColumnIndexOrThrow("IdDate")),
                    "date" to c.getString(c.getColumnIndexOrThrow("Date"))
                )
                _id += item
            }
            c.close()
        }
    }


    // 戻るボタンの追加
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            android.R.id.home->{
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}


