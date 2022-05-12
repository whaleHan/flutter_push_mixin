import 'dart:async';
import 'dart:convert';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_apns_only/flutter_apns_only.dart';
import 'package:logger/logger.dart';
import 'package:notification_permissions/notification_permissions.dart';

class FlutterPushMixin {
  static const EventChannel _eventChannel = EventChannel(
      'flutter_push_mixin_event');

  static const MethodChannel _methodChannel = MethodChannel("flutter_push_mixin_method");

  static final Logger _log = Logger();

  static void getId() => _methodChannel.invokeMethod('getId');

  static Future initListener({
    required Function(RegIdModel model) getId,
    required Function(PushModel model) getMessage,
    required Function(String apnsToken) getApnsToken,
    Function(dynamic message)? eventLog
  }) async {
    _log.i("_eventChannel 通道名Flutter : ${_eventChannel.name}");

    PermissionStatus permissionStatus = await NotificationPermissions
        .requestNotificationPermissions(openSettings: false);
    _log.i("flutter_push_mixin -> 通知栏权限: $permissionStatus");
    if (Platform.isIOS) {
      final ApnsPushConnectorOnly connector = ApnsPushConnectorOnly();
      connector.configureApns(
          onMessage: (ApnsRemoteMessage message) async {
            _log.i("onMessage -> ${message.payload}");
            // final PushModel _model = PushModel(
            //   payload: jsonEncode(message.payload),
            // );
            // getMessage(_model);
          },
          onBackgroundMessage: (ApnsRemoteMessage message) async {
            _log.i("onBackgroundMessage -> ${message.payload}");
            // final PushModel _model = PushModel(
            //   payload: jsonEncode(message.payload),
            // );
            // getMessage(_model);
          },
        onLaunch: (ApnsRemoteMessage message) async {
          _log.i("onLaunch -> ${message.payload}");
          final PushModel _model = PushModel(
            payload: jsonEncode(message.payload),
          );
          getMessage(_model);
        },
        onResume: (ApnsRemoteMessage message) async {
            _log.i("onResume -> ${message.payload}");
          final PushModel _model = PushModel(
            payload: jsonEncode(message.payload),
          );
          getMessage(_model);
        }
      );
      connector.token.addListener(() {
        final String? _token = connector.token.value;
        if (_token != null) {
          getApnsToken(_token);
        }
      });
      connector.requestNotificationPermissions();
    } else {
      final _t = await _methodChannel.invokeMethod("init");
      _log.i('发送通知成功: $_t');

      _eventChannel.receiveBroadcastStream().asBroadcastStream().listen((message) {
        eventLog?.call(message);
        try{
          if (message == 'ok') {
            _log.d('flutter_push_mixin -> : 初始化完成');
          } else if (message is Map) {
            _log.d('Flutter 获取到的Map消息: $message');
            try{
              if (message['regId'] != null) {
                _log.d('flutter_push_mixin -> : 成功获取regId');

                RegIdModel _model = RegIdModel.fromJson(
                    Map<String, dynamic>.from(message));

                getId(_model);
              } else {
                _log.d('flutter_push_mixin -> : 获取到消息');

                PushModel _model = PushModel.fromJson(
                    Map<String, dynamic>.from(message) );

                getMessage(_model);
              }
            }catch(e) {
              _log.e("转换数据失败: $e");
            }
          } else if(message is String) {
            Map _message = jsonDecode(message) as Map;
            try{
              if (_message['regId'] != null) {
                _log.d('flutter_push_mixin -> : 成功获取regId');

                RegIdModel _model = RegIdModel.fromJson(
                    Map<String, dynamic>.from(_message));

                getId(_model);
              } else {
                _log.d('flutter_push_mixin -> : 获取到消息');

                PushModel _model = PushModel.fromJson(
                    Map<String, dynamic>.from(_message) );

                getMessage(_model);
              }
            }catch(e) {
              _log.e("转换数据失败: $e");
            }
          }
        }catch(e, s) {
          debugPrint("$s -> $e");
        }

      });
    }
  }
}

class RegIdModel {
  final String? platformName;
  final String? regId;

  const RegIdModel({required this.platformName, required this.regId});

  factory RegIdModel.fromJson(Map<String, dynamic> json) =>
      RegIdModel(
        platformName: json['platformName'],
        regId: json['regId'],
      );
}

class PushModel {
  final String? title;
  final String? description;
  final String? platform;
  final String? payload;
  final bool? passThrough;

  const PushModel({
    this.payload,
    this.description,
    this.title,
    this.platform,
    this.passThrough});

  factory PushModel.fromJson(Map<String, dynamic> json) =>
      PushModel(
        payload: json['payload'],
        platform: json['platform'],
        title: json['title'],
        description: json['description'],
        passThrough: json['passThrough'],
      );
}
