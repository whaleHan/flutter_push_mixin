package com.example.flutter_push_mixin

import android.content.ComponentName
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.NonNull
import com.mixpush.core.MixPushMessage
import com.mixpush.core.MixPushPlatform
import com.mixpush.core.MixPushReceiver
import io.flutter.plugin.common.BasicMessageChannel
import io.flutter.plugin.common.EventChannel

class MyPushReceiver : MixPushReceiver() {
    private var reply: EventChannel.EventSink? = null

    fun initPush(@NonNull reply: EventChannel.EventSink?) {
        this.reply = reply
    }

    @Override
    override fun onRegisterSucceed(context: Context?, mixPushPlatform: MixPushPlatform?) {
// 默认初始化5个推送平台（小米推送、华为推送、魅族推送、OPPO推送、VIVO推送），以小米推荐作为默认平台
    }

    @Override
    override fun onNotificationMessageClicked(context: Context?, message: MixPushMessage?) {
        // TODO 通知栏消息点击触发，实现打开具体页面，打开浏览器等。

        Log.i(TAG,"onNotificationMessageClicked -> 点击了消息: ${message?.toString()}")
        try{

            val componentName: ComponentName  = ComponentName(context!!.packageName, "${context!!.packageName}.MainActivity");
            val intent: Intent = Intent()
            //新开一个任务栈，这样当应用处于前台，再次打开MainActivity会走 NewIntent 方法
            //当应用处于杀死状态，会走onCreate方法
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
            intent.component = componentName;

            if(message != null){
                intent.putExtra(BaseConstants.Extras, message.toString());//存入参数
            }

            context.startActivity(intent);

            val map = mapOf(
                    "title" to message?.title,
                    "description" to message?.description,
                    "platform" to message?.platform,
                    "payload" to message?.payload,
                    "passThrough" to message?.isPassThrough
            )

//            reply?.success(map)
        }catch (e: Error){
            Log.i(TAG, "=============Exception:"+e.toString());
        }

    }

}
