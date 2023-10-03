package com.example.mzikcalar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.util.Random;

public class MusicPlayerActivity extends AppCompatActivity {
    TextView titleTv, currentTimeTv, totalTimeTv;
    SeekBar seekBar;
    ImageView pausePlayImg, skipImg, prevImg;
    ArrayList<AudioModel> songsList;
    ArrayList<AudioModel> likedList = new ArrayList<>();
    Button shufle,liked;
    AudioModel currentSong;
    MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();
    boolean onShuffle = false;
    Random rnd = new Random();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        titleTv = findViewById(R.id.song_title);
        currentTimeTv = findViewById(R.id.current_time);
        totalTimeTv = findViewById(R.id.total_time);
        seekBar = findViewById(R.id.seek_bar);
        pausePlayImg = findViewById(R.id.pause_play);
        skipImg = findViewById(R.id.next);
        prevImg = findViewById(R.id.previous);
        liked = findViewById(R.id.liked);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        shufle = findViewById(R.id.shufle);

        Context context = this;


        liked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (liked.getBackground().getConstantState() != null &&
                        liked.getBackground().getConstantState().equals(
                                ContextCompat.getDrawable(context, R.drawable.of_heart).getConstantState()))
                {
                    liked.setBackgroundResource(R.drawable.on_hearth);
                }
                else
                {
                    liked.setBackgroundResource(R.drawable.of_heart);
                }


            }
        });

        shufle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shufle.getBackground().getConstantState() != null &&
                        shufle.getBackground().getConstantState().equals(
                                ContextCompat.getDrawable(context, R.drawable.baseline_shuffle_24).getConstantState())) {
                    onShuffle = true;
                    shufle.setBackgroundResource(R.drawable.baseline_shuffle_on_24);
                } else {
                    onShuffle = false;
                    shufle.setBackgroundResource(R.drawable.baseline_shuffle_24);
                }
            }
        });

        titleTv.setSelected(true);

        songsList = (ArrayList<AudioModel>) getIntent().getSerializableExtra("LIST");

        setResourcesWithMusic();

        MusicPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    currentTimeTv.setText(convertToMMSS(mediaPlayer.getCurrentPosition() + ""));

                    if (mediaPlayer.isPlaying()) {
                        pausePlayImg.setImageResource(R.drawable.baseline_pause_circle_outline_24);
                    } else {
                        pausePlayImg.setImageResource(R.drawable.baseline_play_circle_outline_24);
                    }

                }
                new Handler().postDelayed(this, 100);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                if (liked.getBackground().getConstantState() != null &&
                        liked.getBackground().getConstantState().equals(
                                ContextCompat.getDrawable(context, R.drawable.of_heart).getConstantState()))
                {
                    if (i == seekBar.getMax()) {
                        nextSong(); // Bir sonraki şarkıya geç
                    }
                    if (mediaPlayer != null && b) {
                        mediaPlayer.seekTo(i);
                    }
                }
                else
                {
                    if (mediaPlayer != null && b) {
                        mediaPlayer.seekTo(i);
                    }
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    void setResourcesWithMusic() {
        currentSong = songsList.get(MyMediaPlayer.currentIndex);

        titleTv.setText(currentSong.getTitle());
        totalTimeTv.setText(convertToMMSS(currentSong.getDuration()));

        pausePlayImg.setOnClickListener(v -> pauseSong());
        skipImg.setOnClickListener(v -> nextSong());
        prevImg.setOnClickListener(v -> prevSong());

        playMusic();
    }

    private void playMusic() {

        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(currentSong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setProgress(0);
            seekBar.setMax(mediaPlayer.getDuration());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void nextSong() {
        Context context = this;
        if (onShuffle) {
            int randomToken;
            do {
                randomToken = rnd.nextInt(songsList.size());
            } while (randomToken == MyMediaPlayer.currentIndex); // Aynı şarkıyı tekrar seçme

            MyMediaPlayer.currentIndex = randomToken;
        } else {
            if (MyMediaPlayer.currentIndex == songsList.size() - 1)
                MyMediaPlayer.currentIndex = 0;
            else
                MyMediaPlayer.currentIndex += 1;
        }

        mediaPlayer.reset();
        setResourcesWithMusic();
    }

    public void prevSong() {
        if (onShuffle) {
            int randomToken;
            do {
                randomToken = rnd.nextInt(songsList.size());
            } while (randomToken == MyMediaPlayer.currentIndex); // Aynı şarkıyı tekrar seçme

            MyMediaPlayer.currentIndex = randomToken;
        } else {
            if (MyMediaPlayer.currentIndex == 0)
                MyMediaPlayer.currentIndex = (songsList.size() - 1);
            else
                MyMediaPlayer.currentIndex -= 1;
        }
        mediaPlayer.reset();
        setResourcesWithMusic();}

    public void pauseSong() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.start();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Intent intent = new Intent(this,MainActivity.class);
            intent.putExtra("LISTNEW",likedList);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static String convertToMMSS(String duration) {
        Long millis = Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }

}
