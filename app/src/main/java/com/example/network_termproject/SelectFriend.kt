package com.example.network_termproject

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.network_termproject.network.Client
import com.example.network_termproject.network.NetData
import com.example.network_termproject.recycler.SelectAdapter
import kotlinx.android.synthetic.main.select_friend_layout.*
import java.nio.ByteBuffer
import java.util.*
import java.util.function.Consumer

class SelectFriend : AppCompatActivity() {

    private var users: ArrayList<String>? = null
    private var selectAdapter: SelectAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_friend_layout)

        users = ArrayList()
        selectAdapter = SelectAdapter(users!!)

        select_recyclerView.apply {
            layoutManager = LinearLayoutManager(this@SelectFriend)
            adapter = selectAdapter
            setHasFixedSize(true)
        }

        setSupportActionBar(select_toolBar)
        Objects.requireNonNull(supportActionBar)?.setDisplayHomeAsUpEnabled(true)
        select_okButton.setOnClickListener {
            val intent = Intent()
            intent.putExtra("selectedUserList", selectAdapter!!.checkedUsers)
            setResult(RESULT_OK, intent)
            finish()
        }
        requestList()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.home) {
            val intent = Intent()
            setResult(RESULT_CANCELED, intent)
            finish()
        }
        return false
    }

    private fun requestList() {
        val builder = NetData.Builder()
        Client.instance.setList(Consumer { serverData: NetData? -> setList(serverData!!) })
        val mainData = builder.setType("requestList").setUserId(Client.instance.id!!).build()

        val header = ByteBuffer.allocate(6)
        header.apply {
            putChar('s')
            putInt(mainData.data.toString().toByteArray().size)
            flip()
        }

        var buffer = ByteBuffer.allocate(6 + mainData.data.toString().toByteArray().size)
        buffer.apply {
            put(header)
            put(mainData.data.toString().toByteArray())
            flip()
        }
        Client.instance.write(buffer)
    }

    private fun setList(serverData: NetData) {
        users!!.clear()
        runOnUiThread {
            for (i in 0 until serverData.getList().size) {
                users!!.add(serverData.getList()[i])
                selectAdapter!!.notifyItemInserted(i)
            }
        }
    }
}