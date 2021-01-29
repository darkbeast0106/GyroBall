package com.darkbeast0106.gyroball;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private BallView ballView;
    private Timer timer;

    private int screenWidth;
    private int screenHeight;
    private PointF ballPosition;
    private PointF ballSpeed;
    private FrameLayout mainFrame;

    //Nem kötelező - az onTouchListenert felülírná a performclick és ezt orvosolja
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //actionbar eltűntetése
        getSupportActionBar().hide();
        //Képernyő flagek beállítása:
        // - Teljes képernyős alkalmazás
        // - Nem kapcsol ki automatikusan a kijelző
        getWindow().setFlags(0xFFFFFFFF,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        SensorManager sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        //Regisztrálunk egy eseménykezelőt arra, hogy ha változást érzékel a telefon gyroscopeja
        sensorManager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                //Döntötség alapján állítjuk be a sebességet.
                ballSpeed.x = -event.values[0];
                ballSpeed.y = event.values[1];
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        },sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0), SensorManager.SENSOR_DELAY_NORMAL);

        //Érintésre a labdát az érintés helyére rakjuk
        mainFrame.setOnTouchListener((View v, MotionEvent event) -> {
            ballPosition.x = event.getX();
            ballPosition.y = event.getY();
            return true;
        });
    }

    private void init() {
        mainFrame = findViewById(R.id.mainFrame);

        //Képernyő szélesség és magasság lekérdezése és lementése, hogy
        // ha túlmegy a golyó akkor vissza tudjuk rakni a másik oldalra
        Display display = getWindowManager().getDefaultDisplay();
        //deprecated api 15-el
        //screenHeight = display.getHeight();
        //screenWidth = display.getWidth();

        //deprecated - api 30-al (még ezt használjuk)
        Point size = new Point();
        display.getSize(size);

        screenWidth = size.x;
        screenHeight = size.y;

        ballPosition = new PointF();
        ballSpeed = new PointF();

        //Golyó kezdő helyzete a képernyő közepén.
        ballPosition.x = screenWidth / 2f;
        ballPosition.y = screenHeight / 2f;

        ballSpeed.x = 0;
        ballSpeed.y = 0;

        //A színt hexadecimális számmal is meg lehet adni / Color.argb segítségével.
        ballView = new BallView(this, ballPosition.x, ballPosition.y, 25, Color.GREEN);
        mainFrame.addView(ballView);
        //Labda kirajzolása
        ballView.invalidate();
    }

    //onResume akkor hívódik meg amikor az activity előtérbe kerül
    //Amikor először jön létre az activity akkor is lefut.
    @Override
    protected void onResume() {
        //A leálított timert nem lehet javaban újra elindítani hanem új timert kell létrehozni
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                ballPosition.x += ballSpeed.x;
                ballPosition.y += ballSpeed.y;

                //Ha kimegyünk a kejelzőről akkor a kijelző túloldalára rakjuk a golyót.
                if (ballPosition.x > screenWidth){
                    ballPosition.x = 0;
                } else if(ballPosition.x < 0){
                    ballPosition.x = screenWidth;
                }
                if (ballPosition.y > screenHeight){
                    ballPosition.y = 0;
                } else if(ballPosition.y < 0){
                    ballPosition.y = screenHeight;
                }

                ballView.x = ballPosition.x;
                ballView.y = ballPosition.y;

                //Golyó újrarajzolása.
                ballView.invalidate();
            }
        };
        timer.schedule(task, 10,10);
        super.onResume();
    }

    //onPause akkor hívódik meg amikor az activity nincs előtérben.
    @Override
    protected void onPause() {
        //Amikor háttérbe kerül az alkalmazás akkor leállítjuk a timert.
        timer.cancel();
        super.onPause();
    }
}