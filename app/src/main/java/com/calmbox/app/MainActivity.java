package com.calmbox.app;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        startService(new Intent(this, CalmService.class));

        Button boton = (Button) findViewById(R.id.testButton);

        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, CalmService.class);
                i.setAction("PLAY_NOW");
                startService(i);
            }
        });
    }
}
