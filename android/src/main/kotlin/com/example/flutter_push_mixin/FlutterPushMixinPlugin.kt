package com.example.flutter_push_mixin

import android.content.Context
import androidx.annotation.NonNull
import com.mixpush.core.MixPushClient

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.BasicMessageChannel
import io.flutter.plugin.common.BasicMessageChannel.MessageHandler
import io.flutter.plugin.common.StandardMessageCodec

class FlutterPushMixinPlugin() : FlutterPlugin, MessageHandler<Any>{
    private var initChannel: String = "init_channel_push_mix"

    private var push: MyPushReceiver = MyPushReceiver()

    private var pushClient: MixPushClient = MixPushClient()

    private var getId: GetId = GetId()

    private lateinit var channel: BasicMessageChannel<Any>

    private lateinit var context: Context

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = BasicMessageChannel(flutterPluginBinding.binaryMessenger, "flutter_push_mixin", StandardMessageCodec.INSTANCE)

        channel.setMessageHandler(this)

        context = flutterPluginBinding.applicationContext
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMessageHandler(null)
    }

    override fun onMessage(message: Any?, reply: BasicMessageChannel.Reply<Any>) {
        when (message) {
            initChannel -> {
                push.initPush(reply)

                getId.init(reply)

                pushClient.setPushReceiver(push)

                pushClient.getRegisterId(context, getId)

                reply.reply("ok")
            }
            else -> {
                print("收到消息: $message")
            }
        }
    }
}
