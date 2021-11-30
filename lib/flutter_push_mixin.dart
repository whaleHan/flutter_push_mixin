import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class FlutterPushMixin {
  static const MethodChannel _methodChannel = MethodChannel('ss');

  static const EventChannel _channel = EventChannel('flutter_push_mixin');

  static const String _init = 'init_channel_push_mix';

  static Stream<PushModel> initListener() async* {

    final Stream _stream = _channel.receiveBroadcastStream(_init);



    _stream.listen((event) {
      print('监听回报数据: $event');

      if(event == 'ok') {
        //TODO: 初始化完成
      } else if(event is Map) {
        if(event['regId'] != null) {
          //成功获取数据
          print('获取regId成功');
        } else {
          PushModel _model = PushModel.fromJson(event as Map<String, dynamic>);
        }
      }
    });
  }
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
