package com.example.network_termproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.network_termproject.databinding.SelectFriendLayoutBinding
import com.example.network_termproject.network.Client
import com.example.network_termproject.network.NetData
import com.example.network_termproject.recycler.SelectAdapter
import java.nio.ByteBuffer
import java.util.*
import java.util.function.Consumer

class SelectFriend : AppCompatActivity() {

    private var binding: SelectFriendLayoutBinding? = null
    private var users: ArrayList<String>? = null
    private var selectAdapter: SelectAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SelectFriendLayoutBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        users = ArrayList()
        selectAdapter = SelectAdapter(users!!)

        binding!!.apply {
            selectRecyclerView.layoutManager = LinearLayoutManager(this@SelectFriend)
            selectRecyclerView.adapter = selectAdapter
            selectRecyclerView.setHasFixedSize(true)
        }
        setSupportActionBar(binding!!.selectToolBar)
        Objects.requireNonNull(supportActionBar)?.setDisplayHomeAsUpEnabled(true)
        binding!!.selectOkButton.setOnClickListener {
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
        header.putChar('s')
        header.putInt(mainData.data.toString().toByteArray().size)
        header.flip()

        var buffer = ByteBuffer.allocate(6 + mainData.data.toString().toByteArray().size)
        buffer.apply {
            put(header)
            put(mainData.data.toString().toByteArray())
        }
        buffer.flip()

        Client.instance.write(buffer)
    }

    private fun setList(serverData: NetData) {
        users!!.clear()
        //        users.addAll(serverData.getList());
//        selectAdapter.notifyDataSetChanged();
        runOnUiThread {
            for (i in 0 until serverData.getList().size) {
                users!!.add(serverData.getList()[i])
                selectAdapter!!.notifyItemInserted(i)
            }
        }
    }
}