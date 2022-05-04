package com.example.flutter_push_mixin

import android.content.Context
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
        val map = mapOf(
            "title" to message?.title,
            "description" to message?.description,
            "platform" to message?.platform,
            "payload" to message?.payload,
            "passThrough" to message?.isPassThrough
        )

        reply?.success(map)
    }
}
