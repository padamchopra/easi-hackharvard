import 'package:easi/widgets/centre_card.dart';
import 'package:flutter/material.dart';
import './widgets/wallpaper.dart';
import './widgets/text.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      theme: ThemeData(
        fontFamily: "productsans",
        primaryColor: Colors.black,
        accentColor: Colors.black,
        highlightColor: Colors.black,
        cursorColor: Colors.black,
      ),
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
            ListView(
              scrollDirection: Axis.vertical,
              children: <Widget>[
                Container(
                  margin: EdgeInsets.only(left: 25.0),
                  child: Heading(
                    "EASI",
                    Colors.white,
                    fontSize: 48.0,
                    textAlign: TextAlign.start,
                  ),
                ),
                SizedBox(height: 10.0,),
                CentreCard()
              ],
            ),
          ],
        ),
        bottomNavigationBar: BottomAppBar(
          child: Container(
            margin: EdgeInsets.only(
              left: 40.0,
              right: 40.0,
              top: 4.0,
              bottom: 4.0,
            ),
            child: Row(
              mainAxisSize: MainAxisSize.max,
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: <Widget>[
                IconButton(
                  onPressed: () {},
                  icon: Icon(
                    Icons.list,
                    size: 32.0,
                    color: Color(0xff4285f4),
                  ),
                ),
                IconButton(
                  onPressed: () {},
                  icon: Icon(
                    Icons.translate,
                    size: 28.0,
                    color: Color(0xff4285f4),
                  ),
                )
              ],
            ),
          ),
          elevation: 10.0,
          color: Colors.black,
          shape: CircularNotchedRectangle(),
        ),
        floatingActionButtonLocation: FloatingActionButtonLocation.centerDocked,
        floatingActionButton: FloatingActionButton(
          elevation: 5.0,
          onPressed: () {},
          child: Container(
            margin: EdgeInsets.all(15.0),
            child: Icon(Icons.add),
          ),
          backgroundColor: Color(0xff4285f4),
        ),
      ),
    );
  }
}
