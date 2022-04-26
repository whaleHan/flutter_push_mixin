import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_push_mixin/flutter_push_mixin.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  final String _platformVersion = 'Unknown';

  @override
  void initState() {
    super.initState();
    initPushUtil();
  }

  Future initPushUtil() async {
    print('开始获取ApnsToken');
    FlutterPushMixin.initListener(
        getId: (RegIdModel model) {},
        getMessage: (PushModel model) {},
        getApnsToken: (String token) {
          print('获取到的ApnsToken: $token');
        });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
            child: CupertinoButton(
          color: Colors.red,
          onPressed: () {
            initPushUtil();
          },
          child: Text('调用'),
        )),
      ),
    );
  }
}
