package com.chopin;

import android.app.Activity;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import java.util.ArrayList;
import java.lang.Thread;
import android.net.Uri;
import android.content.ContentResolver;
import android.database.Cursor;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.MediaController.MediaPlayerControl;
import android.os.IBinder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import com.chopin.MusicService.MusicBinder;

/**@mainpage (Chop)in
 * @author Branden Romero, Mason McNutt, Slaton Spangler, Cameron Tierney
 * @section intro Introduction
 * (Chop)in is a music discovery app that enables user's to discover new music
 * quickly.
 */

public class MainActivity extends Activity implements GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener, MediaPlayerControl{
    private ArrayList<Song> songList;
    private ListView songView;
    private MusicController controller;
    private MusicService musicSrv = null;
    private Intent playIntent;
    private boolean musicBound = false;
    private boolean paused=false, playbackPaused=false;
    //private SimpleGestureFilter detector;
    private static final String DEBUG_TAG = "Gestures";
    private GestureDetectorCompat mDetector;

    //private SimpleGestureFilter detector;  //SC01: uncomment to test

    /**
     * Start-up procedure. Sets song list as start page and populates the song list.
     * @param savedInstanceState Android default
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDetector = new GestureDetectorCompat(this,this);
        mDetector.setOnDoubleTapListener(this);
        songView = (ListView)findViewById(R.id.song_list);
        songList = new ArrayList<Song>();
        getSongList();
        SongAdapter songAdt = new SongAdapter(this, songList);
        songView.setAdapter(songAdt);
        setController();
        //detector = new SimpleGestureFilter(this,this); //SC01: uncomment to test

}

    /**
     * Catches all gestures.
     * @param event Filtered gesture movement
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event){
        this.mDetector.onTouchEvent(event);
        // Be sure to call the superclass implementation
        return super.onTouchEvent(event);
    }

    /**
     * After a gesture is caught and the motion is picked up as a downward motion it does nothing.
     * @param event Filtered gesture movement
     * @return
     */
    @Override
    public boolean onDown(MotionEvent event) {
        Log.d(DEBUG_TAG,"onDown: " + event.toString());
        return true;
    }

