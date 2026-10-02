package com.calmbox.app;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;

import java.util.HashMap;
import java.util.Locale;

public class CalmService extends Service
        implements TextToSpeech.OnInitListener {

    private TextToSpeech voz;
    private boolean vozLista = false;
    private boolean pantallaApagada = false;

    private final String[] pasos = {

        "Bienvenido a CalmBox. Este es un momento para recuperar la calma.",

        "Paso uno. Detente un momento y reconoce lo que estás sintiendo.",

        "Paso dos. Relaja los hombros, las manos y el rostro.",

        "Paso tres. Inhala lentamente por la nariz y exhala despacio.",

        "Paso cuatro. Repite esta respiración tres veces, sin apresurarte.",

        "Paso cinco. Identifica la emoción que estás sintiendo. Puede ser enojo, tristeza, miedo, nervios o frustración.",

        "Paso seis. Recuerda que puedes darte unos segundos antes de reaccionar.",

        "Paso siete. Piensa qué situación provocó esa emoción.",

        "Paso ocho. Busca una manera tranquila y respetuosa de expresar lo que sientes.",

        "Paso nueve. Piensa en una acción sencilla que pueda ayudarte a resolver la situación.",

        "Paso diez. Respira profundamente una última vez. Continúa cuando te sientas más tranquilo.",

        "Has terminado. Recuerda: detenerte, respirar y pensar puede ayudarte a manejar mejor tus emociones."
    };


    private final BroadcastReceiver receptorPantalla =
            new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {

                pantallaApagada = true;

                detenerGuia();

            }

            else if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())
                    && pantallaApagada) {

                pantallaApagada = false;

                reproducirGuia();
            }
        }
    };


    @Override
    public void onCreate() {

        super.onCreate();

        voz = new TextToSpeech(this, this);

        IntentFilter filtro = new IntentFilter();

        filtro.addAction(Intent.ACTION_SCREEN_ON);
        filtro.addAction(Intent.ACTION_SCREEN_OFF);

        registerReceiver(receptorPantalla, filtro);
    }


    @Override
    public int onStartCommand(
            Intent intent,
            int flags,
            int startId) {

        if (intent != null &&
                "PLAY_NOW".equals(intent.getAction())) {

            reproducirGuia();
        }

        return START_STICKY;
    }


    @Override
    public void onInit(int estado) {

        if (estado == TextToSpeech.SUCCESS) {

            voz.setLanguage(new Locale("es", "ES"));

            // Voz un poco más lenta y tranquila
            voz.setSpeechRate(0.82f);

            voz.setPitch(0.95f);

            vozLista = true;
        }
    }


    private void reproducirGuia() {

        if (!vozLista) {
            return;
        }

        voz.stop();

        HashMap<String, String> parametros =
                new HashMap<String, String>();

        parametros.put(
                TextToSpeech.Engine.KEY_PARAM_STREAM,
                String.valueOf(AudioManager.STREAM_MUSIC)
        );


        for (int i = 0; i < pasos.length; i++) {

            voz.speak(
                    pasos[i],
                    i == 0
                            ? TextToSpeech.QUEUE_FLUSH
                            : TextToSpeech.QUEUE_ADD,
                    parametros
            );


            // Pausa entre cada paso
            if (i > 0 && i < pasos.length - 1) {

                long pausa = 1800;

                // Más tiempo para realizar las respiraciones
                if (i == 3 || i == 4) {
                    pausa = 4500;
                }

                voz.playSilence(
                        pausa,
                        TextToSpeech.QUEUE_ADD,
                        null
                );
            }
        }
    }


    private void detenerGuia() {

        if (voz != null) {
            voz.stop();
        }
    }


    @Override
    public void onDestroy() {

        try {
            unregisterReceiver(receptorPantalla);
        }
        catch (Exception e) {
            // No hacemos nada
        }

        if (voz != null) {

            voz.stop();
            voz.shutdown();
        }

        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
}
