package com.example.easi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.WindowManager

import io.flutter.app.FlutterActivity
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant
import android.widget.Toast
import android.speech.RecognitionListener
import java.lang.Exception
import java.util.*


class MainActivity: FlutterActivity() {

  private val REQUEST_CODE_SPEECH_INPUT = 100
  lateinit var resultG: MethodChannel.Result

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    GeneratedPluginRegistrant.registerWith(this)
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)

    MethodChannel(flutterView,"easi/stt").setMethodCallHandler{call,result ->
      if(call.method == "getRecognisedText"){
        this.resultG = result
        startListening()
      }else{
        result.notImplemented();
      }
    }


//      val speechRecognizer:SpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
//      RecognitionListener listener = new RecognitionListener() {
//        @Override
//        public void onReadyForSpeech(Bundle bundle) {
//          snackbar.show();
//        }
//
//        @Override
//        public void onBeginningOfSpeech() {
//
//        }
//
//        @Override
//        public void onRmsChanged(float v) {
//
//        }
//
//        @Override
//        public void onBufferReceived(byte[] bytes) {
//
//        }
//
//        @Override
//        public void onEndOfSpeech() {
//          snackbar.dismiss();
//        }
//
//        @Override
//        public void onError(int i) {
//
//        }
//
//        @Override
//        public void onResults(Bundle bundle) {
//          try {
//            ArrayList<String> results = new ArrayList<>(bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION));
//            speechResults(results.get(0));
//          } catch (Error e) {
//            e.printStackTrace();
//            Toast.makeText(easi.this, "Could not understand you.", Toast.LENGTH_SHORT).show();
//          }
//        }
//
//        @Override
//        public void onPartialResults(Bundle bundle) {
//
//        }
//
//        @Override
//        public void onEvent(int i, Bundle bundle) {
//
//        }
//      };
//
//      speechRecognizer.setRecognitionListener(listener);
//      Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//      speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//      speechIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
//      speechRecognizer.startListening(speechIntent);
//


  }

  private fun startListening() {

    val mIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
    mIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
    mIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, Locale.getDefault())
    try {
      startActivityForResult(mIntent, REQUEST_CODE_SPEECH_INPUT)
    } catch (e: Exception) {
      print(e)
    }

  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    when(requestCode)
    {
      REQUEST_CODE_SPEECH_INPUT -> {
        if(resultCode == Activity.RESULT_OK && data != null){
          val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
          System.out.println(result)
          this.resultG.success(result[0])
        }
      }
    }
  }
}
