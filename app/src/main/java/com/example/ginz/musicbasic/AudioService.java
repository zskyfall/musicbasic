package com.example.ginz.musicbasic;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class AudioService extends Service {

    private final IBinder iBinder = new AudioBinder();
    private MediaPlayer mMediaPlayer;
    private List<Song> mListSongs;
    private int mSongPosition;
    private NotificationManager mNotificationManager;
    private MediaMetadataRetriever mMediaMetadataRetriever;
    private int mNotification = 23;
    private int[] mListSongIds = {R.raw.alone_alan_walker, R.raw.why_not_me};
    private static final CharSequence TEXT = "SONG IS PLAYING";
    private static final String CONTENT_TTILE = "WHY NOT ME";
    private static final String CONTENT_TEXT = "ALAN WALKER";
    private static final int DEFAULT_SONG_POSITION = 0;
    private static final int PENDING_INTENT_REQUEST_CODE = 0;
    private static final int PENDING_INTENT_FLAG = 0;

    public class AudioBinder extends Binder {
        public AudioService getService() {
            return AudioService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        showNotification(TEXT, CONTENT_TTILE, CONTENT_TEXT);
        mSongPosition = DEFAULT_SONG_POSITION;
        initMusicPlayer();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mMediaPlayer.stop();
        mMediaPlayer.release();
        return false;
    }

    public void initMusicPlayer(){
        mListSongs = new ArrayList<>();
        for (int i : mListSongIds){
            mListSongs.add(new Song(mListSongIds[i]));
        }
        mMediaPlayer = MediaPlayer.create(this, mListSongs.get(mSongPosition).getID());
    }

    public void setList(ArrayList<Song> theSongs){
        mListSongs = theSongs;
    }

    public int getCurrentPosition(){
        return mMediaPlayer.getCurrentPosition();
    }

    public int getDuration(){
        return mMediaPlayer.getDuration();
    }

    public void playSong(){
       mMediaPlayer.start();
    }

    public void pauseSong(){
        mMediaPlayer.pause();
    }

    public void stopSong(){
        mMediaPlayer.stop();
    }

    public void resetSong(){
        mMediaPlayer.reset();
    }

    public void setSong(int songIndex){
        mSongPosition = songIndex;
    }

    public void seekTo(int time){
        mMediaPlayer.seekTo(time);
    }

    private void showNotification(CharSequence text, String contentTitle, String contentText) {

        PendingIntent pendingIntent = PendingIntent.getActivity(this, PENDING_INTENT_REQUEST_CODE, new Intent(this, MainActivity.class), PENDING_INTENT_FLAG);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            Notification notification = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.black_panther)
                    .setTicker(text)
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle(contentTitle)
                    .setContentText(contentText)
                    .setContentIntent(pendingIntent)
                    .build();

            mNotificationManager.notify(mNotification, notification);
        }
    }
}
