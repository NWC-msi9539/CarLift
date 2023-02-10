package nwc.hardware.carlift.fragments;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import nwc.hardware.Interfaces.OnGattListener;
import nwc.hardware.bletool.BluetoothGeneralTool;
import nwc.hardware.carlift.R;
import nwc.hardware.carlift.activities.HomeActivity;

public class SingleFragment extends Fragment {
    private final String TAG = "SingleFragment";

    private final int release = -1;

    private final int up = 1;
    private final int down = 2;
    private final int stop = 3;

    private final int FL = 0;
    private final int FR = 1;
    private final int RL = 2;
    private final int RR = 3;

    private int status = release;
    private int Target = -1;

    private final byte[] READ_SENSOR1 = { 'R' , 'S' , 'P' , '1', 'E' , 0x0d, 0x0a};
    private final byte[] READ_SENSOR2 = { 'R' , 'S' , 'P' , '2', 'E' , 0x0d, 0x0a};
    private final byte[] READ_SENSOR3 = { 'R' , 'S' , 'P' , '3', 'E' , 0x0d, 0x0a};
    private final byte[] READ_SENSOR4 = { 'R' , 'S' , 'P' , '4', 'E' , 0x0d, 0x0a};

    private final byte[] STOP = { 'L' , 'T' , 'E' , 0x0d, 0x0a};

    private final byte[] FL_UP = { 'S' , 'F' , 'L' , 'U' , 'E' , 0x0d, 0x0a};
    private final byte[] FL_DOWN = { 'S' , 'F' , 'L' , 'D' , 'E' , 0x0d, 0x0a};


    private final byte[] FR_UP = { 'S' , 'F' , 'R' , 'U' , 'E' , 0x0d, 0x0a};
    private final byte[] FR_DOWN = { 'S' , 'F' , 'R' , 'D' , 'E' , 0x0d, 0x0a};

    private final byte[] RL_UP = { 'S' , 'R' , 'L' , 'U' , 'E' , 0x0d, 0x0a};
    private final byte[] RL_DOWN = { 'S' , 'R' , 'L' , 'D' , 'E' , 0x0d, 0x0a};

    private final byte[] RR_UP = { 'S' , 'R' , 'R' , 'U' , 'E' , 0x0d, 0x0a};
    private final byte[] RR_DOWN = { 'S' , 'R' , 'R' , 'D' , 'E' , 0x0d, 0x0a};

    private TextView FL_Sensor;
    private TextView FL_Lock;
    private TextView FR_Sensor;
    private TextView FR_Lock;
    private TextView RL_Sensor;
    private TextView RL_Lock;
    private TextView RR_Sensor;
    private TextView RR_Lock;

    private ImageButton FL_UpBTN;
    private ImageButton FL_DownBTN;
    private ImageButton FR_UpBTN;
    private ImageButton FR_DownBTN;
    private ImageButton RL_UpBTN;
    private ImageButton RL_DownBTN;
    private ImageButton RR_UpBTN;
    private ImageButton RR_DownBTN;

    private Timer timer;
    private TimerTask timerTask;

