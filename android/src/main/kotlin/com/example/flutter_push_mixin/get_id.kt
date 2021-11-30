package com.example.flutter_push_mixin

import androidx.annotation.NonNull
import com.mixpush.core.GetRegisterIdCallback
import com.mixpush.core.MixPushPlatform
import io.flutter.plugin.common.EventChannel
import java.util.*

class GetId: GetRegisterIdCallback() {
    private var events: EventChannel.EventSink? = null

    fun init(@NonNull events: EventChannel.EventSink?) {
        this.events = events;
    }


    override fun callback(platform: MixPushPlatform?) {
        val map = mapOf(
            "platformName" to platform?.platformName,
            "regId" to platform?.regId
        )

        events?.success(map);
    }
}