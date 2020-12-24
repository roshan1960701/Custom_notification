import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:firebase_messaging/firebase_messaging.dart';

void main() {
  runApp(MaterialApp(
    home: MyHome(),
  ));
}

class MyHome extends StatefulWidget {
  @override
  _MyHomeState createState() => _MyHomeState();
}

class _MyHomeState extends State<MyHome> {
  final FirebaseMessaging firebaseMessaging = FirebaseMessaging();
  String token = " ";
  static const mNotificationBar =
      const MethodChannel('notification_bar.flutter.io/notificationBar');
  TextEditingController mControllera = TextEditingController();
  TextEditingController mControllerb = TextEditingController();

  @override
  void initState() {
    getToken();
    super.initState();
  }

  Future getToken() async {
    /*if (Platform.isIOS) {
      firebaseMessaging.requestNotificationPermissions(
          IosNotificationSettings(sound: true, badge: true, alert: true));
      firebaseMessaging.onIosSettingsRegistered
          .listen((IosNotificationSettings settings) {
        print("Settings registered: $settings");
      });
    }*/
    firebaseMessaging.getToken().then((value) {
      token = value.toString();
      print("Token: $token");
    }).catchError((onError) {
      print("Exception: $onError");
    });

    firebaseMessaging.configure(
      onMessage: (Map<String, dynamic> message) async {
        var title = message['notification']['title'];
        var body = message['notification']['body'];
        var dataTitle = message['data']['title'];
        var dataContent = message['data']['content'];
        Map<String, String> map = {
          "contentTitle": title,
          "contentText": body,
          "dataTitle": dataTitle,
          "dataContent": dataContent,
        };
        await mNotificationBar.invokeMethod('content', map);
        print("Message: $message");
      },
      onBackgroundMessage: myBackgroundMessageHandler,
      /*(Map<String, dynamic> message) async {
        var title = message['notification']['title'];
        var body = message['notification']['body'];
        var dataTitle = message['data']['title'];
        var dataContent = message['data']['content'];

        Map<String, String> map = {
          "contentTitle": title,
          "contentText": body,
          "dataTitle": dataTitle,
          "dataContent": dataContent,
        };
        await mNotificationBar.invokeMethod('content', map);
        print("onBackgroundMessage: $message");
      },*/
      onResume: (Map<String, dynamic> message) async {
        var title = message['notification']['title'];
        var body = message['notification']['body'];
        var dataTitle = message['data']['title'];
        var dataContent = message['data']['content'];

        Map<String, String> map = {
          "contentTitle": title,
          "contentText": body,
          "dataTitle": dataTitle,
          "dataContent": dataContent,
        };
        await mNotificationBar.invokeMethod('content', map);

        print("Resume: $message");
      },
      onLaunch: (Map<String, dynamic> message) async {
        var title = message['notification']['title'];
        var body = message['notification']['body'];
        var dataTitle = message['data']['title'];
        var dataContent = message['data']['content'];

        Map<String, String> map = {
          "contentTitle": title,
          "contentText": body,
          "dataTitle": dataTitle,
          "dataContent": dataContent,
        };
        await mNotificationBar.invokeMethod('content', map);

        print("Launch: $message");
      },
    );
  }

  Future<dynamic> myBackgroundMessageHandler(
      Map<String, dynamic> message) async {
    if (message.containsKey('data')) {
      var dataTitle = message['data']['title'];
      var dataContent = message['data']['content'];

      Map<String, String> map = {
        "dataTitle": dataTitle,
        "dataContent": dataContent,
      };
      await mNotificationBar.invokeMethod('content', map);
      // Handle data message
      final dynamic data = message['data'];
    }

    if (message.containsKey('notification')) {
      var title = message['notification']['title'];
      var body = message['notification']['body'];

      Map<String, String> map = {
        "contentTitle": title,
        "contentText": body,
      };
      await mNotificationBar.invokeMethod('content', map);
      // Handle notification message
      final dynamic notification = message['notification'];
    }

    // Or do other work.
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          title: Text('Notification'),
        ),
        body: Padding(
          padding: EdgeInsets.all(10),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.center,
            children: <Widget>[
              Text('flutter_demo_notification_bar'),
              SizedBox(
                height: 50,
              ),
              TextField(
                controller: mControllera,
                decoration: new InputDecoration(
                  labelText: 'ContentTitle',
                ),
                autofocus: false,
              ),
              TextField(
                controller: mControllerb,
                decoration: new InputDecoration(
                  labelText: 'ContentText',
                ),
                autofocus: false,
              ),
              SizedBox(
                height: 50,
              ),
              RaisedButton(
                child: Text('Demo'),
                onPressed: () async {
                  String stra = mControllera.text.trim();
                  String strb = mControllerb.text.trim();
                  if (stra.isEmpty) {
                    stra = 'Empty';
                  }
                  if (strb.isEmpty) {
                    strb = 'Empty！';
                  }
                  Map<String, String> map = {
                    "contentTitle": stra,
                    "contentText": strb
                  };
                  String result =
                      await mNotificationBar.invokeMethod('content', map);
                  mControllera.text = '';
                  mControllerb.text = '';
                  print(result);
                },
              )
            ],
          ),
        ));
  }
}
