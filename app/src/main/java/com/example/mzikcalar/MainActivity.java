package com.example.mzikcalar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView no_music;
    Button filterButton;
    EditText search_name;
    ArrayList<AudioModel> songsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerview);
        no_music = findViewById(R.id.no_music);

        search_name = findViewById(R.id.search_name);

        filterButton = findViewById(R.id.searching_button);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Context context = this;

        Button filterButton = findViewById(R.id.searching_button);

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filterButton.getBackground().getConstantState() != null &&
                        filterButton.getBackground().getConstantState().equals(
                                ContextCompat.getDrawable(context, R.drawable.baseline_search_24).getConstantState())) {
                    search_name.setVisibility(View.VISIBLE);
                    filterButton.setBackgroundResource(R.drawable.baseline_saved_search_24);

                    search_name.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            String searchText = editable.toString().toLowerCase();
                            ArrayList<AudioModel> filteredList = new ArrayList<>();
                            for (AudioModel song : songsList) {
                                if (song.getTitle().toLowerCase().contains(searchText)) {
                                    filteredList.add(song);
                                }
                            }
                            recyclerView.setAdapter(new MusicListAdapter(filteredList, getApplicationContext()));
                        }
                    });
                } else {
                    search_name.setVisibility(View.GONE);
                    filterButton.setBackgroundResource(R.drawable.baseline_search_24);
                }
            }
        });



        if (checkPermission() == false) {
            requestPermission();
            return;
        }

        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION
        };

        String selection = MediaStore.Audio.Media.IS_MUSIC + " !=0";

        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null, null);
        while (cursor.moveToNext()) {
            AudioModel songsData = new AudioModel(cursor.getString(1), cursor.getString(0), cursor.getString(2));

            if (new File(songsData.getPath()).exists())
                songsList.add(songsData);
        }

        if (songsList.size() == 0) {
            no_music.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new MusicListAdapter(songsList, getApplicationContext()));
        }

    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(MainActivity.this, "Kardeşim İzin Ver Artık (-.-)", Toast.LENGTH_SHORT);
        } else
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // İzin verildi, depolama erişimi sağlandı
                // Burada depolama erişimi olan kodu çalıştırabilirsiniz.
            } else {
                // İzin reddedildi, kullanıcıya uygun bir geri bildirimde bulunabilirsiniz.
            }
        }
    }
    public void onResume() {
        super.onResume();
        if (recyclerView != null) {
            recyclerView.setAdapter(new MusicListAdapter(songsList, getApplicationContext()));
        }
    }

}

