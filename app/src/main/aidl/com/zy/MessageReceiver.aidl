// MessageReceiver.aidl
package com.zy;
import com.zy.data.MessageModel;

interface MessageReceiver {
    
    void onMessageReceived(in MessageModel messageModel);
    
}
