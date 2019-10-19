package com.watermass.easi;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GestureDetectorCompat;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    private View contextView;
    public GestureDetectorCompat gestureDetectorCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        contextView = findViewById(R.id.root_view);
        this.gestureDetectorCompat = new GestureDetectorCompat(this, this);
    }

    //Gesture Detection to listen for swipes
    @Override
    public boolean onTouchEvent(MotionEvent event){
        this.gestureDetectorCompat.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (e2.getY() - e1.getY() > 50) {
            //swipe down
            onSpeechButtonClicked(contextView);
            Toast.makeText(getApplicationContext(), "Speak Now",
                    Toast.LENGTH_SHORT).show();
            return true;
        } else if (e2.getX() - e1.getX() > 50) {
            //swipe right
            System.out.println("right");
            return true;
        } else if (e1.getX() - e2.getX() > 50) {
            //swipe left
            System.out.println("left");
            return true;
        } else {
            return false;
        }
    }

    //Starts intent for listening
    //View->Function(result with highest confidence)
    public void onSpeechButtonClicked(View view) {
        System.out.println("Here");
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        SpeechRecognizer speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        final Snackbar snackbar = Snackbar.make(contextView, "Listening...", Snackbar.LENGTH_INDEFINITE);
        RecognitionListener listener = new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
                snackbar.show();
            }

            @Override
            public void onBeginningOfSpeech() {}

            @Override
            public void onRmsChanged(float v) {}

            @Override
            public void onBufferReceived(byte[] bytes) {}

            @Override
            public void onEndOfSpeech() {
                snackbar.dismiss();
            }

            @Override
            public void onError(int i) {}

            @Override
            public void onResults(Bundle bundle) {
                try {
                    ArrayList<String> results = new ArrayList<>(bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION));
                    speechResults(results.get(0));
                } catch (Error e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Could not understand you.", Toast.LENGTH_SHORT).show();
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

    //Do further processing with the speech results
    public void speechResults(String results){
        System.out.println(results);
    }
}
