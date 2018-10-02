package com.example.gueye.memoireprevention2018.listeners;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import static android.view.KeyCharacterMap.ALPHA;

public abstract class ShakeListener  implements SensorEventListener {

    SensorManager sensorManager ;
    private Sensor sensorAcelor;
    private float [] gravity  = new float[3];
    private Context context;

    private final String TAG = "SHAKE LISTENER";

    private float accVal = SensorManager.GRAVITY_EARTH;
    private float accLast = SensorManager.GRAVITY_EARTH;
    private float shake =0.00f;
    private int countSecoue = 0;
    private long firstMovTime;
    private long secondMovTime;
    private long SHAKE_WINDOW_TIME_INTERVAL=400;
    private long delayBetweenShake =50;
    private long intervalleBetWeenShake;
    private ShakeListenerInterface listener;

    private int cpt=0;
    private long firstTimeSend =0;
    private long secondTimeSend =0;

    public void init(Context context){

        this.context =context;

        sensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);

        sensorAcelor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        
        register();

    }


    private float calcMaxAcceleration(SensorEvent event) {
        gravity[0] =  calcGravityForce(event.values[0], 0);
        gravity[1] = calcGravityForce(event.values[1], 1);
        gravity[2] = calcGravityForce(event.values[2], 2);

        float accX = event.values[0] - gravity[0];
        float accY = event.values[1] - gravity[1];
        float accZ = event.values[2] - gravity[2];

        float max1 = Math.max(accX, accY);
        return Math.max(max1, accZ);
    }


    private float calcGravityForce(float currentVal, int index) {

        return  ALPHA * gravity[index] + (1 - ALPHA) * currentVal;

    }

    private void register() {

        sensorManager.registerListener(this, sensorAcelor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];
        float z = sensorEvent.values[2];

        accLast = accVal;

        accVal = (float) Math.sqrt( (double) (x*x + y*y + z*z));

        float delta = accVal -accLast;

        shake =shake* 0.9f + delta;

        Log.d("ShakeListener ", "onSensorChanged: shake value "+ shake);

        if(shake > 24){


            if(countSecoue == 0){

                firstMovTime = System.currentTimeMillis();
                Log.d(TAG, "onSensorChanged: firt time moove" + firstMovTime );
                countSecoue++;

            }else{

                countSecoue ++;

                if (countSecoue == 2){

                    secondMovTime = System.currentTimeMillis();

                    Log.d(TAG, "onSensorChanged: second time moove " + secondMovTime);

                    Log.d(TAG, "onSensorChanged: diff time "+ (secondMovTime - firstMovTime));

                    if ((secondMovTime -firstMovTime ) < SHAKE_WINDOW_TIME_INTERVAL){

                        cpt ++;

                        if(cpt == 1){

                            firstTimeSend = System.currentTimeMillis();

                            Log.d(TAG, "onSensorChanged: diff time "+ (secondMovTime - firstMovTime));

                            Toast.makeText(context, "Là on peut envoyer une alerte ", Toast.LENGTH_SHORT).show();

                            onShake();
                        }

                        if(cpt == 2){

                            secondTimeSend = System.currentTimeMillis();

                            long diffTimeSend = secondTimeSend  - firstTimeSend;
                            if(diffTimeSend >= 120000 ){

                                onShake();

                                cpt =0;
                            }else{

                                cpt =1;

                                Toast.makeText(context, "Veuillez patienter quelques minutes avant d'envoyer une autre alerte", Toast.LENGTH_SHORT).show();
                            }
                        }









                            countSecoue =0;

                    }else{

                        countSecoue =0;
                        Toast.makeText(context, "L'intervalle de secoue est petite", Toast.LENGTH_SHORT).show();
                    }

                }else {

                    intervalleBetWeenShake = System.currentTimeMillis();
                    long diff = intervalleBetWeenShake -firstMovTime;
                    if (diff > delayBetweenShake){
                        
                        countSecoue = 0;

                        Toast.makeText(context, "le délai d'attente est dépassé ", Toast.LENGTH_SHORT).show();
                    }
                }


            }

        }



    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public static interface ShakeListenerInterface {
        public void onShake();
    }

    public abstract void onShake();
}
