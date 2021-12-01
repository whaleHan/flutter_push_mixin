package com.example.flutter_push_mixin

import androidx.annotation.NonNull
import com.mixpush.core.GetRegisterIdCallback
import com.mixpush.core.MixPushPlatform
import io.flutter.plugin.common.BasicMessageChannel

class GetId: GetRegisterIdCallback() {
    private lateinit var reply: BasicMessageChannel.Reply<Any>

    fun init(@NonNull reply: BasicMessageChannel.Reply<Any>) {
        this.reply = reply
    }


    override fun callback(platform: MixPushPlatform?) {
        val map = mapOf(
            "platformName" to platform?.platformName,
            "regId" to platform?.regId
        )

        reply.reply(map)
    }
}