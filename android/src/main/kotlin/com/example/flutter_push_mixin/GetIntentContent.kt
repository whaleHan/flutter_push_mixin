package com.example.flutter_push_mixin

import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import androidx.annotation.NonNull
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.PluginRegistry

class GetIntentContent: PluginRegistry.NewIntentListener {
    private var reply: EventChannel.EventSink? = null

    fun initPush(@NonNull reply: EventChannel.EventSink?) {
        this.reply = reply
    }

    override fun onNewIntent(intent: Intent): Boolean {
        Log.i(TAG, "onNewIntent: $intent")
        //获取intentData
        getIntentData(intent);
        return false
    }

    //获取intentData
    private fun getIntentData(intent: Intent ) {
        if (intent.extras != null && intent.getStringExtra(BaseConstants.Extras) != null) {

            val content: String?  = intent.getStringExtra(BaseConstants.Extras);
            Log.i(TAG, "save receive data from push, data = $content");

            if(content != null && reply != null) {
                pushMsgEvent(content);

                intent.putExtra(BaseConstants.Extras, "");//存入参数
            }
        } else {
            Log.i(TAG, "intent is null");
        }

    }



    //发送消息至flutter
    private fun pushMsgEvent(content: String ) {
        reply?.success(content)

    }

}