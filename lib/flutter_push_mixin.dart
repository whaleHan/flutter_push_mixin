import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:logger/logger.dart';

class FlutterPushMixin {
  static const EventChannel _channel = EventChannel('flutter_push_mixin');

  static const String _init = 'init_channel_push_mix';

  static Logger log = Logger();

  static Stream<PushModel> initListener({
    required Function(RegIdModel model) getId,
    required Function(PushModel model) getMessage,
  }) async* {
    final Stream _stream = _channel.receiveBroadcastStream(_init);

    _stream.listen((event) {
      log.d('flutter_push_mixin -> : $event');

      if (event == 'ok') {

        log.d('flutter_push_mixin -> : 初始化完成');

      } else if (event is Map) {
        if (event['regId'] != null) {
          log.d('flutter_push_mixin -> : 成功获取regId');

          RegIdModel _model = RegIdModel.fromJson(event as Map<String, dynamic>);

          getId(_model);
        } else {
          log.d('flutter_push_mixin -> : 成功获取regId');

          PushModel _model = PushModel.fromJson(event as Map<String, dynamic>);

          getMessage(_model);
        }
      }
    });
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
