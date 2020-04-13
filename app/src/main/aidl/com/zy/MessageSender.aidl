// MessageSender.aidl
package com.zy;
import com.zy.data.MessageModel;
import com.zy.MessageReceiver;

interface MessageSender {
   
    void sendMessage(in MessageModel messageModel);
    
    void registerReceiveListener(MessageReceiver messageReceiver);
    
    void unregisterReceiveListener(MessageReceiver messageReceiver);
}
