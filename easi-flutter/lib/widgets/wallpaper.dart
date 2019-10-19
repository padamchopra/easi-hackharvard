import 'package:flutter/material.dart';

class Wallpaper extends StatelessWidget{
  @override
  Widget build(BuildContext context) {
    return ListView(
      scrollDirection: Axis.vertical,
      children: <Widget>[
        Container(
          width: MediaQuery.of(context).size.width,
          height: MediaQuery.of(context).size.height/2,
          color: Color(0xff4285f4),
        ),
        Container(
          width: MediaQuery.of(context).size.width,
          height: MediaQuery.of(context).size.height/2,
          color: Colors.white,
        )
      ],
    );
  }
}