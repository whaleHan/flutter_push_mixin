package com.example.flutter_push_mixin

import androidx.annotation.NonNull
import com.mixpush.core.GetRegisterIdCallback
import com.mixpush.core.MixPushPlatform
import io.flutter.plugin.common.BasicMessageChannel
import io.flutter.plugin.common.EventChannel

class GetId: GetRegisterIdCallback() {
    private var reply: EventChannel.EventSink? = null

    fun init(@NonNull reply: EventChannel.EventSink?) {
        this.reply = reply
        println("GetId init");
    }


    override fun callback(platform: MixPushPlatform?) {
//        println("获取到数据： ${platform.toString()}")
        if(platform?.regId == null) {
            reply?.success("获取regId 失败")
        }
        val map = mapOf(
            "platformName" to platform?.platformName,
            "regId" to platform?.regId
        )

        reply?.success(map)
    }
}