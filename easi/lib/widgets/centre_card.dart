import 'package:easi/widgets/text.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'dart:async';

class CentreCard extends StatefulWidget {
  @override
  _CentreCardState createState() => _CentreCardState();
}

class _CentreCardState extends State<CentreCard> {
  double marginTop = 10.0;
  bool swipedDown = false;
  static const invokeSpeechRecog = const MethodChannel("easi/stt");
  String textToShow = "swipe down to listen";

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onVerticalDragDown: (_) async {
        setState(() {
          marginTop = 30.0;
          swipedDown = true;
          textToShow = "Listening..";
          Future.delayed(Duration(milliseconds: 120), () {
            setState(() {
              marginTop = 0.0;
            });
          });
          print("about to invoke android code");
          invokeSpeechRecog.invokeMethod("getRecognisedText")
          .then((result) {
            print("got result in flutter");
            setState(() {
              textToShow = result;
            });
          });
        });
      },
      child: AnimatedContainer(
        duration: Duration(
          milliseconds: 120,
        ),
        height: MediaQuery.of(context).size.height * 0.70,
        width: MediaQuery.of(context).size.width,
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(20.0),
          color: Color(0xfff5f5f5),
          boxShadow: [
            BoxShadow(
              blurRadius: 3.0,
              color: Colors.black38,
              offset: Offset(
                0.0,
                0.0,
              ),
            )
          ],
        ),
        margin: EdgeInsets.fromLTRB(25.0, marginTop, 25.0, 80.0),
        child: Center(
          child: Subheading(
            textToShow,
            Colors.black,
            fontSize: 18.0,
          ),
        ),
      ),
    );
  }
}
