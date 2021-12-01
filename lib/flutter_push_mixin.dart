import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:logger/logger.dart';

class FlutterPushMixin {
  static const BasicMessageChannel _channel = BasicMessageChannel('flutter_push_mixin', StringCodec());

  static const String _init = 'init_channel_push_mix';

  static Logger log = Logger();

  static Stream<PushModel> initListener({
    required Function(RegIdModel model) getId,
    required Function(PushModel model) getMessage,
  }) async* {

    _channel.setMessageHandler((message) async{
      log.d('flutter_push_mixin -> : $message');

      if (message == 'ok') {

        log.d('flutter_push_mixin -> : 初始化完成');

      } else if (message is Map) {
        if (message['regId'] != null) {
          log.d('flutter_push_mixin -> : 成功获取regId');

          RegIdModel _model = RegIdModel.fromJson(message as Map<String, dynamic>);

          getId(_model);
        } else {
          log.d('flutter_push_mixin -> : 获取到消息');

          PushModel _model = PushModel.fromJson(message as Map<String, dynamic>);

          getMessage(_model);
        }
      }
    });

    _channel.send(_init);

  }
}

class RegIdModel {
  final String? platformName;
  final String? regId;

  const RegIdModel({required this.platformName, required this.regId});

  factory RegIdModel.fromJson(Map<String, dynamic> json) => RegIdModel(
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

  const PushModel(
      {required this.description,
      required this.payload,
      required this.title,
      required this.platform,
      required this.passThrough});

  factory PushModel.fromJson(Map<String, dynamic> json) => PushModel(
        payload: json['payload'],
        platform: json['platform'],
        title: json['title'],
        description: json['description'],
        passThrough: json['passThrough'],
      );
}
