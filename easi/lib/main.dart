import 'package:flutter/material.dart';
import './widgets/wallpaper.dart';
import './widgets/text.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          backgroundColor: Color(0xff4285f4),
          elevation: 0.0,
          primary: false,
        ),
        body: Stack(
          fit: StackFit.expand,
          children: <Widget>[
            Wallpaper(),
            Container(
              margin: EdgeInsets.only(left: 25.0),
              child: Heading(
                "easi",
                Colors.white,
                fontSize: 56.0,
                textAlign: TextAlign.start,
              ),
            ),
            Container(
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(20.0),
                color: Colors.white,
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
              margin: EdgeInsets.symmetric(
                horizontal: 25.0,
                vertical: 80.0,
              ),
            )
          ],
        ),
      ),
    );
  }
}
