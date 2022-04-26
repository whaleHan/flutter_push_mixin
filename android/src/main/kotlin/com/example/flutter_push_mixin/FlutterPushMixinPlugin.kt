package com.example.flutter_push_mixin

import android.content.Context
import androidx.annotation.NonNull
import com.mixpush.core.MixPushClient

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.*
import io.flutter.plugin.common.BasicMessageChannel.MessageHandler

class FlutterPushMixinPlugin() : FlutterPlugin, MethodChannel.MethodCallHandler, EventChannel.StreamHandler{
    private var initChannelStr: String = "flutter_push_mixin_event"
    private var methodChannelStr: String = "flutter_push_mixin_method"

    private var push: MyPushReceiver = MyPushReceiver()

    private var pushClient: MixPushClient = MixPushClient()

    private var getId: GetId = GetId()

    private lateinit var methodChannel: MethodChannel
    private lateinit var eventChannel: EventChannel

    private lateinit var context: Context

    private var eventSink: EventChannel.EventSink? = null

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {

        println("_eventChannel 通道名Android : $initChannelStr")

        eventChannel = EventChannel(flutterPluginBinding.binaryMessenger, initChannelStr)
        eventChannel.setStreamHandler(this)
        methodChannel = MethodChannel(flutterPluginBinding.binaryMessenger, methodChannelStr)
        methodChannel.setMethodCallHandler(this)

        context = flutterPluginBinding.applicationContext
    }

    /**
     * This `FlutterPlugin` has been removed from a [ ] instance.
     *
     *
     * The `binding` passed to this method is the same instance that was passed in [ ][.onAttachedToEngine]. It is provided again in this method as a
     * convenience. The `binding` may be referenced during the execution of this method, but it
     * must not be cached or referenced after this method returns.
     *
     *
     * `FlutterPlugin`s should release all resources in this method.
     */
    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        methodChannel.setMethodCallHandler(null)
    }

    /**
     * Handles a request to set up an event stream.
     *
     *
     * Any uncaught exception thrown by this method will be caught by the channel implementation
     * and logged. An error result message will be sent back to Flutter.
     *
     * @param arguments stream configuration arguments, possibly null.
     * @param events an [EventSink] for emitting events to the Flutter receiver.
     */
    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        println("onListen 接受到的消息： $arguments")
        eventSink = events
        initPushCtrl()
    }

    /**
     * Handles a request to tear down the most recently created event stream.
     *
     *
     * Any uncaught exception thrown by this method will be caught by the channel implementation
     * and logged. An error result message will be sent back to Flutter.
     *
     *
     * The channel implementation may call this method with null arguments to separate a pair of
     * two consecutive set up requests. Such request pairs may occur during Flutter hot restart. Any
     * uncaught exception thrown in this situation will be logged without notifying Flutter.
     *
     * @param arguments stream configuration arguments, possibly null.
     */
    override fun onCancel(arguments: Any?) {
        eventSink = null
    }

    /**
     * Handles the specified method call received from Flutter.
     *
     *
     * Handler implementations must submit a result for all incoming calls, by making a single
     * call on the given [Result] callback. Failure to do so will result in lingering Flutter
     * result handlers. The result may be submitted asynchronously and on any thread. Calls to
     * unknown or unimplemented methods should be handled using [Result.notImplemented].
     *
     *
     * Any uncaught exception thrown by this method will be caught by the channel implementation
     * and logged, and an error result will be sent back to Flutter.
     *
     *
     * The handler is called on the platform thread (Android main thread). For more details see
     * [Threading in
 * the Flutter Engine](https://github.com/flutter/engine/wiki/Threading-in-the-Flutter-Engine).
     *
     * @param call A [MethodCall].
     * @param result A [Result] used for submitting the result of the call.
     */
    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        println("接受到MethodCall 的消息 -> ${call.method}")
        when (call.method) {
            "init" -> {
                println("开始初始化: ${call.method}")
                result.success(true)
            }
            else -> {
                println("接受到消息: ${call.method}")
                result.notImplemented()
            }
        }
    }

    private fun initPushCtrl() {
        println("eventSink 是否为空：${eventSink == null}")
        push.initPush(eventSink)

        getId.init(eventSink)

        pushClient.setPushReceiver(push)

        println("开始获取Id")
        pushClient.getRegisterId(context, getId)

        eventSink?.success("ok")
    }

}
