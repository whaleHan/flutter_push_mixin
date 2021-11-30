package com.example.flutter_push_mixin

import android.content.Context
import androidx.annotation.NonNull
import com.mixpush.core.GetRegisterIdCallback
import com.mixpush.core.MixPushClient

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.EventChannel.StreamHandler

/** FlutterPushMixinPlugin */
class FlutterPushMixinPlugin : FlutterPlugin, StreamHandler {

    private var initChannel: String = "init_channel_push_mix";

    private var push: MyPushReceiver = MyPushReceiver();

    private var pushClient: MixPushClient = MixPushClient();

    private var getId: GetId = GetId();

    private lateinit var channel: EventChannel;

    private lateinit var context: Context;

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = EventChannel(flutterPluginBinding.binaryMessenger, "flutter_push_mixin")

        channel.setStreamHandler(this);

        context = flutterPluginBinding.applicationContext;
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setStreamHandler(null);
    }

    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        when (arguments) {
            initChannel -> {
                push.initPush(events);

                getId.init(events);

                pushClient.setPushReceiver(push);


                pushClient.getRegisterId(context, getId)

                events?.success("ok");
            }
            else -> {
                events?.endOfStream();
            }
        }
    }

    override fun onCancel(arguments: Any?) {
//        channel.setStreamHandler(null)
    }
}
