package com.watermass.easi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Translate extends AppCompatActivity implements LanguagesAdapter.ItemClickListener{

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    ArrayList<String> languageNames = new ArrayList<>();
    ArrayList<String> languageCodes = new ArrayList<>();
    String languages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mAdapter = new LanguagesAdapter(this,languageNames);
        ((LanguagesAdapter) mAdapter).setClickListener(this);

        OkHttpClient client = new OkHttpClient();
        try {
            String url = "https://translation.googleapis.com/language/translate/v2/languages?target=en&key=AIzaSyAcwLomuxB9rJMrL-avlDsdLSUveZgZgfw";
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if(response.isSuccessful()) {
                        try{
                            languages = new JSONObject(new JSONObject(response.body().string()).get("data").toString()).get("languages").toString();
                            final String[] result = languages.split("\"language\":\"");
                            Translate.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    for(int i=1;i<result.length;i++){
                                        languageCodes.add(result[i].split("\"")[0]);
                                        languageNames.add(result[i].split("\"name\":\"")[1].split("\"")[0]);
                                        mAdapter.notifyDataSetChanged();
                                    }
                                }
                            });
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            });
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error occured. Try Again.", Toast.LENGTH_SHORT).show();
        }

        recyclerView = findViewById(R.id.recycler_view_languages);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onItemClick(View view, int position) {
        FirebaseUser user = mAuth.getCurrentUser();
        Map<String, Object> lang = new HashMap<>();
        lang.put("language",languageCodes.get(position));
        db.collection(user.getUid()).document("settings").set(lang)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Language preference changed.", Toast.LENGTH_SHORT).show();
                    }
                });
        startActivity(new Intent(Translate.this, MainActivity.class));
        finish();
    }
}
