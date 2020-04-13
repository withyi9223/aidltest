package com.zy

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Message
import android.os.RemoteCallbackList
import android.util.Log
import com.zy.data.MessageModel
import java.util.concurrent.atomic.AtomicBoolean

class MessageService : Service() {

    var listenerList = RemoteCallbackList<MessageReceiver>()
    var serviceStop = AtomicBoolean(false)

    override fun onBind(intent: Intent): IBinder {
        return messageSender
    }

    private val messageSender = object : MessageSender.Stub() {
        override fun sendMessage(messageModel: MessageModel?) {
            Log.e("service", "messageModel:" + messageModel.toString())
        }

        override fun unregisterReceiveListener(messageReceiver: MessageReceiver?) {
            listenerList.unregister(messageReceiver)
        }

        override fun registerReceiveListener(messageReceiver: MessageReceiver?) {
            listenerList.register(messageReceiver)
        }

    }

    override fun onCreate() {
        super.onCreate()
        Thread(FakeTCPTask()).start()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceStop.set(true)
    }

    private inner class FakeTCPTask() : Runnable {
        override fun run() {
            Thread.sleep(5000)
            val model = MessageModel()
            model.from = "service"
            model.to = "client"
            model.content = System.currentTimeMillis().toString()

            var count = listenerList.beginBroadcast()
            Log.e("service:", "count:$count")
            for (i in 0 until count) {
                var receiver = listenerList.getBroadcastItem(i)
                receiver?.onMessageReceived(model)
            }
        }

    }

}
