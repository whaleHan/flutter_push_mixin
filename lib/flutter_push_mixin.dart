import 'dart:async';
import 'dart:convert';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_apns_only/flutter_apns_only.dart';
import 'package:logger/logger.dart';
import 'package:permission_handler/permission_handler.dart';

class FlutterPushMixin {
  static const BasicMessageChannel _channel = BasicMessageChannel(
      'flutter_push_mixin', StringCodec());

  static const String _init = 'init_channel_push_mix';

  static Logger log = Logger();

  static Stream<PushModel> initListener({
    required Function(RegIdModel model) getId,
    required Function(PushModel model) getMessage,
    required Function(String apnsToken) getApnsToken,
  }) async* {
    await Permission.notification.request();
    if (Platform.isIOS) {
      final ApnsPushConnectorOnly connector = ApnsPushConnectorOnly();
      connector.configureApns(
          onMessage: (ApnsRemoteMessage message) async{
            final PushModel _model = PushModel(
              payload: jsonEncode(message.payload),
            );
            getMessage(_model);
          }
      );
      connector.token.addListener(() {
        final String? _token = connector.token.value;
        if(_token != null) {
          getApnsToken(_token);
        }
      });
      connector.requestNotificationPermissions();
    } else {
      _channel.setMessageHandler((message) async {

        log.d('flutter_push_mixin -> : $message');

        if (message == 'ok') {
          log.d('flutter_push_mixin -> : 初始化完成');
        } else if (message is Map) {
          if (message['regId'] != null) {
            log.d('flutter_push_mixin -> : 成功获取regId');

            RegIdModel _model = RegIdModel.fromJson(
                message as Map<String, dynamic>);

            getId(_model);
          } else {
            log.d('flutter_push_mixin -> : 获取到消息');

            PushModel _model = PushModel.fromJson(
                message as Map<String, dynamic>);

            getMessage(_model);
          }
        }
      });

      _channel.send(_init);
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
