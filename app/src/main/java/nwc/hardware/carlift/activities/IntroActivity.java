package nwc.hardware.carlift.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import nwc.hardware.carlift.R;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        Handler Logohandler = new Handler(Looper.getMainLooper());
        Logohandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ImageView imageView = findViewById(R.id.introLogo);
                imageView.animate()
                        .setDuration(3000)
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .alpha(1f)
                        .start();
            }
        }, 1200);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), SearchingBluetoothActivity.class);
                startActivity(intent);
                finish();
            }
        }, 6200);
    }
}