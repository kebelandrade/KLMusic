package music.com.klmusic;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import android.net.Uri;
import android.content.ContentResolver;
import android.database.Cursor;
import android.widget.ListView;
import android.os.IBinder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.MenuItem;
import android.view.View;

import music.com.klmusic.MusicService.MusicBinder;

import static music.com.klmusic.R.id.song_list;

import android.widget.MediaController.MediaPlayerControl;

public class MainActivity extends Activity implements MediaPlayerControl{

    public static final String CANAL_PRINCIPAL = "principal";
    private ArrayList<Song> songList;
    private ListView songView;
    private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      //  songView = findViewById(song_list);
        songView = (ListView)findViewById(R.id.song_list);//agregue esto de la primera parte, obteermos la instancia de ListView usando song_list, que es el id que le dimos
        songList = new ArrayList<Song>();
        registrarCanalPrincipal();
        regis();
        Intent intent = new Intent(MainActivity.this, MusicService.class);
        startService(intent);
        getSongList();
        Collections.sort(songList, new Comparator<Song>(){
            public int compare(Song a, Song b){
                return a.getTitle().compareTo(b.getTitle());
            }
        });

        SongAdapter songAdt = new SongAdapter(this, songList);
        songView.setAdapter(songAdt);



        setController();
    }

    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicBinder binder = (MusicBinder)service;
            //get service
            musicSrv = binder.getService();
            //pass list
            musicSrv.setList(songList);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    protected void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    //I guest this part goes in MusicService class

//    private final IBinder musicBind = new MusicBinder();
//
//
//    public IBinder onBind(Intent intent) {
//        return musicBind;
//    }

//    public boolean onUnbind(Intent intent){
//        player.stop();
//        player.release();
//        return false;
//    }

    //I guest this part goes in MusicService class


    public void songPicked(View view){
        musicSrv.setSongs(Integer.parseInt(view.getTag().toString()));
        musicSrv.playSong();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_shuffle:
                break;
            case  R.id.action_end:
                stopService(playIntent);
                musicSrv=null;
                System.exit(0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void registrarCanalPrincipal() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String nombre = "Reproductor de Música";
            String descripcion = "Categoria que controla la reporoduccion de musica";
            int importancia = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CANAL_PRINCIPAL, nombre, importancia);
            channel.setDescription(descripcion);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void regis(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String nombre = "Hola";
            String descripcion = "Categoria que controla la reporoduccion de musica";
            int importancia = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel("Hola", nombre, importancia);
            channel.setDescription(descripcion);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void getSongList() {
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        if(musicCursor!=null && musicCursor.moveToFirst()){
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                songList.add(new Song(thisId, thisTitle, thisArtist));
            }
            while (musicCursor.moveToNext());
        }
    }


    private MusicController controller;

    private void setController(){
        controller = new MusicController(this);

        controller.setMediaPlayer(this);
        controller.setAnchorView(findViewById(R.id.song_list));
        controller.setEnabled(true);
    }


    @Override
    public void start() {

    }

    @Override
    public void pause() {

    }

    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        return 0;
    }

    @Override
    public void seekTo(int pos) {

    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return false;
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

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
