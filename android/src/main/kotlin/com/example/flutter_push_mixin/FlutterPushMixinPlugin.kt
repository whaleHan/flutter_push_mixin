package com.example.flutter_push_mixin

import android.content.*
import androidx.annotation.NonNull
import com.mixpush.core.MixPushClient

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.*

class FlutterPushMixinPlugin() : FlutterPlugin, MethodChannel.MethodCallHandler, EventChannel.StreamHandler, ActivityAware {

    private var pushReceiver: MixPushReceiver = MixPushReceiver()

    private var pushClient: MixPushClient = MixPushClient()

    private var getId: GetId = GetId()

    private lateinit var methodChannel: MethodChannel

    private lateinit var eventChannel: EventChannel

    private lateinit var context: Context

    private var eventSink: EventChannel.EventSink? = null

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {

        println("_eventChannel 通道名Android : ${BaseConstants.initChannelStr}")

        eventChannel = EventChannel(flutterPluginBinding.binaryMessenger, BaseConstants.initChannelStr)
        eventChannel.setStreamHandler(this)
        methodChannel = MethodChannel(flutterPluginBinding.binaryMessenger, BaseConstants.methodChannelStr)
        methodChannel.setMethodCallHandler(this)

        context = flutterPluginBinding.applicationContext


        println("mixPush 注册通道 setPushReceiver(MyPushReceiver())")
        pushClient.setPushReceiver(MixPushReceiver())
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
            "getId" -> {
                println("获取id getId")

                pushClient.getRegisterId(context, getId)
            }
            else -> {
                println("接受到消息: ${call.method}")
                result.notImplemented()
            }
        }
    }

    private fun initPushCtrl() {
        println("eventSink 是否为空：${eventSink == null}")
        pushReceiver.initReply(eventSink)

        pushClient.setPushReceiver(pushReceiver)

        //发送应用关闭时的消息
        sendPushMessageByClose()

        getId.init(eventSink)

        pushClient.register(context)

        println("开始获取Id")
        pushClient.getRegisterId(context, getId)

        eventSink?.success("ok")
    }

    //将应用关闭时存储的消息上传
    private fun sendPushMessageByClose() {
        println("mixPush调用: sendPushMessageByClose")

        val data: String? = getPushRecevieMsg()
        if (data != null) {
            eventSink?.success(data)
        }
    }

    //获取应用关闭时存储的消息
    private fun getPushRecevieMsg(): String? {
        println("mixPush调用: getPushRecevieMsg")
        val preferences: SharedPreferences = context.getSharedPreferences("Push", Context.MODE_PRIVATE);
        val data: String? = preferences.getString("push", "");
        println("mixPush调用: getPushRecevieMsg 获取到的数据: $data")
        val editor: SharedPreferences.Editor = preferences.edit();
        //清理数据
        if (data?.isNotEmpty() == true) {
            editor.remove("push");
            editor.commit();
        }
        return data
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        println("mixPush调用: onAttachedToActivity")
        binding.addOnNewIntentListener(fun(intent: Intent?): Boolean {
            println("mixPush调用: onAttachedToActivity addOnNewIntentListener")
            intent?.let { getIntentData(it) }
            return false;
        })
        getIntentData(binding.activity.intent)
    }

    override fun onDetachedFromActivityForConfigChanges() {

    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        println("mixPush调用: onReattachedToActivityForConfigChanges")
        binding.addOnNewIntentListener(fun(intent: Intent?): Boolean {
            println("mixPush调用: onReattachedToActivityForConfigChanges addOnNewIntentListener")
            intent?.let { getIntentData(it) }
            return false;
        })
        getIntentData(binding.activity.intent)
    }

    override fun onDetachedFromActivity() {
        TODO("Not yet implemented")
    }

    //获取intentData
    private fun getIntentData(intent: Intent) {
        println("mixPush调用: getIntentData")
        if (intent.extras != null && intent.getStringExtra(BaseConstants.Extras) != null) {

            val content: String? = intent.getStringExtra(BaseConstants.Extras);
            println("mixPush save receive data from push, data = $content");

            if (content != null) {
                eventSink?.success(content)
                intent.putExtra(BaseConstants.Extras, "");//存入参数
            }
        } else {
            println("intent is null 从componentName 获取数据");

            val componentName = ComponentName(context.packageName, "${context.packageName}.MainActivity")


            println("mixPush getIntentData 包名：${context.packageName}")

            val intent2 = Intent()
            //新开一个任务栈，这样当应用处于前台，再次打开MainActivity会走 NewIntent 方法
            //当应用处于杀死状态，会走onCreate方法
            intent2.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent2.component = componentName

            intent2.getStringExtra(BaseConstants.Extras)
            val content: String? = intent2.getStringExtra(BaseConstants.Extras)

            println("mixPush 从componentName 里获取的消息, data = $content")

            if (content != null) {
                eventSink?.success(content)
                intent.putExtra(BaseConstants.Extras, "");//存入参数
            }
        }

    }

}
