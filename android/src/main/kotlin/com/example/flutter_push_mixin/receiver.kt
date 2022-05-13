package com.example.flutter_push_mixin

import android.content.ComponentName
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import com.mixpush.core.MixPushMessage
import com.mixpush.core.MixPushPlatform
import com.mixpush.core.MixPushReceiver
import io.flutter.plugin.common.EventChannel

class MixPushReceiver : MixPushReceiver() {

    private var reply: EventChannel.EventSink? = null

    public  fun initReply(event: EventChannel.EventSink?) {
        reply = event
    }

    override fun onRegisterSucceed(context: Context?, mixPushPlatform: MixPushPlatform?) {

        println("mixPush onRegisterSucceed ")
// 默认初始化5个推送平台（小米推送、华为推送、魅族推送、OPPO推送、VIVO推送），以小米推荐作为默认平台
    }

    override fun onNotificationMessageArrived(context: Context?, message: MixPushMessage?) {
        super.onNotificationMessageArrived(context, message)

        println("mixPush onNotificationMessageArrived -> 接收消息: ${message?.toString()}")
        setMessage(context, message)
    }


    override fun onNotificationMessageClicked(context: Context?, message: MixPushMessage?) {
        println("mixPush onNotificationMessageClicked -> 点击了消息: ${message?.toString()}")
        setMessage(context, message)
    }

    private fun setMessage(context: Context?, message: MixPushMessage?) {
        try{
            val componentName  = ComponentName(context!!.packageName, "${context!!.packageName}.MainActivity");
            println("mixPush setMessage 包名：${context!!.packageName}")
            val intent: Intent = Intent()
            //新开一个任务栈，这样当应用处于前台，再次打开MainActivity会走 NewIntent 方法
            //当应用处于杀死状态，会走onCreate方法
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
            intent.component = componentName;

            if(message != null){
                println("mixPush 写入intent 的消息: $message")
                intent.putExtra(BaseConstants.Extras, message.toString());//存入参数
                println("mixPush 写入intent 的消息2: ${intent.getStringExtra(BaseConstants.Extras)}")
            }

            context.startActivity(intent);


        }catch (e: Error){
            Log.i(TAG, "=============Exception:"+e.toString());
        }
    }
}
