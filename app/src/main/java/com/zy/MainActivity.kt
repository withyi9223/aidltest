package com.zy

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.zy.data.MessageModel
import kotlinx.android.synthetic.main.activity_main.*

/**
 * ━━━━━━神兽出没━━━━━━
 * 　　　┏┓　　　┏┓
 * 　　┏┛┻━━━┛┻┓
 * 　　┃　　　　　　　┃
 * 　　┃　　　━　　　┃
 * 　　┃　┳┛　┗┳　┃
 * 　　┃　　　　　　　┃
 * 　　┃　　　┻　　　┃
 * 　　┃　　　　　　　┃
 * 　　┗━┓　　　┏━┛Code is far away from bug with the animal protecting
 * 　　　　┃　　　┃    神兽保佑,代码无bug
 * 　　　　┃　　　┃
 * 　　　　┃　　　┗━━━┓
 * 　　　　┃　　　　　　　┣┓
 * 　　　　┃　　　　　　　┏┛
 * 　　　　┗┓┓┏━┳┓┏┛
 * 　　　　　┃┫┫　┃┫┫
 * 　　　　　┗┻┛　┗┻┛
 * ━━━━━━感觉萌萌哒━━━━━━
 *
 *
 * Created by yi on 2020/4/13.
 */
class MainActivity : AppCompatActivity() {

    private var messageSender: MessageSender? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tv_start.setOnClickListener {
            startService()
            Log.e("Main", MyApplication.application.processName())
        }
    }

    private fun startService() {
        val intent = Intent(this, MessageService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
        startService(intent)
    }

    private var connection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Log.e("Main", "onServiceConnected")
            messageSender = MessageSender.Stub.asInterface(service)
            val model = MessageModel()
            model.from = "client user id"
            model.to = "receiver user id"
            model.content = "This is message content"
            try {
                messageSender?.registerReceiveListener(messageReceiver)
                messageSender?.sendMessage(model)
                //binder 死忙监听
                messageSender?.asBinder()?.linkToDeath(deathRecipient, 0)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Log.e("Main", "onServiceDisconnected")
        }
    }

    private val messageReceiver = object : MessageReceiver.Stub() {
        override fun onMessageReceived(messageModel: MessageModel?) {
            Log.e("Main", "onMessageReceived" + messageModel?.toString())
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        if (messageSender != null && messageSender?.asBinder()?.isBinderAlive == true) {
            messageSender?.unregisterReceiveListener(messageReceiver)
        }
        unbindService(connection)

    }

    /**
     * Binder 可能意外終止，client监听到binder死忙之后可以进行重连操作等
     * */
    val deathRecipient = object : IBinder.DeathRecipient {
        override fun binderDied() {
            Log.e("Main", "binderDied")
            if (messageSender != null) {
                messageSender!!.asBinder().unlinkToDeath(this, 0)
                messageSender = null
            }
            startService()
        }

    }
}