package com.watermass.easi;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.bottomappbar.BottomAppBar;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    private View contextView;
    public GestureDetectorCompat gestureDetectorCompat;
    FloatingActionButton floatingActionButton;
    BottomNavigationView bottomNavigationView;
    BottomAppBar bottomAppBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String preferredLanguage;
    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    private StorageReference mStorageRef;
    public List<Uri> uriList;
    public String phraseToAdd;
    public VideoView myVideoView;
    public boolean firstVideo = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        uriList = new ArrayList<Uri>();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        myVideoView = findViewById(R.id.video_view);
        db = FirebaseFirestore.getInstance();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        } else {
            sharedPreferences = getSharedPreferences("easi", Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
            db.collection(mAuth.getCurrentUser().getUid()).document("settings").get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Map<String, Object> data = documentSnapshot.getData();
                            preferredLanguage = data.containsKey("language") ? data.get("language").toString() : "en";
                        }
                    });
        }
        //startActivity(new Intent(this, GestureList.class));
        floatingActionButton = findViewById(R.id.fab);
        bottomAppBar = findViewById(R.id.bottom_app_bar);
        setSupportActionBar(bottomAppBar);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 2);


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSign();
            }
        });

        contextView = findViewById(R.id.root_view);
        this.gestureDetectorCompat = new GestureDetectorCompat(this, this);
    }

    //Gesture Detection to listen for swipes
    @Override
    public boolean onTouchEvent(MotionEvent event) {
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
        uriList.clear();
        firstVideo = true;
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        SpeechRecognizer speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        final Snackbar snackbar = Snackbar.make(contextView, "Listening...", Snackbar.LENGTH_INDEFINITE);
        RecognitionListener listener = new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
                snackbar.show();
            }

            @Override
            public void onBeginningOfSpeech() {
            }

            @Override
            public void onRmsChanged(float v) {
            }

            @Override
            public void onBufferReceived(byte[] bytes) {
            }

            @Override
            public void onEndOfSpeech() {
                snackbar.dismiss();
            }

            @Override
            public void onError(int i) {
            }

            @Override
            public void onResults(Bundle bundle) {
                try {
                    ArrayList<String> results = new ArrayList<>(bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION));
                    TextView textView = findViewById(R.id.easi_recognised_text_tv);
                    textView.setText(results.get(0));
                    speechResults(results.get(0));
                } catch (Error e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Could not understand you.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onPartialResults(Bundle bundle) {
            }

            @Override
            public void onEvent(int i, Bundle bundle) {
            }
        };

        speechRecognizer.setRecognitionListener(listener);
        Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        speechRecognizer.startListening(speechIntent);
    }

    //Do further processing with the speech results
    public void speechResults(String results) {
        System.out.println("Recognition result:" + results);
        LinearLayout linearLayout = findViewById(R.id.easi_interpretations_linlayout);
        linearLayout.setVisibility(View.VISIBLE);
        OkHttpClient client = new OkHttpClient();
        loadVideos(results);
        try {
            String url = "https://translation.googleapis.com/language/translate/v2?q=" + URLEncoder.encode(results, "UTF-8") + "&target=" + preferredLanguage + "&key=AIzaSyAcwLomuxB9rJMrL-avlDsdLSUveZgZgfw";
            Request request = new Request.Builder()
                    .url(url).get()
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        try {
                            final String result = response.body().string();
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView textView = findViewById(R.id.easi_recognised_text_tv);
                                    textView.setText(result.split("\"translatedText\": \"")[1].split("\"")[0]);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error occured. Try Again.", Toast.LENGTH_SHORT).show();
            System.out.println("Error coming");
        }
    }

    public void loadVideos(String phrase){
        if(phrase.length()>0) {
            String[] phraseArray = phrase.trim().split(" ");
            if (sharedPreferences.getString(phraseArray[0], "notfound").equals("notfound")) {
                String[] currentWordArr = phraseArray[0].trim().split("");
                for (int k = 0; k < currentWordArr.length; k++) {
                    getDownloadUri(currentWordArr[k].toUpperCase(), phrase);
                }
            } else {
                String toCheck = phraseArray[0];
                for (int j = 1; j < phraseArray.length; j++) {
                    if (!sharedPreferences.getString(toCheck, "notfound").equals("notfound")) {
                        toCheck += " " + phraseArray[j];
                    } else {
                        getDownloadUri(toCheck, phrase);
                    }
                }
            }
        }
    }



    public void getDownloadUri(String name, String phrase) {
        final String name3 = name.concat(".mp4");
        final String name2 = name.toLowerCase();
        final String phrase2 = phrase;
        try {
            final File localFile = File.createTempFile("video", "mp4");
            System.out.println("finding video in storage: " + name3);
            mStorageRef.child(name3).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    uriList.add(uri);
                    String newString = phrase2.replaceFirst(name2.replaceFirst(".mp4",""),"");
                    loadVideos(newString);
                    if(firstVideo){
                        firstVideo = false;
                        myVideoView.setVideoURI(uriList.get(0));
                        myVideoView.start();
                        uriList.remove(0);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        myVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(!uriList.isEmpty()){
                    myVideoView.setVideoURI(uriList.get(0));
                    myVideoView.start();
                    uriList.remove(0);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bar_todo:
                startActivity(new Intent(MainActivity.this, Translate.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void logout(View view) {
        mAuth.signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    public void addSign() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Make sign for?");
        final EditText phrase = new EditText(this);
        phrase.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);
        alertDialogBuilder.setView(phrase);

        alertDialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String[] toAdd = phrase.getText().toString().toLowerCase().split(" ");
                String temp = "";
                for (int j = 0; j < toAdd.length - 1; j++) {
                    temp += " " + toAdd[j];
                    if (sharedPreferences.getString(temp.trim(), "notfound").equals("notfound")) {
                        editor.putString(temp.trim(), "");
                        System.out.println(temp);
                    }else{
                        sharedPreferences.getString(temp,"notfound");
                    }
                }
                editor.apply();
                phraseToAdd = phrase.getText().toString();
                record();
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                finish();
            }
        });
        alertDialogBuilder.show();
    }

    //Record video
    static final int REQUEST_VIDEO_CAPTURE = 1;

    public void record() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri videoUri = intent.getData();

            StorageReference riversRef = mStorageRef.child(phraseToAdd+".mp4");

            riversRef.putFile(videoUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            Toast.makeText(getApplicationContext(),"Sign Added",Toast.LENGTH_SHORT).show();
                            editor.putString(phraseToAdd,"");
                            editor.apply();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                        }
                    });
        }

    }
}