    /**
     * If the onTouchEvent catches a horizontal gesture, it plays next song.
     * @param event1 Gesture event
     * @param event2 Gesture event
     * @param velocityX Length of the swipe in the X-direction
     * @param velocityY distanceY Length of the swipe in the Y-direction
     * @return
     */
    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY) {
        Log.d(DEBUG_TAG, "onFling: " + event1.toString()+event2.toString());
        musicSrv.playNext();

        try {
            Thread.sleep(1000);
        }catch (InterruptedException e){
            System.out.println("got interuppted!");
        }
        int dur = musicSrv.getDur();
        seekTo(dur - (30*1000));
        return true;
    }

    /**
     * If the onTouchEvent catches a long press, then it will pause the music.
     * @param event Filtered gesture movement
     */
    @Override
    public void onLongPress(MotionEvent event) {
        Log.d(DEBUG_TAG, "onLongPress: " + event.toString());
        if(!playbackPaused)
            pause();
        else {
            musicSrv.go();
            playbackPaused = false;
        }
    }

    /**
     * Will be implemented for menus.
     * @param e1 Gesture event
     * @param e2 Gesture event
     * @param distanceX Length of the swipe in the X-direction
     * @param distanceY Length of the swipe in the Y-direction
     * @return
     */
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        Log.d(DEBUG_TAG, "onScroll: " + e1.toString()+e2.toString());
        System.out.print("hello");
        return true;
    }

    /**
     *
     * @param event Filtered gesture movement
     */
    @Override
    public void onShowPress(MotionEvent event) {
        Log.d(DEBUG_TAG, "onShowPress: " + event.toString());
    }

    /**
     *
     * @param event Filtered gesture movement
     * @return
     */
    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        Log.d(DEBUG_TAG, "onSingleTapUp: " + event.toString());
        int dur = getDuration();
        seekTo(dur - (30*1000));
        return true;
    }

    /**
     *
     * @param event Filtered gesture movement
     * @return
     */
    @Override
    public boolean onDoubleTap(MotionEvent event) {
        Log.d(DEBUG_TAG, "onDoubleTap: " + event.toString());
        musicSrv.seek(0);
        return true;
    }

    /**
     *
     * @param event Filtered gesture movement
     * @return
     */
    @Override
    public boolean onDoubleTapEvent(MotionEvent event) {
        Log.d(DEBUG_TAG, "onDoubleTapEvent: " + event.toString());
        return true;
    }

    /**
     *
     * @param event Filtered gesture movement
     * @return
     */
    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        Log.d(DEBUG_TAG, "onSingleTapConfirmed: " + event.toString());

        return true;
    }

    private ServiceConnection musicConnection = new ServiceConnection() {
        /**
         *
         * @param name Music service
         * @param service
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicBinder binder = (MusicBinder)service;
            musicSrv = binder.getService();
            musicSrv.setList(songList);
            musicBound = true;
        }

        /**
         *
         * @param name Disconnects Binder at the end
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {

            musicBound = false;
        }
    };

    /**
     *Sets up the music player
     */
    @Override
    protected void onStart(){
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    /**
     * Default Android method for menus
     * @param menu The menu created by android
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    /**
     * Performs operation on menu item select
     * @param item Items in the menu to be selected
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case R.id.action_end:
            stopService(playIntent);
            musicSrv=null;
            System.exit(0);
        break;
        }
        return super.onOptionsItemSelected(item);


    }

    /**
     * Used by oncreate to populate the song list using the SongAdapter and Song class
     */
    public void getSongList(){
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri,null, null,null,null);
        if(musicCursor!=null && musicCursor.moveToFirst()){
            int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                songList.add(new Song(thisId, thisTitle, thisArtist));
            }
            while (musicCursor.moveToNext());
            }

    }

    @Override
    /**
     * Plays the song at which the music cursor is placed.
     */
    public void start() {
        musicSrv.go();
    }

    @Override

    public void pause() {
        musicSrv.pausePlayer();
        playbackPaused=true;
    }

    @Override
    public int getDuration() {
        if(musicSrv!=null && musicBound && musicSrv.isPng())
            return musicSrv.getDur();
        else return 0;
    }

    @Override
    public int getCurrentPosition() {
        if(musicSrv != null && musicBound)
            return musicSrv.getPosn();
        else return 0;
    }

    @Override
    public void seekTo(int pos) {
        musicSrv.seek(pos);
    }

    @Override
    public boolean isPlaying() {
        if (musicSrv!=null && musicBound)
            return musicSrv.isPng();
        return false;
    }

    @Override
    public int getBufferPercentage() {

        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    /**
     * Enables the built in android music controller, and sets up a listener to listen for clicks
     */
    private  void  setController(){
        controller = new MusicController(this);
        controller.setPrevNextListeners(
            new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
                }
            },
            new View.OnClickListener(){
                public void onClick(View v){

                }
            }
        );
        controller.setMediaPlayer(this);
        //controller.setAnchorView(findViewById(R.id.song_list));
        //controller.setEnabled(true);
    }

    /**
     *
     * @param view Song list view.
     */
    public void songPicked(View view){
        musicSrv.setSong(Integer.parseInt(view.getTag().toString()));
        musicSrv.playSong();
        if(playbackPaused){
            setController();
            playbackPaused=false;
        }
        controller.show(0);
        try {
            Thread.sleep(1000);
        }catch (InterruptedException e){
            System.out.println("got interuppted!");
        }
        int dur = musicSrv.getDur();
        seekTo(dur - (30*1000));

    }

    @Override
    protected  void onDestroy(){
        stopService(playIntent);
        musicSrv=null;
        super.onDestroy();
    }

    private void playNext(){
        musicSrv.playNext();
        if(playbackPaused){
            setController();
            playbackPaused=false;
        }

        controller.show(0);

    }
    private void playPrev(){
        musicSrv.playPrev();
        if(playbackPaused){
            setController();
            playbackPaused=false;
        }
        controller.show(0);
    }
    @Override
    protected void onPause(){
        super.onPause();
        paused=true;
    }
    @Override
    protected void onResume(){
        super.onResume();
        if(paused){
            setController();
            paused=false;
        }
    }
    @Override
    protected void onStop() {
        controller.hide();
        super.onStop();
    }

   /* @Override
    public boolean dispatchTouchEvent(MotionEvent me){
        // Call onTouchEvent of SimpleGestureFilter class
        this.detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }
*/

   //@Override
    /*public void onSwipe(int direction) {

        switch (direction) {
            case SimpleGestureFilter.SWIPE_RIGHT : musicSrv.playNext();
                break;
            case SimpleGestureFilter.SWIPE_LEFT :  musicSrv.playNext();
                break;
            //case SimpleGestureFilter.SWIPE_DOWN :  str = "Swipe Down"; //Do we want these?
            //    break;
            //case SimpleGestureFilter.SWIPE_UP :    str = "Swipe Up";
            //    break;
        }
    }*/

/*
    //@Override
    public void onDoubleTap() {
         //do nothing right now...
    }
*/

}
