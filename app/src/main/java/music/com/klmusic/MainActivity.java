package music.com.klmusic;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    public static final String CANAL_PRINCIPAL = "principal";

    Button mBtnIniciar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registrarCanalPrincipal();
        regis();
        mBtnIniciar = findViewById(R.id.activity_main_btn_iniciar);
        mBtnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MusicService.class);
                startService(intent);
            }
        });
    }

    private void registrarCanalPrincipal() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String nombre = getString(R.string.notif_musica);
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
}
