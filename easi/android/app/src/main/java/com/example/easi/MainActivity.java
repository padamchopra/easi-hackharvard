package com.example.easi;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;

import io.flutter.app.FlutterActivity;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;

public class MainActivity extends FlutterActivity {

    private String recognisedSpeech;
    private MethodChannel.Result resultG;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GeneratedPluginRegistrant.registerWith(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        new MethodChannel(getFlutterView(), "easi/stt").setMethodCallHandler(
                new MethodChannel.MethodCallHandler() {
                    @Override
                    public void onMethodCall(MethodCall call, MethodChannel.Result result) {
                        // Note: this method is invoked on the main thread.
                        if(call.method.equals("getRecognisedText")){
                            resultG = result;
                            System.out.println("starting recognition");
                            startListening();
                        }else{
                            result.notImplemented();
                        }
                    }
                });
    }

    @TargetApi(8)
    private void startListening(){
        System.out.println("reached method");
        SpeechRecognizer speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        RecognitionListener listener = new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {}

            @Override
            public void onBeginningOfSpeech() {System.out.println("speaking");}

            @Override
            public void onRmsChanged(float v) {}

            @Override
            public void onBufferReceived(byte[] bytes) {}

            @Override
            public void onEndOfSpeech() {}

            @Override
            public void onError(int i) {}

            @Override
            public void onResults(Bundle bundle) {
                try {
                    ArrayList<String> results = new ArrayList<>(bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION));
                    recognisedSpeech = results.get(0);
                    resultG.success(recognisedSpeech);
                    System.out.println("Recognition over");
                } catch (Error e) {
                    e.printStackTrace();
                    resultG.success("Error Occured.\nPlease try again.");
                    System.out.println("recognition error");
                }
            }

            @Override
            public void onPartialResults(Bundle bundle) {}

            @Override
            public void onEvent(int i, Bundle bundle) {}
        };

        speechRecognizer.setRecognitionListener(listener);
        Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        speechRecognizer.startListening(speechIntent);
    }
}
