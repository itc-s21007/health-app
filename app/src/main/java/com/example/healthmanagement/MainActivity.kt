package com.example.healthmanagement

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import com.example.healthmanagement.DB.HealthDatabaseHelper
import com.example.healthmanagement.Dialog.DialogRegister
import com.example.healthmanagement.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), DialogRegister.DataCallBack {
    private lateinit var binding : ActivityMainBinding

    private val helper = HealthDatabaseHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val helper = HealthDatabaseHelper(this)



        binding.searchButton.setOnClickListener {
            if (binding.Search.text.length > 0) {
                startActivity(Intent(this, SearchActivity::class.java).apply {
                    putExtra("food_search", binding.Search.text.toString())
                })
            }
        }


    }
    // オプションメニューバーの表示
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    // ItemSelect した時の処理
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val dialog = DialogRegister()


        when(item.itemId) {
            R.id.history -> startActivity(Intent(this, HistoryActivity::class.java))
            R.id.register -> dialog.show(supportFragmentManager, "dialog_basic")
        }

        Toast.makeText(
            this, "日付登録画面", Toast.LENGTH_SHORT).show()
        return true
    }

    override fun onData(data: String) {
        // ダイアログクラスから来た値を、データベースに追加
        helper.writableDatabase.let {
            val cv = ContentValues().apply {
                put("Date", data)
            }
            it.insert("DATE", null, cv)

        }
        startActivity(Intent(this, HistoryActivity::class.java))
    }
}