package com.example.ginz.musicbasic;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private AudioService mMusicSrv;
    private Intent mPlayIntent;
    private boolean mMusicBound = false;
    private List<Song> mSongList;
    private MediaPlayer mMediaPlayer;
    private Handler mThreadHandler;

    private Button mButtonPlayPause;
    private Button mButtonPrevious;
    private Button mButtonNext;
    private ImageView mImageSongCover;
    private TextView mTextTimeLeft;
    private SeekBar mSeekBar;
    private boolean mIsSongPlaying;
    private boolean mShouldUnbind;
    private MediaMetadataRetriever mMediaMetadataRetriever;
    private static final int DEFAULT_SONG_POSITION = 0;
    private static final int DELAY_MILIS = 50;
    private static final int SUBSTRACT_TIME = 5000;
    private static final int NUMB_ZERO = 0;
    private static final int IC_STOP_BLACK_24 = R.drawable.ic_stop_black_24dp;
    private static final int IC_PLAY_BLACK_24 = R.drawable.ic_play_arrow_black_24dp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, AudioService.class);

        bindService(intent, mConnection, BIND_AUTO_CREATE);

        mThreadHandler = new Handler();
        mIsSongPlaying = false;
        mButtonNext = findViewById(R.id.button_next);
        mButtonPrevious = findViewById(R.id.button_previous);
        mButtonPlayPause = findViewById(R.id.button_play_pause);
        mTextTimeLeft = findViewById(R.id.text_time_left);
        mSeekBar = findViewById(R.id.seek_bar_duration);
        mImageSongCover = findViewById(R.id.image_song_cover);

        mButtonPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mIsSongPlaying){
                    mButtonPlayPause.setBackgroundResource(IC_STOP_BLACK_24);
                    mMusicSrv.pauseSong();
                    mIsSongPlaying = false;
                }
                else{
                    mButtonPlayPause.setBackgroundResource(IC_PLAY_BLACK_24);
                    doStart();
                    mIsSongPlaying = true;
                }
            }
        });

        mButtonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doRewind();
            }
        });

        mButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doFastForward();
            }
        });

    }

    public void doStart()  {
        int duration = mMusicSrv.getDuration();
        int currentPosition = mMusicSrv.getCurrentPosition();
        if(currentPosition== DEFAULT_SONG_POSITION)  {
            this.mSeekBar.setMax(duration);
            String maxTimeString = this.millisecondsToString(duration);

        } else if(currentPosition== duration)  {
            mMusicSrv.resetSong();
        }
        mMusicSrv.playSong();

        UpdateSeekBarThread updateSeekBarThread= new UpdateSeekBarThread();
        mThreadHandler.postDelayed(updateSeekBarThread,DELAY_MILIS);

    }

    private String millisecondsToString(int milliseconds)  {
        long minutes = TimeUnit.MILLISECONDS.toMinutes((long) milliseconds);
        long seconds =  TimeUnit.MILLISECONDS.toSeconds((long) milliseconds) ;
        return minutes+":"+ seconds;
    }

    class UpdateSeekBarThread implements Runnable {

        public void run()  {
            int currentPosition = mMusicSrv.getCurrentPosition();
            String currentPositionStr = millisecondsToString(currentPosition);
            mTextTimeLeft.setText(currentPositionStr);
            mSeekBar.setProgress(currentPosition);
            mThreadHandler.postDelayed(this, DELAY_MILIS);
        }
    }

    public void doRewind()  {
        int currentPosition = mMusicSrv.getCurrentPosition();
        int duration = mMusicSrv.getDuration();

        int SUBTRACT_TIME = SUBSTRACT_TIME;

        if(currentPosition - SUBTRACT_TIME > NUMB_ZERO )  {
            mMusicSrv.seekTo(currentPosition - SUBTRACT_TIME);
        }
    }

    public void doFastForward()  {
        int currentPosition = mMusicSrv.getCurrentPosition();
        int duration = mMusicSrv.getDuration();
        int ADD_TIME = SUBSTRACT_TIME;

        if(currentPosition + ADD_TIME < duration)  {
            mMusicSrv.seekTo(currentPosition + ADD_TIME);
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mMusicSrv = ((AudioService.AudioBinder) iBinder).getService();
            mMusicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

}