    private BluetoothGeneralTool bluetoothGeneralTool;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_single, container, false);

        SetTimer();
        SetGattListener();

        FL_Sensor = v.findViewById(R.id.Single_FL_Sensor);
        FL_Lock = v.findViewById(R.id.Single_FL_Lock);

        FR_Sensor = v.findViewById(R.id.Single_FR_Sensor);
        FR_Lock = v.findViewById(R.id.Single_FR_Lock);

        RL_Sensor = v.findViewById(R.id.Single_RL_Sensor);
        RL_Lock = v.findViewById(R.id.Single_RL_Lock);

        RR_Sensor = v.findViewById(R.id.Single_RR_Sensor);
        RR_Lock = v.findViewById(R.id.Single_RR_Lock);

        FL_UpBTN = v.findViewById(R.id.Single_FL_UpBTN);
        FL_UpBTN.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        Log.d(TAG, "FL_UpBTN DOWN!!!");
                        Target = FL;
                        status = up;
                        break;
                    case MotionEvent.ACTION_UP:
                        status = stop;
                        break;
                }

                return false;
            }
        });
        FL_DownBTN = v.findViewById(R.id.Single_FL_DownBTN);
        FL_DownBTN.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        Log.d(TAG, "FL_DownBTN DOWN!!!");
                        Target = FL;
                        status = down;
                        break;
                    case MotionEvent.ACTION_UP:
                        status = stop;
                        break;
                }

                return false;
            }
        });

        FR_UpBTN = v.findViewById(R.id.Single_FR_UpBTN);
        FR_UpBTN.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        Log.d(TAG, "FR_UpBTN DOWN!!!");
                        Target = FR;
                        status = up;
                        break;
                    case MotionEvent.ACTION_UP:
                        status = stop;
                        break;
                }

                return false;
            }
        });
        FR_DownBTN = v.findViewById(R.id.Single_FR_DownBTN);
        FR_DownBTN.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        Log.d(TAG, "FR_DownBTN DOWN!!!");
                        Target = FR;
                        status = down;
                        break;
                    case MotionEvent.ACTION_UP:
                        status = stop;
                        break;
                }

                return false;
            }
        });

        RL_UpBTN = v.findViewById(R.id.Single_RL_UpBTN);
        RL_UpBTN.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        Log.d(TAG, "RL_UpBTN DOWN!!!");
                        Target = RL;
                        status = up;
                        break;
                    case MotionEvent.ACTION_UP:
                        status = stop;
                        break;
                }

                return false;
            }
        });
        RL_DownBTN = v.findViewById(R.id.Single_RL_DownBTN);
        RL_DownBTN.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        Log.d(TAG, "RL_DownBTN DOWN!!!");
                        Target = RL;
                        status = down;
                        break;
                    case MotionEvent.ACTION_UP:
                        status = stop;
                        break;
                }

                return false;
            }
        });

        RR_UpBTN = v.findViewById(R.id.Single_RR_UpBTN);
        RR_UpBTN.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        Log.d(TAG, "RR_UpBTN DOWN!!!");
                        Target = RR;
                        status = up;
                        break;
                    case MotionEvent.ACTION_UP:
                        status = stop;
                        break;
                }

                return false;
            }
        });
        RR_DownBTN = v.findViewById(R.id.Single_RR_DownBTN);
        RR_DownBTN.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        Log.d(TAG, "RR_DownBTN DOWN!!!");
                        Target = RR;
                        status = down;
                        break;
                    case MotionEvent.ACTION_UP:
                        status = stop;
                        break;
                }

                return false;
            }
        });

        timer.schedule(timerTask, 0, 1000);

        return v;
    }

    private void SetGattListener(){
        bluetoothGeneralTool = BluetoothGeneralTool.getInstance(new OnGattListener() {
            @Override
            public void connectionStateConnecting(BluetoothGatt bluetoothGatt) {

            }

            @Override
            public void connectionStateConnected(BluetoothGatt bluetoothGatt) {

            }

            @Override
            public void connectionStateDisconnected(BluetoothGatt bluetoothGatt) {

            }

            @Override
            public void discoveredServices() {

            }

            @Override
            public void characteristicWrite(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i) {
                if(HomeActivity.terminalLayout.getVisibility() == View.VISIBLE) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            byte[] datas = bluetoothGattCharacteristic.getValue();
                            String s = "";
                            for(byte b : datas){
                                s += (char)b;
                            }
                            HomeActivity.terminal_logField.setText(HomeActivity.terminal_logField.getText() +
                                    "\bReceive Data\b --------------------------\n" +
                                    s +
                                    "\n");
                        }
                    });
                }
            }

            @Override
            public void characteristicRead(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i) {

            }

            @Override
            public void characteristicChanged(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic) {
                byte[] datas = bluetoothGattCharacteristic.getValue();
                String s = "";
                for (byte b : datas) {
                    s += (char) b;
                }
                if(datas[0] == 'R'){
                    if (datas[1] == 'S') {
                        switch (datas[2]) {
                            case '1':
                                float fl_value = 0;
                                float fl_lock = 0;

                                for (int i = 3; i < 6; i++) {
                                    float disc = 1;
                                    for (int j = 0; j < (i - 3); j++) {
                                        disc *= 10;
                                    }
                                    disc = 10 / disc;
                                    fl_value += Float.parseFloat(String.valueOf((char) datas[i])) * disc;
                                }

                                fl_lock += Float.parseFloat(String.valueOf((char) datas[6]));

                                FL_Sensor.setText(String.format("%.1f",fl_value));
                                FL_Lock.setText("" + (int)fl_lock);
                                break;
                            case '2':
                                float fr_value = 0;
                                float fr_lock = 0;

                                for (int i = 3; i < 6; i++) {
                                    float disc = 1;
                                    for (int j = 0; j < (i - 3); j++) {
                                        disc *= 10;
                                    }
                                    disc = 10 / disc;
                                    fr_value += Float.parseFloat(String.valueOf((char) datas[i])) * disc;
                                }

                                fr_lock += Float.parseFloat(String.valueOf((char) datas[6]));

                                FR_Sensor.setText(String.format("%.1f",fr_value));
                                FR_Lock.setText("" + (int)fr_lock);
                                break;
                            case '3':
                                float rl_value = 0;
                                float rl_lock = 0;

                                for (int i = 3; i < 6; i++) {
                                    float disc = 1;
                                    for (int j = 0; j < (i - 3); j++) {
                                        disc *= 10;
                                    }
                                    disc = 10 / disc;
                                    rl_value += Float.parseFloat(String.valueOf((char) datas[i])) * disc;
                                }

                                rl_lock += Float.parseFloat(String.valueOf((char) datas[6]));

                                RL_Sensor.setText(String.format("%.1f",rl_value));
                                RL_Lock.setText("" + (int)rl_lock);
                                break;
                            case '4':
                                float rr_value = 0;
                                float rr_lock = 0;

                                for (int i = 3; i < 6; i++) {
                                    float disc = 1;
                                    for (int j = 0; j < (i - 3); j++) {
                                        disc *= 10;
                                    }
                                    disc = 10 / disc;
                                    rr_value += Float.parseFloat(String.valueOf((char) datas[i])) * disc;
                                }

                                rr_lock += Float.parseFloat(String.valueOf((char) datas[6]));

                                RR_Sensor.setText(String.format("%.1f",rr_value));
                                RR_Lock.setText("" + (int)rr_lock);
                                break;
                            default:
                                break;
                        }
                    }
                }
                Log.d(TAG,"Read Data -> " + s);

                if(HomeActivity.terminalLayout.getVisibility() == View.VISIBLE) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            byte[] datas = bluetoothGattCharacteristic.getValue();
                            String s = "";
                            for(byte b : datas){
                                s += (char)b;
                            }
                            HomeActivity.terminal_logField.setText(HomeActivity.terminal_logField.getText() +
                                    "\bReceive Data\b --------------------------\n" +
                                    s +
                                    "\n");

                        }
                    });
                }
            }

            @Override
            public void readRssi(BluetoothGatt bluetoothGatt, int i, int i1) {

            }
        });
    }

    private void SetTimer(){
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    if(status == stop){
                        bluetoothGeneralTool.Write(STOP);
                        status = release;
                        Thread.sleep(200);
                    }
                    if(status == up){
                        switch (Target){
                            case FL:
                                bluetoothGeneralTool.Write(FL_UP);
                                break;
                            case FR:
                                bluetoothGeneralTool.Write(FR_UP);
                                break;
                            case RL:
                                bluetoothGeneralTool.Write(RL_UP);
                                break;
                            case RR:
                                bluetoothGeneralTool.Write(RR_UP);
                                break;
                            default:
                                break;
                        }
                        Thread.sleep(200);
                    }
                    if(status == down){
                        switch (Target){
                            case FL:
                                bluetoothGeneralTool.Write(FL_DOWN);
                                break;
                            case FR:
                                bluetoothGeneralTool.Write(FR_DOWN);
                                break;
                            case RL:
                                bluetoothGeneralTool.Write(RL_DOWN);
                                break;
                            case RR:
                                bluetoothGeneralTool.Write(RR_DOWN);
                                break;
                            default:
                                break;
                        }
                        Thread.sleep(200);
                    }

                    bluetoothGeneralTool.Write(READ_SENSOR1);
                    Thread.sleep(200);
                    if(status == stop){
                        bluetoothGeneralTool.Write(STOP);
                        status = release;
                        Thread.sleep(200);
                    }
                    bluetoothGeneralTool.Write(READ_SENSOR2);
                    Thread.sleep(200);
                    if(status == stop){
                        bluetoothGeneralTool.Write(STOP);
                        status = release;
                        Thread.sleep(200);
                    }
                    bluetoothGeneralTool.Write(READ_SENSOR3);
                    Thread.sleep(200);
                    if(status == stop){
                        bluetoothGeneralTool.Write(STOP);
                        status = release;
                        Thread.sleep(200);
                    }
                    bluetoothGeneralTool.Write(READ_SENSOR4);
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    public void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }
}